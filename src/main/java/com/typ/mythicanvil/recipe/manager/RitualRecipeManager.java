package com.typ.mythicanvil.recipe.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.typ.mythicanvil.recipe.ModRecipeSerializers;
import com.typ.mythicanvil.recipe.RitualRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class RitualRecipeManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RitualRecipeManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private Map<ResourceLocation, RitualRecipe> recipes = new HashMap<>();
    private Set<BlockState> validTargetBlocks = new HashSet<>();
    private Set<ItemStack> validTriggerItems = new HashSet<>();

    public RitualRecipeManager() {
        super(GSON, "ritual");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, RitualRecipe> newRecipes = new HashMap<>();
        Set<BlockState> newValidTargetBlocks = new HashSet<>();
        Set<ItemStack> newValidTriggerItems = new HashSet<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            ResourceLocation recipeId = entry.getKey();
            JsonElement json = entry.getValue();

            try {
                // Convert JsonElement to the appropriate format for MapCodec
                if (!json.isJsonObject()) {
                    LOGGER.error("Recipe {} is not a JSON object", recipeId);
                    continue;
                }

                var codecResult = ModRecipeSerializers.RITUAL.get().codec().decode(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(json).getOrThrow());
                if (codecResult.isSuccess()) {
                    RitualRecipe recipe = codecResult.getOrThrow();

                    newRecipes.put(recipeId, recipe);
                    newValidTargetBlocks.add(recipe.getTargetBlock());

                    // Add all possible trigger items from the ingredient
                    Arrays.stream(recipe.getTriggerItem().getItems()).forEach(newValidTriggerItems::add);

                    LOGGER.debug("Loaded ritual recipe: {}", recipeId);
                } else {
                    LOGGER.error("Failed to parse ritual recipe {}: {}", recipeId, codecResult.error().map(Object::toString).orElse("Unknown error"));
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load ritual recipe {}: {}", recipeId, e.getMessage());
            }
        }

        this.recipes = newRecipes;
        this.validTargetBlocks = newValidTargetBlocks;
        this.validTriggerItems = newValidTriggerItems;

        LOGGER.info("Loaded {} ritual recipes", newRecipes.size());
    }

    public Collection<RitualRecipe> getAllRecipes() {
        return recipes.values();
    }

    public Optional<RitualRecipe> getRecipeById(ResourceLocation id) {
        return Optional.ofNullable(recipes.get(id));
    }

    public boolean hasRecipesForTriggerItem(ItemStack triggerItem) {
        return validTriggerItems.stream().anyMatch(validItem ->
            ItemStack.isSameItemSameComponents(validItem, triggerItem));
    }

    public boolean hasRecipesForTargetBlock(BlockState targetBlock) {
        // Check if any recipe has a target block with the same block type (ignore blockstate properties)
        return validTargetBlocks.stream().anyMatch(validBlock ->
            validBlock.getBlock().equals(targetBlock.getBlock()));
    }

    public List<RitualRecipe> getRecipesForTriggerItem(ItemStack triggerItem) {
        return recipes.values().stream()
                .filter(recipe -> recipe.getTriggerItem().test(triggerItem))
                .toList();
    }

    public List<RitualRecipe> getRecipesForTargetBlock(BlockState targetBlock) {
        return recipes.values().stream()
                .filter(recipe -> recipe.getTargetBlock().getBlock().equals(targetBlock.getBlock()))
                .toList();
    }
}
