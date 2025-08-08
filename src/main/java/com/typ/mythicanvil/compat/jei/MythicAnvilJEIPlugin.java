package com.typ.mythicanvil.compat.jei;

import com.typ.mythicanvil.MythicAnvil;
import com.typ.mythicanvil.recipe.ModRecipeTypes;
import com.typ.mythicanvil.recipe.RitualRecipe;
import com.typ.mythicanvil.recipe.handler.RitualCraftingHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

@JeiPlugin
public class MythicAnvilJEIPlugin implements IModPlugin {

    public static final RecipeType<RitualRecipe> RITUAL_RECIPE_TYPE =
            RecipeType.create(MythicAnvil.MOD_ID, "ritual", RitualRecipe.class);

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<RitualRecipe> ritualRecipes;

        // Try to get recipes from the ritual recipe manager first (if available)
        if (RitualCraftingHandler.getRitualRecipeManager() != null) {
            Collection<RitualRecipe> managerRecipes = RitualCraftingHandler.getRitualRecipeManager().getAllRecipes();
            ritualRecipes = List.copyOf(managerRecipes);
            MythicAnvil.LOGGER.info("Loading {} ritual recipes from RitualRecipeManager", ritualRecipes.size());
        }
        // Fallback: try to get from vanilla recipe manager (if level is available)
        else if (Minecraft.getInstance().level != null) {
            RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
            ritualRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.RITUAL_TYPE.get())
                    .stream()
                    .map(recipeHolder -> recipeHolder.value())
                    .toList();
            MythicAnvil.LOGGER.info("Loading {} ritual recipes from RecipeManager", ritualRecipes.size());
        }
        // No recipes available - return empty list
        else {
            MythicAnvil.LOGGER.warn("No recipe sources available, no recipes will be shown in JEI");
            ritualRecipes = List.of();
        }

        if (ritualRecipes.isEmpty()) {
            MythicAnvil.LOGGER.warn("No ritual recipes found for JEI! Check that recipes are loading correctly.");
        } else {
            MythicAnvil.LOGGER.info("Registering {} ritual recipes with JEI", ritualRecipes.size());
        }

        registration.addRecipes(RITUAL_RECIPE_TYPE, ritualRecipes);
    }
}