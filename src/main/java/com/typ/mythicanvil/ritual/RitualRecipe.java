package com.typ.mythicanvil.ritual;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class RitualRecipe {
    private final List<ItemStack> inputItems;
    private final ItemStack outputItem;
    private final Block ritualBlock;
    private final Item activationItem;

    public RitualRecipe(List<ItemStack> inputItems, ItemStack outputItem, Block ritualBlock, Item activationItem) {
        this.inputItems = inputItems;
        this.outputItem = outputItem;
        this.ritualBlock = ritualBlock;
        this.activationItem = activationItem;
    }

    public List<ItemStack> getInputItems() {
        return inputItems;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public Block getRitualBlock() {
        return ritualBlock;
    }

    public Item getActivationItem() {
        return activationItem;
    }

    // Helper method to check if a recipe matches the given conditions
    public boolean matches(Block block, Item activationItem) {
        return this.ritualBlock.equals(block) && this.activationItem.equals(activationItem);
    }
}
