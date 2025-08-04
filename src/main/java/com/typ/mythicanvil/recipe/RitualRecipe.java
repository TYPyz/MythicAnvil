package com.typ.mythicanvil.recipe;

import com.typ.mythicanvil.recipe.input.RitualRecipeInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class RitualRecipe implements Recipe<RitualRecipeInput> {
    private final BlockState targetBlock;
    private final Ingredient triggerItem;
    private final List<Ingredient> thrownItems;
    private final ItemStack result;

    public RitualRecipe(BlockState targetBlock, Ingredient triggerItem, List<Ingredient> thrownItems, ItemStack result) {
        this.targetBlock = targetBlock;
        this.triggerItem = triggerItem;
        this.thrownItems = thrownItems;
        this.result = result;
    }

    @Override
    public boolean matches(RitualRecipeInput input, Level level) {
        // Check if target block matches
        if (!this.targetBlock.equals(input.targetBlock())) {
            return false;
        }

        // Check if trigger item matches
        if (!this.triggerItem.test(input.triggerItem())) {
            return false;
        }

        // Check if we have the right number of thrown items
        if (input.thrownItems().size() != this.thrownItems.size()) {
            return false;
        }

        // Check if all thrown items match (order doesn't matter)
        // Create a copy of the input items to track which ones we've matched
        List<ItemStack> remainingInputItems = new ArrayList<>(input.thrownItems());

        // For each required ingredient, try to find a matching item in the input
        for (Ingredient requiredIngredient : this.thrownItems) {
            boolean found = false;

            // Look for a matching item in the remaining input items
            for (int i = 0; i < remainingInputItems.size(); i++) {
                if (requiredIngredient.test(remainingInputItems.get(i))) {
                    // Found a match, remove it from the remaining items and continue
                    remainingInputItems.remove(i);
                    found = true;
                    break;
                }
            }

            // If we couldn't find a match for this ingredient, the recipe doesn't match
            if (!found) {
                return false;
            }
        }

        return true;
    }

    @Override
    @Nonnull
    public ItemStack assemble(RitualRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true; // Ritual crafting doesn't use a grid
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public boolean isSpecial() {
        return true; // This prevents the recipe from appearing in recipe books
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.RITUAL.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return ModRecipeTypes.RITUAL_TYPE.get();
    }

    // Getters for the serializer
    public BlockState getTargetBlock() {
        return targetBlock;
    }

    public Ingredient getTriggerItem() {
        return triggerItem;
    }

    public List<Ingredient> getThrownItems() {
        return thrownItems;
    }

    public ItemStack getResult() {
        return result;
    }
}
