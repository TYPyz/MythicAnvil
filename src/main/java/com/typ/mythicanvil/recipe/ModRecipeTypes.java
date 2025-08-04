package com.typ.mythicanvil.recipe;

import com.typ.mythicanvil.MythicAnvil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MythicAnvil.MOD_ID);

    public static final Supplier<RecipeType<RitualRecipe>> RITUAL_TYPE =
            RECIPE_TYPES.register("ritual", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID, "ritual")));
}
