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

        // Find a matching recipe
        RitualRecipe recipe = RitualRecipeManager.findRecipe(state.getBlock(), heldItem.getItem());
        if (recipe == null) {
            return;
        }

        // Get the position above the ritual block
        BlockPos abovePos = pos.above();

        // Check for items in the area above the block
        AABB searchArea = new AABB(abovePos).inflate(0.5);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchArea);

        // Check if we have all the required input items
        List<ItemEntity> foundItems = new ArrayList<>();
        List<ItemStack> requiredItems = new ArrayList<>(recipe.getInputItems());

        for (ItemEntity itemEntity : items) {
            ItemStack itemStack = itemEntity.getItem();

            // Check if this item matches any of the required items
            for (int i = 0; i < requiredItems.size(); i++) {
                ItemStack required = requiredItems.get(i);
                if (itemStack.is(required.getItem()) && itemStack.getCount() >= required.getCount()) {
                    foundItems.add(itemEntity);
                    requiredItems.remove(i);
                    break;
                }
            }
        }

        // If we have all required items, perform the ritual
        if (requiredItems.isEmpty()) {
            // Remove the input items
            for (ItemEntity itemEntity : foundItems) {
                itemEntity.discard();
            }

            // Create and drop the output item
            ItemEntity outputEntity = new ItemEntity(level,
                    abovePos.getX() + 0.5,
                    abovePos.getY() + 0.5,
                    abovePos.getZ() + 0.5,
                    recipe.getOutputItem());
            level.addFreshEntity(outputEntity);

            // Play a sound effect
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ANVIL_USE,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

            // Cancel the event to prevent normal interaction
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}