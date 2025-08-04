package com.typ.mythicanvil.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InWorldCraftingRecipe implements Recipe<InWorldCraftingInput> {
    private final BlockState targetBlock;
    private final Ingredient triggerItem;
    private final List<Ingredient> thrownIngredients;
    private final ItemStack result;

    public InWorldCraftingRecipe(BlockState targetBlock, Ingredient triggerItem, List<Ingredient> thrownIngredients, ItemStack result) {
        this.targetBlock = targetBlock;
        this.triggerItem = triggerItem;
        this.thrownIngredients = thrownIngredients;
        this.result = result;
    }

    // Check whether the given input matches this recipe
    @Override
    public boolean matches(@NotNull InWorldCraftingInput input, @NotNull Level level) {
        // Check if the target block matches
        if (!this.targetBlock.equals(input.targetBlock())) {
            return false;
        }

        // Check if the trigger item matches
        if (!this.triggerItem.test(input.triggerItem())) {
            return false;
        }

        // Check if we have the right number of thrown items
        if (this.thrownIngredients.size() != input.thrownItems().size()) {
            return false;
        }

        // Check if all thrown ingredients match (order doesn't matter)
        List<ItemStack> remainingItems = new ArrayList<>(input.thrownItems());

        for (Ingredient ingredient : this.thrownIngredients) {
            boolean found = false;
            for (int i = 0; i < remainingItems.size(); i++) {
                if (ingredient.test(remainingItems.get(i))) {
                    remainingItems.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    // Return the result of the recipe
    @Override
    @NotNull
    public ItemStack assemble(@NotNull InWorldCraftingInput input, @NotNull HolderLookup.Provider registries) {
        return this.result.copy();
    }

    // This recipe shouldn't appear in recipe book as it's an in-world mechanic
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true; // Not applicable for in-world crafting
    }

    @Override
    @NotNull
    public ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    @NotNull
    public RecipeType<? extends Recipe<InWorldCraftingInput>> getType() {
        return ModRecipeTypes.IN_WORLD_CRAFTING_TYPE.get();
    }

    @Override
    @NotNull
    public RecipeSerializer<? extends Recipe<InWorldCraftingInput>> getSerializer() {
        return ModRecipeSerializers.IN_WORLD_CRAFTING.get();
    }

    // Getters for data generation
    public BlockState getTargetBlock() {
        return targetBlock;
    }

    public Ingredient getTriggerItem() {
        return triggerItem;
    }

    public List<Ingredient> getThrownIngredients() {
        return thrownIngredients;
    }

    @NotNull
    public ItemStack getResult() {
        return result;
    }
}
