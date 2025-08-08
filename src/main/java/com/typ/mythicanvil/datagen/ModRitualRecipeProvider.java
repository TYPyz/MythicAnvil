package com.typ.mythicanvil.datagen;

import com.typ.mythicanvil.MythicAnvil;
import com.typ.mythicanvil.recipe.RitualRecipe;
import com.typ.mythicanvil.recipe.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRitualRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRitualRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Create a custom RecipeOutput that saves to the ritual folder
        RecipeOutput ritualOutput = new RitualRecipeOutput(recipeOutput);

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

        ritualOutput.accept(
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

        ritualOutput.accept(
            ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID, "stone_to_pickaxe"),
            stoneToPickaxe,
            null
        );
    }

    // Custom RecipeOutput that modifies the path to save to ritual folder
    private static class RitualRecipeOutput implements RecipeOutput {
        private final RecipeOutput delegate;

        public RitualRecipeOutput(RecipeOutput delegate) {
            this.delegate = delegate;
        }

        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, net.minecraft.advancements.AdvancementHolder advancement, net.neoforged.neoforge.common.conditions.ICondition... conditions) {
            // Modify the path to save in ritual folder instead of recipes
            ResourceLocation ritualId = ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                id.getPath().replace("recipes/", "ritual/")
            );

            delegate.accept(ritualId, recipe, advancement, conditions);
        }

        @Override
        public net.minecraft.advancements.Advancement.Builder advancement() {
            return delegate.advancement();
        }
    }
}
