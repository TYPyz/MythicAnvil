package com.typ.mythicanvil.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// Our inputs are a BlockState, a trigger ItemStack, and a list of thrown ItemStacks
public record InWorldCraftingInput(BlockState targetBlock, ItemStack triggerItem, List<ItemStack> thrownItems) implements RecipeInput {

    // Method to get an item from a specific slot. We treat slot 0 as trigger item, and subsequent slots as thrown items
    @Override
    @NotNull
    public ItemStack getItem(int slot) {
        if (slot == 0) {
            return this.triggerItem();
        } else if (slot > 0 && slot <= thrownItems.size()) {
            return this.thrownItems().get(slot - 1);
        }
        throw new IllegalArgumentException("No item for index " + slot);
    }

    // The slot size our input requires. 1 for trigger + number of thrown items
    @Override
    public int size() {
        return 1 + thrownItems.size();
    }
}
