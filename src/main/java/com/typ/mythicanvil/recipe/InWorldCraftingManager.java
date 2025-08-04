package com.typ.mythicanvil.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages thrown items on blocks for in-world crafting
 */
public class InWorldCraftingManager {
    private static final Map<BlockPos, Long> lastInteractionTime = new HashMap<>();
    private static final long INTERACTION_COOLDOWN = 1000; // 1 second cooldown

    /**
     * Gets all item entities within a 1x1x1 area above the specified block position
     */
    public static List<ItemEntity> getItemsOnBlock(ServerLevel level, BlockPos blockPos) {
        AABB aabb = new AABB(blockPos.above()).inflate(0.5);
        return level.getEntitiesOfClass(ItemEntity.class, aabb);
    }

    /**
     * Extracts item stacks from item entities without removing them from the world yet
     */
    public static List<ItemStack> getItemStacksFromEntities(List<ItemEntity> itemEntities) {
        return itemEntities.stream()
                .map(ItemEntity::getItem)
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    /**
     * Removes the item entities from the world (after successful crafting)
     */
    public static void consumeItemEntities(List<ItemEntity> itemEntities) {
        itemEntities.forEach(ItemEntity::discard);
    }

    /**
     * Checks if enough time has passed since the last interaction at this position
     */
    public static boolean canInteract(BlockPos pos) {
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastInteractionTime.get(pos);

        if (lastTime == null || currentTime - lastTime >= INTERACTION_COOLDOWN) {
            lastInteractionTime.put(pos, currentTime);
            return true;
        }

        return false;
    }

    /**
     * Cleans up old interaction times to prevent memory leaks
     */
    public static void cleanupOldInteractions() {
        long currentTime = System.currentTimeMillis();
        lastInteractionTime.entrySet().removeIf(entry ->
            currentTime - entry.getValue() > INTERACTION_COOLDOWN * 10);
    }
}
