package com.typ.mythicanvil.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InWorldCraftingRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final BlockState targetBlock;
    private final Ingredient triggerItem;
    private final List<Ingredient> thrownIngredients = new ArrayList<>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    public InWorldCraftingRecipeBuilder(ItemStack result, BlockState targetBlock, Ingredient triggerItem) {
        this.result = result;
        this.targetBlock = targetBlock;
        this.triggerItem = triggerItem;
    }

    public static InWorldCraftingRecipeBuilder create(ItemStack result, BlockState targetBlock, Ingredient triggerItem) {
        return new InWorldCraftingRecipeBuilder(result, targetBlock, triggerItem);
    }

    public InWorldCraftingRecipeBuilder addThrownIngredient(Ingredient ingredient) {
        this.thrownIngredients.add(ingredient);
        return this;
    }

    @Override
    @NotNull
    public InWorldCraftingRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    @NotNull
    public InWorldCraftingRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    @NotNull
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(@NotNull RecipeOutput output, @NotNull ResourceLocation id) {
        // Build the advancement
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        // Create the recipe
        InWorldCraftingRecipe recipe = new InWorldCraftingRecipe(
                this.targetBlock,
                this.triggerItem,
                this.thrownIngredients,
                this.result
        );

        // Pass the recipe to the output
        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/")));
    }
}
