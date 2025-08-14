package com.typ.mythicanvil.item.custom;

import com.typ.mythicanvil.recipe.RitualRecipe;
import com.typ.mythicanvil.recipe.handler.RitualCraftingHandler;
import com.typ.mythicanvil.recipe.input.RitualRecipeInput;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RitualDebuggerItem extends Item {

    public RitualDebuggerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();

        if (player == null || level.isClientSide()) {
            return InteractionResult.PASS;
        }

        ItemStack triggerItem = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        BlockState targetBlock = level.getBlockState(pos);

        // Get nearby items
        AABB searchArea = new AABB(pos).inflate(1.5);
        List<ItemEntity> nearbyItems = level.getEntitiesOfClass(ItemEntity.class, searchArea);
        List<ItemStack> thrownItems = nearbyItems.stream()
                .map(ItemEntity::getItem)
                .toList();

        player.sendSystemMessage(Component.literal("=== RITUAL DEBUG ANALYSIS ===").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal("Target Block: " + targetBlock.getBlock().getName().getString()).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.literal("Trigger Item: " + triggerItem.getDisplayName().getString()).withStyle(ChatFormatting.AQUA));

        player.sendSystemMessage(Component.literal("Thrown Items (" + thrownItems.size() + "):").withStyle(ChatFormatting.GREEN));
        if (thrownItems.isEmpty()) {
            player.sendSystemMessage(Component.literal("  - No items found on the ground").withStyle(ChatFormatting.RED));
        } else {
            for (int i = 0; i < thrownItems.size(); i++) {
                ItemStack stack = thrownItems.get(i);
                player.sendSystemMessage(Component.literal("  " + (i + 1) + ". " + stack.getDisplayName().getString() + " x" + stack.getCount()).withStyle(ChatFormatting.WHITE));
            }
        }

        // Check if ritual recipe manager exists
        if (RitualCraftingHandler.getRitualRecipeManager() == null) {
            player.sendSystemMessage(Component.literal("ERROR: Ritual Recipe Manager not initialized!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            return InteractionResult.SUCCESS;
        }

        // Create recipe input
        RitualRecipeInput input = new RitualRecipeInput(targetBlock, triggerItem, thrownItems);

        // Find matching recipes - only check recipes for this specific target block
        Collection<RitualRecipe> allRecipesCollection = RitualCraftingHandler.getRitualRecipeManager().getAllRecipes();
        List<RitualRecipe> targetBlockRecipes = allRecipesCollection.stream()
                .filter(recipe -> recipe.getTargetBlock().getBlock().equals(targetBlock.getBlock()))
                .toList();

        player.sendSystemMessage(Component.literal("Found " + targetBlockRecipes.size() + " recipes for target block: " + targetBlock.getBlock().getName().getString()).withStyle(ChatFormatting.AQUA));

        if (targetBlockRecipes.isEmpty()) {
            player.sendSystemMessage(Component.literal("✗ NO RECIPES FOUND FOR THIS BLOCK").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("This block is not used in any ritual recipes.").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(Component.literal("=== END DEBUG ANALYSIS ===").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            return InteractionResult.SUCCESS;
        }

        Optional<RitualRecipe> matchingRecipe = targetBlockRecipes.stream()
                .filter(recipe -> recipe.matches(input, level))
                .findFirst();

        if (matchingRecipe.isPresent()) {
            RitualRecipe recipe = matchingRecipe.get();
            player.sendSystemMessage(Component.literal("✓ MATCHING RECIPE FOUND!").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            player.sendSystemMessage(Component.literal("Result: " + recipe.getResult().getDisplayName().getString() + " x" + recipe.getResult().getCount()).withStyle(ChatFormatting.LIGHT_PURPLE));
            player.sendSystemMessage(Component.literal("Consumes Trigger: " + (recipe.shouldConsumeTrigger() ? "YES" : "NO")).withStyle(recipe.shouldConsumeTrigger() ? ChatFormatting.RED : ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("Lightning Strike: " + (recipe.shouldStrikeLightning() ? "YES" : "NO")).withStyle(recipe.shouldStrikeLightning() ? ChatFormatting.YELLOW : ChatFormatting.GRAY));

            player.sendSystemMessage(Component.literal("Required Ingredients:").withStyle(ChatFormatting.BLUE));
            for (int i = 0; i < recipe.getThrownItems().size(); i++) {
                player.sendSystemMessage(Component.literal("  " + (i + 1) + ". " + recipe.getThrownItems().get(i).toString()).withStyle(ChatFormatting.WHITE));
            }
        } else {
            player.sendSystemMessage(Component.literal("✗ NO MATCHING RECIPE FOUND").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));

            // Show detailed analysis of why recipes don't match (only for this target block)
            player.sendSystemMessage(Component.literal("Analyzing why recipes for this block don't match:").withStyle(ChatFormatting.YELLOW));

            for (int i = 0; i < targetBlockRecipes.size(); i++) {
                RitualRecipe recipe = targetBlockRecipes.get(i);
                player.sendSystemMessage(Component.literal("Recipe " + (i + 1) + " (Result: " + recipe.getResult().getDisplayName().getString() + "):").withStyle(ChatFormatting.GRAY));

                // We know target block matches, so check trigger item
                if (!recipe.getTriggerItem().test(triggerItem)) {
                    player.sendSystemMessage(Component.literal("  ✗ Trigger item mismatch: Expected " + recipe.getTriggerItem().toString()).withStyle(ChatFormatting.RED));
                    continue;
                }

                // Check thrown items count
                List<ItemStack> expandedInputItems = expandItemStacks(thrownItems);
                if (expandedInputItems.size() != recipe.getThrownItems().size()) {
                    player.sendSystemMessage(Component.literal("  ✗ Item count mismatch: Expected " + recipe.getThrownItems().size() + ", got " + expandedInputItems.size()).withStyle(ChatFormatting.RED));
                    continue;
                }

                player.sendSystemMessage(Component.literal("  ✗ Ingredient matching failed").withStyle(ChatFormatting.RED));
            }
        }

        player.sendSystemMessage(Component.literal("=== END DEBUG ANALYSIS ===").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        return InteractionResult.SUCCESS;
    }

    private List<ItemStack> expandItemStacks(List<ItemStack> inputStacks) {
        return inputStacks.stream()
                .flatMap(stack -> {
                    return java.util.stream.IntStream.range(0, stack.getCount())
                            .mapToObj(i -> {
                                ItemStack singleItem = stack.copy();
                                singleItem.setCount(1);
                                return singleItem;
                            });
                })
                .toList();
    }
}
