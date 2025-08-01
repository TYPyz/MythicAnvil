package com.typ.mythicanvil.ritual;

import com.typ.mythicanvil.block.ModBlocks;
import com.typ.mythicanvil.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class RitualRecipeManager {
    private static final List<RitualRecipe> recipes = new ArrayList<>();

    public static void registerRecipes() {
        // Recipe: cobblestone + coal on mythic anvil with trigger item = diamond
        recipes.add(new RitualRecipe(
                List.of(new ItemStack(Items.COBBLESTONE), new ItemStack(Items.COAL)),
                new ItemStack(Items.DIAMOND),
                ModBlocks.MYTHIC_ANVIL.get(),
                ModItems.TRIGGER.get()
        ));
        // Here I'll add more recipes as needed
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
