package com.typ.mythicanvil.ritual;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class RitualRecipeManager {
    private static final List<RitualRecipe> recipes = new ArrayList<>();

    public static void registerRecipes() {
        // Original recipe: cobblestone + coal on grass block with stick = diamond
        recipes.add(new RitualRecipe(
                List.of(new ItemStack(Items.COBBLESTONE), new ItemStack(Items.COAL)),
                new ItemStack(Items.DIAMOND),
                Blocks.GRASS_BLOCK,
                Items.STICK
        ));

        // Example additional recipe: iron + redstone on stone with blaze rod = gold
        recipes.add(new RitualRecipe(
                List.of(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.REDSTONE)),
                new ItemStack(Items.GOLD_INGOT),
                Blocks.STONE,
                Items.BLAZE_ROD
        ));

        // Example recipe: diamond + emerald on obsidian with golden apple = netherite
        recipes.add(new RitualRecipe(
                List.of(new ItemStack(Items.DIAMOND), new ItemStack(Items.EMERALD)),
                new ItemStack(Items.NETHERITE_INGOT),
                Blocks.OBSIDIAN,
                Items.GOLDEN_APPLE
        ));
    }

    public static RitualRecipe findRecipe(Block block, Item activationItem) {
        for (RitualRecipe recipe : recipes) {
            if (recipe.matches(block, activationItem)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<RitualRecipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }
}
