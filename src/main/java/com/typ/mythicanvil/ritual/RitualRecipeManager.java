package com.typ.mythicanvil.ritual;

import com.typ.mythicanvil.block.ModBlocks;
import com.typ.mythicanvil.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.ArrayList;
import java.util.List;

public class RitualRecipeManager {
    private static final List<RitualRecipe> recipes = new ArrayList<>();

    public static void registerRecipes() {
        // Recipe: cobblestone + coal on mythic anvil with trigger item = diamond
//        recipes.add(new RitualRecipe(
//                List.of(new ItemStack(Items.COBBLESTONE), new ItemStack(Items.COAL)),
//                new ItemStack(Items.DIAMOND),
//                ModBlocks.MYTHIC_ANVIL.get(),
//                ModItems.TRIGGER.get()
//        ));
        // Here I'll add more recipes as needed
    }

    // Method for KubeJS to add recipes dynamically
    public static void addRecipe(RitualRecipe recipe) {
        recipes.add(recipe);
    }

    // Method to clear all recipes (useful for reloading)
    public static void clearRecipes() {
        recipes.clear();
    }

    public static RitualRecipe findRecipe(Block block, Item activationItem) {
        for (RitualRecipe recipe : recipes) {
            if (recipe.matches(block, activationItem)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Find a recipe that matches the given block, activation item, and available items
     * This method checks if all required items are available in the correct quantities
     * Optimized version that pre-calculates available item counts for better performance
     */
    public static RitualRecipe findMatchingRecipe(Block block, Item activationItem, List<ItemEntity> availableItems) {
        // Pre-calculate available item counts for optimization
        java.util.Map<Item, Integer> availableItemCounts = new java.util.HashMap<>();
        for (ItemEntity entity : availableItems) {
            Item item = entity.getItem().getItem();
            int count = entity.getItem().getCount();
            availableItemCounts.merge(item, count, Integer::sum);
        }

        // Find matching recipes - check availability immediately to fail fast
        for (RitualRecipe recipe : recipes) {
            if (recipe.matches(block, activationItem) && canSatisfyRecipeOptimized(recipe, availableItemCounts)) {
                return recipe;
            }
        }

        return null;
    }

    /**
     * Optimized version that uses pre-calculated item counts
     */
    private static boolean canSatisfyRecipeOptimized(RitualRecipe recipe, java.util.Map<Item, Integer> availableItemCounts) {
        // Group required items by type and sum their counts
        java.util.Map<Item, Integer> requiredItemCounts = new java.util.HashMap<>();
        for (ItemStack required : recipe.getInputItems()) {
            Item item = required.getItem();
            int count = required.getCount();
            requiredItemCounts.merge(item, count, Integer::sum);
        }

        // Check if we have enough of each required item type
        for (java.util.Map.Entry<Item, Integer> entry : requiredItemCounts.entrySet()) {
            Item requiredItem = entry.getKey();
            int requiredCount = entry.getValue();
            int availableCount = availableItemCounts.getOrDefault(requiredItem, 0);

            if (availableCount < requiredCount) {
                return false;
            }
        }

        return true;
    }

    /**
     * Quick check if any recipe uses the given activation item
     * This is used for early performance optimization
     */
    public static boolean hasRecipeWithActivationItem(Item activationItem) {
        for (RitualRecipe recipe : recipes) {
            if (recipe.getActivationItem().equals(activationItem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Quick check if any recipe uses the given block and activation item combination
     * This is used for early performance optimization
     */
    public static boolean hasRecipeWithBlockAndActivationItem(Block block, Item activationItem) {
        for (RitualRecipe recipe : recipes) {
            if (recipe.matches(block, activationItem)) {
                return true;
            }
        }
        return false;
    }

    public static List<RitualRecipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }
}
