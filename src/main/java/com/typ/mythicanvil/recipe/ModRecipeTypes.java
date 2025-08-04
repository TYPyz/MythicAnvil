package com.typ.mythicanvil.recipe;

import com.typ.mythicanvil.MythicAnvil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MythicAnvil.MOD_ID);

    // Reverted back to "in_world_crafting" to load from data/mythicanvil/recipe/ folder
    public static final Supplier<RecipeType<InWorldCraftingRecipe>> IN_WORLD_CRAFTING_TYPE =
            RECIPE_TYPES.register("in_world_crafting", RecipeType::simple);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
