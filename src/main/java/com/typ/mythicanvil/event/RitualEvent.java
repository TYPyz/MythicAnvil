package com.typ.mythicanvil.event;

import com.typ.mythicanvil.ritual.RitualRecipe;
import com.typ.mythicanvil.ritual.RitualRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class RitualEvent {

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        ItemStack heldItem = player.getItemInHand(event.getHand());

        // Only process on server side
        if (level.isClientSide()) {
            return;
        }

        // Early performance checks - exit quickly if no recipe could possibly match

        // First check: Is there any recipe with this activation item?
        boolean hasMatchingActivationItem = RitualRecipeManager.hasRecipeWithActivationItem(heldItem.getItem());
        if (!hasMatchingActivationItem) {
            return; // No recipe uses this activation item, exit early
        }

        // Second check: Is there any recipe with this ritual block AND activation item combination?
        boolean hasMatchingBlockAndItem = RitualRecipeManager.hasRecipeWithBlockAndActivationItem(state.getBlock(), heldItem.getItem());
        if (!hasMatchingBlockAndItem) {
            return; // No recipe uses this block + activation item combination, exit early
        }

        // Both checks passed, proceed with expensive operations

        // Get available items first
        BlockPos abovePos = pos.above();
        AABB searchArea = new AABB(abovePos).inflate(1.0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchArea);

        // Find recipe based on block, activation item, AND available items
        RitualRecipe recipe = RitualRecipeManager.findMatchingRecipe(state.getBlock(), heldItem.getItem(), items);
        if (recipe == null) {
            // Play broken tool sound when no recipe is found
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 0.6F);
            return;
        }

        // Check if we have all required items (allowing extras)
        List<ItemStack> requiredItems = new ArrayList<>(recipe.getInputItems());
        List<ItemEntity> toConsume = new ArrayList<>();
        List<Integer> consumeAmounts = new ArrayList<>();

        for (ItemStack required : requiredItems) {
            int needed = required.getCount();

            for (ItemEntity entity : items) {
                if (needed <= 0) break;

                ItemStack stack = entity.getItem();
                if (stack.is(required.getItem())) {
                    int available = stack.getCount();

                    // Check if we've already planned to consume from this entity
                    int alreadyPlanned = 0;
                    for (int i = 0; i < toConsume.size(); i++) {
                        if (toConsume.get(i) == entity) {
                            alreadyPlanned += consumeAmounts.get(i);
                        }
                    }

                    int actuallyAvailable = available - alreadyPlanned;
                    if (actuallyAvailable > 0) {
                        int toTake = Math.min(needed, actuallyAvailable);

                        toConsume.add(entity);
                        consumeAmounts.add(toTake);
                        needed -= toTake;

                        if (needed <= 0) break;
                    }
                }
            }

            if (needed > 0) {
                return;
            }
        }

        // Consume the items
        for (int i = 0; i < toConsume.size(); i++) {
            ItemEntity entity = toConsume.get(i);
            int amount = consumeAmounts.get(i);
            ItemStack stack = entity.getItem();

            if (stack.getCount() <= amount) {
                entity.discard();
            } else {
                stack.shrink(amount);
                entity.setItem(stack);
            }
        }

        // Strike lightning at the ritual block position
        net.minecraft.world.entity.LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
        if (lightning != null) {
            lightning.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            lightning.setVisualOnly(true); // This prevents the lightning from causing fire/damage
            level.addFreshEntity(lightning);
        }

        // Create output item after a small delay (using scheduler)
        level.scheduleTick(pos, state.getBlock(), 10); // 10 ticks = 0.5 seconds delay

        // Store the recipe output in a temporary way - we'll create the item immediately for now
        // but you could implement a more sophisticated delay system if needed
        level.getServer().execute(() -> {
            // Create output item with slight delay
            ItemEntity outputEntity = new ItemEntity(level,
                    abovePos.getX() + 0.5,
                    abovePos.getY() + 0.1,
                    abovePos.getZ() + 0.5,
                    recipe.getOutputItem().copy());

            outputEntity.setDeltaMovement(0, 0.2, 0);
            level.addFreshEntity(outputEntity);
        });

        // Play sound
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ANVIL_USE,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}