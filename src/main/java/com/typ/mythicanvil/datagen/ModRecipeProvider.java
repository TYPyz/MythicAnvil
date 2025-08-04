package com.typ.mythicanvil.datagen;

import com.typ.mythicanvil.block.ModBlocks;
import com.typ.mythicanvil.item.ModItems;
import com.typ.mythicanvil.recipe.InWorldCraftingRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // Example recipe: Throw an apple and iron ingot onto the mythic anvil,
        // then right-click with the trigger item to get a diamond
        InWorldCraftingRecipeBuilder.create(
                new ItemStack(Items.DIAMOND),
                ModBlocks.MYTHIC_ANVIL.get().defaultBlockState(),
                Ingredient.of(ModItems.TRIGGER.get())
        )
        .addThrownIngredient(Ingredient.of(Items.APPLE))
        .addThrownIngredient(Ingredient.of(Items.IRON_INGOT))
        .unlockedBy("has_trigger", has(ModItems.TRIGGER.get()))
        .unlockedBy("has_apple", has(Items.APPLE))
        .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
        .save(output);

        // Another example: Throw a stick onto dirt to get coal
        InWorldCraftingRecipeBuilder.create(
                new ItemStack(Items.COAL),
                Blocks.DIRT.defaultBlockState(),
                Ingredient.of(ModItems.TRIGGER.get())
        )
        .addThrownIngredient(Ingredient.of(Items.STICK))
        .unlockedBy("has_trigger", has(ModItems.TRIGGER.get()))
        .unlockedBy("has_stick", has(Items.STICK))
        .save(output);
    }
}
