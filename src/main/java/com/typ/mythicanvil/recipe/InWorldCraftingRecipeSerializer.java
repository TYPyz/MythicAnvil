package com.typ.mythicanvil.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InWorldCraftingRecipeSerializer implements RecipeSerializer<InWorldCraftingRecipe> {
    public static final MapCodec<InWorldCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        // Add debug logging to see if recipes are being parsed
        System.out.println("DEBUG: InWorldCraftingRecipeSerializer CODEC being used!");
        return inst.group(
            BlockState.CODEC.fieldOf("target_block").forGetter(InWorldCraftingRecipe::getTargetBlock),
            Ingredient.CODEC.fieldOf("trigger_item").forGetter(InWorldCraftingRecipe::getTriggerItem),
            Ingredient.CODEC.listOf().fieldOf("thrown_ingredients").forGetter(InWorldCraftingRecipe::getThrownIngredients),
            ItemStack.CODEC.fieldOf("result").forGetter(InWorldCraftingRecipe::getResult)
        ).apply(inst, InWorldCraftingRecipe::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, InWorldCraftingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), InWorldCraftingRecipe::getTargetBlock,
                    Ingredient.CONTENTS_STREAM_CODEC, InWorldCraftingRecipe::getTriggerItem,
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), InWorldCraftingRecipe::getThrownIngredients,
                    ItemStack.STREAM_CODEC, InWorldCraftingRecipe::getResult,
                    InWorldCraftingRecipe::new
            );

    public InWorldCraftingRecipeSerializer() {
        // Add debug logging to see if serializer is being created
        System.out.println("DEBUG: InWorldCraftingRecipeSerializer created!");
    }

    @Override
    @NotNull
    public MapCodec<InWorldCraftingRecipe> codec() {
        System.out.println("DEBUG: InWorldCraftingRecipeSerializer codec() called!");
        return CODEC;
    }

    @Override
    @NotNull
    public StreamCodec<RegistryFriendlyByteBuf, InWorldCraftingRecipe> streamCodec() {
        System.out.println("DEBUG: InWorldCraftingRecipeSerializer streamCodec() called!");
        return STREAM_CODEC;
    }
}
