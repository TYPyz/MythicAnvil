package net.typyz.mythicanvil.datagen;

import net.minecraft.world.item.Items;
import net.typyz.mythicanvil.MythicAnvil;
import net.typyz.mythicanvil.block.ModBlocks;
import net.typyz.mythicanvil.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private static final List<ItemLike> MYTHIC_WAND_SMELTABLES = List.of(ModItems.MYTHIC_WAND.get(),
            ModBlocks.MYTHIC_LOG.get());

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        oreSmelting(pWriter, MYTHIC_WAND_SMELTABLES, RecipeCategory.MISC, ModItems.MYTHIC_WAND.get(), 0.25f, 200, "mythic_wand");
        oreBlasting(pWriter, MYTHIC_WAND_SMELTABLES, RecipeCategory.MISC, ModItems.MYTHIC_WAND.get(), 0.25f, 200, "mythic_wand");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MYTHIC_LOG.get())
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .define('S', Items.OAK_LOG)
                .define('A', Items.DIAMOND)
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.EMERALD))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.OAK_LOG, 9)
                .requires(ModBlocks.MYTHIC_LOG.get())
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.EMERALD))
                .save(pWriter);
    }

    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult,
                    pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer,  MythicAnvil.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
