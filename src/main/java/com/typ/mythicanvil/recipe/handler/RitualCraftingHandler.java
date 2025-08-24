package com.typ.mythicanvil.recipe.handler;

import com.typ.mythicanvil.MythicAnvil;
import com.typ.mythicanvil.recipe.RitualRecipe;
import com.typ.mythicanvil.recipe.ModRecipeTypes;
import com.typ.mythicanvil.recipe.input.RitualRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = "mythicanvil")
public class RitualCraftingHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        InteractionHand hand = event.getHand();

        // Only process main hand interactions on the server side
        if (hand != InteractionHand.MAIN_HAND || level.isClientSide()) {
            return;
        }

        ItemStack triggerItem = player.getItemInHand(hand);
        BlockState targetBlock = level.getBlockState(pos);

        // Get all ritual recipes from vanilla recipe manager
        List<RecipeHolder<RitualRecipe>> allRitualRecipes = level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.RITUAL_TYPE.get());

        // First check: Does the trigger item have any ritual recipes?
        boolean hasTriggerRecipes = allRitualRecipes.stream()
                .anyMatch(recipeHolder -> recipeHolder.value().getTriggerItem().test(triggerItem));
        
        if (!hasTriggerRecipes) {
            return;
        }

        // Second check: Does the target block have any ritual recipes?
        boolean hasTargetRecipes = allRitualRecipes.stream()
                .anyMatch(recipeHolder -> recipeHolder.value().getTargetBlock().getBlock().equals(targetBlock.getBlock()));
        
        if (!hasTargetRecipes) {
            return;
        }

        // Both checks passed, now look for items on the ground
        List<ItemEntity> nearbyItems = getNearbyItems(level, pos);
        List<ItemStack> thrownItems = nearbyItems.stream()
                .map(ItemEntity::getItem)
                .toList();

        // Create recipe input
        RitualRecipeInput input = new RitualRecipeInput(targetBlock, triggerItem, thrownItems);

        // Try to find a matching recipe from vanilla recipe manager
        Optional<RitualRecipe> matchingRecipe = allRitualRecipes.stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.matches(input, level))
                .findFirst();

        if (matchingRecipe.isPresent()) {
            MythicAnvil.LOGGER.info("RITUAL SUCCESS: Performing ritual crafting for result: {}",
                matchingRecipe.get().getResult().getDisplayName().getString());
            performRitualCrafting(matchingRecipe.get(), input, (ServerLevel) level, pos, nearbyItems, player);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        } else {
            MythicAnvil.LOGGER.warn("RITUAL FAILED: No matching recipe found for trigger: {}, target: {}, {} thrown items",
                triggerItem.getDisplayName().getString(),
                targetBlock.getBlock().getName().getString(),
                thrownItems.size());
            // No matching recipe found - play breaking tool sound to indicate failure
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                           net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static List<ItemEntity> getNearbyItems(Level level, BlockPos pos) {
        // Look for items in a 3x3x3 area around the block
        AABB searchArea = new AABB(pos).inflate(1.5);
        return level.getEntitiesOfClass(ItemEntity.class, searchArea);
    }

    private static void performRitualCrafting(RitualRecipe recipe, RitualRecipeInput input,
                                            ServerLevel level, BlockPos pos,
                                            List<ItemEntity> itemEntities, Player player) {
        MythicAnvil.LOGGER.info("RITUAL CRAFTING: Starting ritual execution...");

        // Get the result
        ItemStack result = recipe.assemble(input, level.registryAccess());

        if (!result.isEmpty()) {
            // Remove the consumed items from the world
            for (ItemEntity itemEntity : itemEntities) {
                itemEntity.discard();
            }

            // Conditionally consume the trigger item based on recipe setting
            if (recipe.shouldConsumeTrigger() && !player.isCreative()) {
                input.triggerItem().shrink(1);
            }

            player.swing(InteractionHand.MAIN_HAND); // Simulate player swing

            // Conditionally strike lightning based on recipe setting
            if (recipe.shouldStrikeLightning()) {
                net.minecraft.world.entity.LightningBolt lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
                if (lightning != null) {
                    lightning.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    lightning.setVisualOnly(true); // Makes it visual only - won't cause fire or damage
                    level.addFreshEntity(lightning);
                }
            }

            // Drop the result at the block position
            MythicAnvil.LOGGER.info("RITUAL CRAFTING: Dropping result item: {} x{} at {}",
                result.getDisplayName().getString(), result.getCount(), pos);
            ItemEntity resultEntity = new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    result);
            level.addFreshEntity(resultEntity);

            MythicAnvil.LOGGER.info("RITUAL CRAFTING: Ritual completed successfully!");
        } else {
            MythicAnvil.LOGGER.error("RITUAL CRAFTING: Recipe result is empty! This should not happen.");
        }
    }
}
