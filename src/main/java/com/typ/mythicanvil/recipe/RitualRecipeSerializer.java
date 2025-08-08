package com.typ.mythicanvil.recipe;

import com.mojang.serialization.Codec;
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

import javax.annotation.Nonnull;
import java.util.List;

public class RitualRecipeSerializer implements RecipeSerializer<RitualRecipe> {

    public static final MapCodec<RitualRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockState.CODEC.fieldOf("target_block").forGetter(RitualRecipe::getTargetBlock),
            Ingredient.CODEC.fieldOf("trigger_item").forGetter(RitualRecipe::getTriggerItem),
            Ingredient.CODEC.listOf().fieldOf("thrown_items").forGetter(RitualRecipe::getThrownItems),
            ItemStack.CODEC.fieldOf("result").forGetter(RitualRecipe::getResult),
            Codec.BOOL.optionalFieldOf("consume_trigger", true).forGetter(RitualRecipe::shouldConsumeTrigger),
            Codec.BOOL.optionalFieldOf("lightning", true).forGetter(RitualRecipe::shouldStrikeLightning)
    ).apply(inst, RitualRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), RitualRecipe::getTargetBlock,
                    Ingredient.CONTENTS_STREAM_CODEC, RitualRecipe::getTriggerItem,
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), RitualRecipe::getThrownItems,
                    ItemStack.STREAM_CODEC, RitualRecipe::getResult,
                    ByteBufCodecs.BOOL, RitualRecipe::shouldConsumeTrigger,
                    ByteBufCodecs.BOOL, RitualRecipe::shouldStrikeLightning,
                    RitualRecipe::new
            );

    @Override
    @Nonnull
    public MapCodec<RitualRecipe> codec() {
        return CODEC;
    }

    @Override
    @Nonnull
    public StreamCodec<RegistryFriendlyByteBuf, RitualRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
