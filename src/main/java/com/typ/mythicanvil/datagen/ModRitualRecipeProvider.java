package com.typ.mythicanvil.datagen;

import com.typ.mythicanvil.MythicAnvil;
import com.typ.mythicanvil.recipe.RitualRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRitualRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRitualRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        // Override the PackOutput to force the correct path
        super(new PackOutput(output.getOutputFolder()) {
            @Override
            public PackOutput.PathProvider createPathProvider(PackOutput.Target target, String path) {
                // Intercept recipe generation and force "recipes" folder
                if ("recipe".equals(path)) {
                    return super.createPathProvider(target, "recipes");
                }
                return super.createPathProvider(target, path);
            }
        }, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Example ritual recipe: Right-click grass block with stick while having dirt nearby
        // Results in a diamond - consumes trigger item and strikes lightning (default behavior)
        RitualRecipe grassToDiamond = new RitualRecipe(
            Blocks.GRASS_BLOCK.defaultBlockState(),
            Ingredient.of(Items.STICK),
            List.of(Ingredient.of(Items.DIRT)),
            new ItemStack(Items.DIAMOND),
            true, // consume trigger item
            true  // strike lightning
        );

        recipeOutput.accept(
            ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID, "grass_to_diamond"),
            grassToDiamond,
            null
        );

        // Example ritual recipe: Right-click stone with apple while having coal and iron nearby
        // Results in an iron pickaxe - does NOT consume trigger item and NO lightning (silent ritual)
        RitualRecipe stoneToPickaxe = new RitualRecipe(
            Blocks.STONE.defaultBlockState(),
            Ingredient.of(Items.APPLE),
            List.of(Ingredient.of(Items.COAL), Ingredient.of(Items.IRON_INGOT)),
            new ItemStack(Items.IRON_PICKAXE),
            false, // don't consume trigger item
            false  // don't strike lightning
        );

        recipeOutput.accept(
            ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID, "stone_to_pickaxe"),
            stoneToPickaxe,
            null
        );
    }
}
