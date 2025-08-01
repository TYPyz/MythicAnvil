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

        System.out.println("Right-clicked block: " + state.getBlock());
        System.out.println("Held item: " + heldItem.getItem());

        RitualRecipe recipe = RitualRecipeManager.findRecipe(state.getBlock(), heldItem.getItem());
        if (recipe == null) {
            System.out.println("No recipe found for this combination");
            return;
        }

        System.out.println("Found matching recipe!");

        BlockPos abovePos = pos.above();
        AABB searchArea = new AABB(abovePos).inflate(1.0);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchArea);

        System.out.println("Found " + items.size() + " items above the block");
        for (ItemEntity item : items) {
            System.out.println("Item: " + item.getItem().getItem() + " Count: " + item.getItem().getCount());
        }

        // Check if we have all required items (allowing extras)
        List<ItemStack> requiredItems = new ArrayList<>(recipe.getInputItems());
        List<ItemEntity> toConsume = new ArrayList<>();
        List<Integer> consumeAmounts = new ArrayList<>();

        for (ItemStack required : requiredItems) {
            int needed = required.getCount();
            System.out.println("Looking for " + needed + " of " + required.getItem());

            for (ItemEntity entity : items) {
                if (needed <= 0) break;

                ItemStack stack = entity.getItem();
                if (stack.is(required.getItem())) {
                    int available = stack.getCount();
                    int toTake = Math.min(needed, available);

                    toConsume.add(entity);
                    consumeAmounts.add(toTake);
                    needed -= toTake;

                    System.out.println("Found " + toTake + " of " + required.getItem() + ", still need " + needed);

                    if (needed <= 0) break;
                }
            }

            if (needed > 0) {
                System.out.println("Not enough " + required.getItem() + " - missing " + needed);
                return;
            }
        }

        System.out.println("All required items found! Executing ritual...");

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

        // Create output item
        ItemEntity outputEntity = new ItemEntity(level,
                abovePos.getX() + 0.5,
                abovePos.getY() + 0.1,
                abovePos.getZ() + 0.5,
                recipe.getOutputItem().copy());

        outputEntity.setDeltaMovement(0, 0.2, 0);
        level.addFreshEntity(outputEntity);

        // Play sound
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ANVIL_USE,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        System.out.println("Ritual completed successfully!");
    }
}