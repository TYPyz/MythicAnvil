package com.typ.mythicanvil.event;

import com.typ.mythicanvil.MythicAnvil;
import com.typ.mythicanvil.recipe.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class InWorldCraftingEventHandler {

    @SubscribeEvent
    public static void onUseItemOnBlock(UseItemOnBlockEvent event) {
        MythicAnvil.LOGGER.info("=== USE ITEM ON BLOCK EVENT TRIGGERED ===");

        // Skip if we are not in the block-dictated phase of the event
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.BLOCK) {
            MythicAnvil.LOGGER.info("Skipping - not in BLOCK phase, current phase: {}", event.getUsePhase());
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState(pos);
        ItemStack triggerItem = event.getItemStack();

        MythicAnvil.LOGGER.info("Player right-clicked block: {} at position: {} with item: {}",
            blockState.getBlock().getName().getString(), pos, triggerItem.getItem().getName(triggerItem).getString());

        // Only process on server side
        if (level.isClientSide()) {
            MythicAnvil.LOGGER.info("Skipping - client side");
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            MythicAnvil.LOGGER.error("Level is not ServerLevel! This should not happen.");
            return;
        }

        MythicAnvil.LOGGER.info("Processing on server side");

        // Debug logging to see if recipes are loaded
        List<RecipeHolder<InWorldCraftingRecipe>> allRitualRecipes = serverLevel.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.IN_WORLD_CRAFTING_TYPE.get());
        MythicAnvil.LOGGER.info("Found {} ritual recipes loaded", allRitualRecipes.size());

        if (allRitualRecipes.isEmpty()) {
            MythicAnvil.LOGGER.error("NO RITUAL RECIPES LOADED! This is the main problem.");
            MythicAnvil.LOGGER.error("Recipe type being searched: {}", ModRecipeTypes.IN_WORLD_CRAFTING_TYPE.get());
            return;
        } else {
            MythicAnvil.LOGGER.info("Available ritual recipes:");
            for (int i = 0; i < allRitualRecipes.size(); i++) {
                InWorldCraftingRecipe recipe = allRitualRecipes.get(i).value();
                MythicAnvil.LOGGER.info("  Recipe {}: target={}, trigger={}, ingredients={}, result={}",
                    i + 1,
                    recipe.getTargetBlock().getBlock().getName().getString(),
                    recipe.getTriggerItem().toString(),
                    recipe.getThrownIngredients().toString(),
                    recipe.getResult().getItem().getName(recipe.getResult()).getString());
            }
        }

        // Check interaction cooldown
        if (!InWorldCraftingManager.canInteract(pos)) {
            MythicAnvil.LOGGER.info("Interaction on cooldown for position: {}", pos);
            return;
        }

        // First check: verify if there are any recipes associated with the trigger item
        boolean hasTriggerRecipes = hasRecipesForTriggerItem(serverLevel, triggerItem);
        MythicAnvil.LOGGER.info("Has recipes for trigger item '{}': {}",
            triggerItem.getItem().getName(triggerItem).getString(), hasTriggerRecipes);

        if (!hasTriggerRecipes) {
            MythicAnvil.LOGGER.info("No recipes found for trigger item: {}", triggerItem.getItem());
            return;
        }

        // Second check: verify if there are recipes for the targeted block
        boolean hasBlockRecipes = hasRecipesForTargetBlock(serverLevel, blockState);
        MythicAnvil.LOGGER.info("Has recipes for target block '{}': {}",
            blockState.getBlock().getName().getString(), hasBlockRecipes);

        if (!hasBlockRecipes) {
            MythicAnvil.LOGGER.info("No recipes found for target block: {}", blockState.getBlock());
            return;
        }

        // Get items on the block
        List<ItemEntity> itemEntities = InWorldCraftingManager.getItemsOnBlock(serverLevel, pos);
        MythicAnvil.LOGGER.info("Found {} item entities on block", itemEntities.size());

        if (itemEntities.isEmpty()) {
            MythicAnvil.LOGGER.info("No items found on block at position: {}", pos);
            return;
        }

        List<ItemStack> thrownItems = InWorldCraftingManager.getItemStacksFromEntities(itemEntities);
        MythicAnvil.LOGGER.info("Found {} thrown items:", thrownItems.size());
        for (int i = 0; i < thrownItems.size(); i++) {
            ItemStack stack = thrownItems.get(i);
            MythicAnvil.LOGGER.info("  Thrown item {}: {} x{}",
                i + 1,
                stack.getItem().getName(stack).getString(),
                stack.getCount());
        }

        // Create input and try to find matching recipe
        InWorldCraftingInput input = new InWorldCraftingInput(blockState, triggerItem, thrownItems);
        MythicAnvil.LOGGER.info("Created crafting input with {} items", input.size());

        Optional<RecipeHolder<InWorldCraftingRecipe>> recipeHolder = serverLevel.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.IN_WORLD_CRAFTING_TYPE.get(), input, serverLevel);

        if (recipeHolder.isPresent()) {
            InWorldCraftingRecipe recipe = recipeHolder.get().value();
            ItemStack result = recipe.assemble(input, serverLevel.registryAccess());
            MythicAnvil.LOGGER.info("Recipe found! Result: {} x{}",
                result.getItem().getName(result).getString(), result.getCount());

            if (!result.isEmpty()) {
                // Consume the thrown items
                InWorldCraftingManager.consumeItemEntities(itemEntities);
                MythicAnvil.LOGGER.info("Consumed {} thrown item entities", itemEntities.size());

                // Spawn the result item
                ItemEntity resultEntity = new ItemEntity(serverLevel,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        result);
                serverLevel.addFreshEntity(resultEntity);
                MythicAnvil.LOGGER.info("Spawned result item entity at position: {}", pos);

                // Cancel the event to prevent further processing
                event.cancelWithResult(ItemInteractionResult.SUCCESS);
                MythicAnvil.LOGGER.info("=== RITUAL CRAFTING SUCCESSFUL! ===");
            }
        } else {
            MythicAnvil.LOGGER.info("No matching recipe found for input");
            MythicAnvil.LOGGER.info("Attempting manual recipe matching...");

            // Manual debug - check each recipe individually
            for (int i = 0; i < allRitualRecipes.size(); i++) {
                InWorldCraftingRecipe recipe = allRitualRecipes.get(i).value();
                boolean matches = recipe.matches(input, serverLevel);
                MythicAnvil.LOGGER.info("Recipe {} matches: {}", i + 1, matches);

                if (!matches) {
                    // Debug why it doesn't match
                    boolean blockMatches = recipe.getTargetBlock().equals(blockState);
                    boolean triggerMatches = recipe.getTriggerItem().test(triggerItem);
                    boolean ingredientCountMatches = recipe.getThrownIngredients().size() == thrownItems.size();

                    MythicAnvil.LOGGER.info("  Block matches: {} (expected: {}, got: {})",
                        blockMatches,
                        recipe.getTargetBlock().getBlock().getName().getString(),
                        blockState.getBlock().getName().getString());
                    MythicAnvil.LOGGER.info("  Trigger matches: {} (expected: {}, got: {})",
                        triggerMatches,
                        recipe.getTriggerItem().toString(),
                        triggerItem.getItem().getName(triggerItem).getString());
                    MythicAnvil.LOGGER.info("  Ingredient count matches: {} (expected: {}, got: {})",
                        ingredientCountMatches,
                        recipe.getThrownIngredients().size(),
                        thrownItems.size());
                }
            }
        }

        MythicAnvil.LOGGER.info("=== END USE ITEM ON BLOCK EVENT ===");
    }

    /**
     * Check if there are any recipes that use the given trigger item
     */
    private static boolean hasRecipesForTriggerItem(ServerLevel level, ItemStack triggerItem) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.IN_WORLD_CRAFTING_TYPE.get())
                .stream()
                .anyMatch(holder -> holder.value().getTriggerItem().test(triggerItem));
    }

    /**
     * Check if there are any recipes that target the given block state
     */
    private static boolean hasRecipesForTargetBlock(ServerLevel level, BlockState blockState) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.IN_WORLD_CRAFTING_TYPE.get())
                .stream()
                .anyMatch(holder -> holder.value().getTargetBlock().equals(blockState));
    }
}
