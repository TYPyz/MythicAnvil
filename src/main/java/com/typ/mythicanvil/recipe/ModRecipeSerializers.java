package com.typ.mythicanvil.recipe;

import com.typ.mythicanvil.MythicAnvil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MythicAnvil.MOD_ID);

    // Reverted back to "in_world_crafting" to match the recipe type registration
    public static final Supplier<RecipeSerializer<InWorldCraftingRecipe>> IN_WORLD_CRAFTING =
            RECIPE_SERIALIZERS.register("in_world_crafting", InWorldCraftingRecipeSerializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
