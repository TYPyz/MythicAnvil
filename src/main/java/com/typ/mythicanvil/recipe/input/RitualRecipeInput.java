package com.typ.mythicanvil.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Recipe input for Ritual crafting system.
 * Contains the block state being targeted, the trigger item in main hand, and thrown items.
 */
public record RitualRecipeInput(BlockState targetBlock, ItemStack triggerItem, List<ItemStack> thrownItems) implements RecipeInput {

    @Override
    @Nonnull
    public ItemStack getItem(int slot) {
        if (slot == 0) {
            return this.triggerItem();
        } else if (slot > 0 && slot <= thrownItems.size()) {
            return this.thrownItems().get(slot - 1);
        }
        throw new IllegalArgumentException("No item for index " + slot);
    }

    @Override
    public int size() {
        return 1 + this.thrownItems().size(); // trigger item + thrown items
    }
}
