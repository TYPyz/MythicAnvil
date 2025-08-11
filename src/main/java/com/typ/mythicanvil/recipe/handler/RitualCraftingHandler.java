package com.typ.mythicanvil.recipe.handler;

import com.typ.mythicanvil.recipe.RitualRecipe;
import com.typ.mythicanvil.recipe.input.RitualRecipeInput;
import com.typ.mythicanvil.recipe.manager.RitualRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = "mythicanvil")
public class RitualCraftingHandler {
    private static RitualRecipeManager ritualRecipeManager;

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        ritualRecipeManager = new RitualRecipeManager();
        event.addListener(ritualRecipeManager);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        InteractionHand hand = event.getHand();

        // Only process main hand interactions on the server side
        if (hand != InteractionHand.MAIN_HAND || level.isClientSide() || ritualRecipeManager == null) {
            return;
        }

        ItemStack triggerItem = player.getItemInHand(hand);
        BlockState targetBlock = level.getBlockState(pos);

        // First check: Does the trigger item have any ritual recipes?
        if (!ritualRecipeManager.hasRecipesForTriggerItem(triggerItem)) {
            return;
        }

        // Second check: Does the target block have any ritual recipes?
        if (!ritualRecipeManager.hasRecipesForTargetBlock(targetBlock)) {
            return;
        }

        // Both checks passed, now look for items on the ground
        List<ItemEntity> nearbyItems = getNearbyItems(level, pos);
        List<ItemStack> thrownItems = nearbyItems.stream()
                .map(ItemEntity::getItem)
                .toList();

        // Create recipe input
        RitualRecipeInput input = new RitualRecipeInput(targetBlock, triggerItem, thrownItems);

        // Try to find a matching recipe
        Optional<RitualRecipe> matchingRecipe = ritualRecipeManager.getAllRecipes().stream()
                .filter(recipe -> recipe.matches(input, level))
                .findFirst();

        if (matchingRecipe.isPresent()) {
            performRitualCrafting(matchingRecipe.get(), input, (ServerLevel) level, pos, nearbyItems, player);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        } else {
            // No matching recipe found - play breaking tool sound to indicate failure
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                           net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static List<ItemEntity> getNearbyItems(Level level, BlockPos pos) {
        // Look for items in a 3x3x3 area around the block
        AABB searchArea = new AABB(pos).inflate(1.5);
        return level.getEntitiesOfClass(ItemEntity.class, searchArea);
    }

    private static void performRitualCrafting(RitualRecipe recipe, RitualRecipeInput input,
                                            ServerLevel level, BlockPos pos,
                                            List<ItemEntity> itemEntities, Player player) {
        // Get the result
        ItemStack result = recipe.assemble(input, level.registryAccess());

        if (!result.isEmpty()) {
            // Remove the consumed items from the world
            for (ItemEntity itemEntity : itemEntities) {
                itemEntity.discard();
            }

            // Conditionally consume the trigger item based on recipe setting
            if (recipe.shouldConsumeTrigger() && !player.isCreative()) {
                input.triggerItem().shrink(1);
            }
            player.swing(InteractionHand.MAIN_HAND); // Simulate player swing
            // Conditionally strike lightning based on recipe setting
            if (recipe.shouldStrikeLightning()) {
                net.minecraft.world.entity.LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
                if (lightning != null) {
                    lightning.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    lightning.setVisualOnly(true); // Makes it visual only - won't cause fire or damage
                    level.addFreshEntity(lightning);
                }
            }

            // Drop the result at the block position
            ItemEntity resultEntity = new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    result);
            level.addFreshEntity(resultEntity);

            // Optional: Add some visual/audio effects here
            // You could play sounds, spawn particles, etc.
        }
    }

    public static RitualRecipeManager getRitualRecipeManager() {
        return ritualRecipeManager;
    }
}
