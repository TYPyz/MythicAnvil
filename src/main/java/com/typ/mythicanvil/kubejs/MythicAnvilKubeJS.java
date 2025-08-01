package com.typ.mythicanvil.kubejs;

import com.typ.mythicanvil.ritual.RitualRecipe;
import com.typ.mythicanvil.ritual.RitualRecipeManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * KubeJS integration for Mythic Anvil ritual recipes
 * This class provides static methods that can be called from KubeJS scripts
 */
public class MythicAnvilKubeJS {

    /**
     * Add a ritual recipe
     * @param inputs Array of input items (strings like "minecraft:iron_ingot" or "minecraft:iron_ingot*5")
     * @param output Output item (string like "minecraft:diamond" or "minecraft:diamond*2")
     * @param ritualBlock The block used for the ritual (string like "mythicanvil:mythic_anvil")
     * @param activationItem The item used to activate the ritual (string like "mythicanvil:trigger")
     */
    public static void addRecipe(String[] inputs, String output, String ritualBlock, String activationItem) {
        try {
            List<ItemStack> inputItems = new ArrayList<>();

            // Process input items
            for (String input : inputs) {
                ItemStack stack = parseItemStack(input);
                if (stack != null) {
                    inputItems.add(stack);
                } else {
                    throw new IllegalArgumentException("Invalid input item: " + input);
                }
            }

            // Process output
            ItemStack outputStack = parseItemStack(output);
            if (outputStack == null) {
                throw new IllegalArgumentException("Invalid output item: " + output);
            }

            // Process ritual block
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(ritualBlock));
            if (block == null) {
                throw new IllegalArgumentException("Invalid ritual block: " + ritualBlock);
            }

            // Process activation item
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(activationItem));
            if (item == null) {
                throw new IllegalArgumentException("Invalid activation item: " + activationItem);
            }

            // Create and register the recipe
            RitualRecipe recipe = new RitualRecipe(inputItems, outputStack, block, item);
            RitualRecipeManager.addRecipe(recipe);

            System.out.println("Added KubeJS ritual recipe: " + inputs.length + " inputs -> " + output);

        } catch (Exception e) {
            System.err.println("Failed to add ritual recipe via KubeJS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ItemStack parseItemStack(String str) {
        // Handle format like "3x minecraft:iron_ingot" or just "minecraft:iron_ingot"
        String[] parts = str.split("x", 2); // Split into max 2 parts

        if (parts.length == 2) {
            // Format: "3x minecraft:iron_ingot"
            try {
                int count = Integer.parseInt(parts[0].trim());
                String itemId = parts[1].trim();

                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
                if (item != null) {
                    return new ItemStack(item, count);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid count format in: " + str);
                return null;
            }
        } else {
            // Format: "minecraft:iron_ingot" (count = 1)
            String itemId = str.trim();
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
            if (item != null) {
                return new ItemStack(item, 1);
            }
        }

        return null;
    }

    /**
     * Clear all ritual recipes (useful for reloading)
     */
    public static void clearRecipes() {
        RitualRecipeManager.clearRecipes();
    }
}
