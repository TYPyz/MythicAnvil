package com.typ.mythicanvil.recipe;

import com.typ.mythicanvil.MythicAnvil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MythicAnvil.MOD_ID);

    public static final Supplier<RecipeSerializer<RitualRecipe>> RITUAL =
            RECIPE_SERIALIZERS.register("ritual", RitualRecipeSerializer::new);
}
