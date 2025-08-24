package com.typ.mythicanvil.item.custom;

import com.typ.mythicanvil.recipe.RitualRecipe;
import com.typ.mythicanvil.recipe.ModRecipeTypes;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

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

        // Create recipe input
        RitualRecipeInput input = new RitualRecipeInput(targetBlock, triggerItem, thrownItems);

        // Get all ritual recipes from vanilla recipe manager
        List<RecipeHolder<RitualRecipe>> allRitualRecipes = level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.RITUAL_TYPE.get());

        // Find matching recipes - only check recipes for this specific target block
        List<RitualRecipe> targetBlockRecipes = allRitualRecipes.stream()
                .map(RecipeHolder::value)
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
                Ingredient ingredient = recipe.getThrownItems().get(i);
                ItemStack[] stacks = ingredient.getItems();
                if (stacks.length > 0) {
                    player.sendSystemMessage(Component.literal("  " + (i + 1) + ". " + stacks[0].getDisplayName().getString()).withStyle(ChatFormatting.WHITE));
                }
            }
        } else {
            player.sendSystemMessage(Component.literal("✗ NO MATCHING RECIPE").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            
            // Show why each recipe didn't match
            player.sendSystemMessage(Component.literal("Analysis of why recipes don't match:").withStyle(ChatFormatting.YELLOW));
            for (int i = 0; i < targetBlockRecipes.size(); i++) {
                RitualRecipe recipe = targetBlockRecipes.get(i);
                player.sendSystemMessage(Component.literal("Recipe " + (i + 1) + ":").withStyle(ChatFormatting.AQUA));
                
                // Check trigger item
                if (!recipe.getTriggerItem().test(triggerItem)) {
                    player.sendSystemMessage(Component.literal("  ✗ Trigger item mismatch").withStyle(ChatFormatting.RED));
                    Ingredient triggerIngredient = recipe.getTriggerItem();
                    ItemStack[] validTriggers = triggerIngredient.getItems();
                    if (validTriggers.length > 0) {
                        player.sendSystemMessage(Component.literal("    Expected: " + validTriggers[0].getDisplayName().getString()).withStyle(ChatFormatting.GRAY));
                    }
                } else {
                    player.sendSystemMessage(Component.literal("  ✓ Trigger item matches").withStyle(ChatFormatting.GREEN));
                }
                
                // Check thrown items count
                if (recipe.getThrownItems().size() != thrownItems.size()) {
                    player.sendSystemMessage(Component.literal("  ✗ Item count mismatch: expected " + recipe.getThrownItems().size() + ", found " + thrownItems.size()).withStyle(ChatFormatting.RED));
                } else {
                    player.sendSystemMessage(Component.literal("  ✓ Item count matches").withStyle(ChatFormatting.GREEN));
                }
            }
        }

        player.sendSystemMessage(Component.literal("=== END DEBUG ANALYSIS ===").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        return InteractionResult.SUCCESS;
    }
}
