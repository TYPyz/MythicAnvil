package net.typyz.mythicanvil.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;



public class MythicAnvilBlock extends Block {
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 13, 16),
            Block.box(14, 13, 2, 16, 14, 14),
            Block.box(0, 13, 14, 16, 14, 16),
            Block.box(0, 13, 0, 16, 14, 2),
            Block.box(0, 13, 2, 2, 14, 14),
            Block.box(4, 13, 4, 12, 15, 12),
            Block.box(5, 15, 5, 11, 17, 11),
            Block.box(4, 17, 4, 12, 19, 12),
            Block.box(3, 19, 3, 13, 21, 13)
    );

    public MythicAnvilBlock(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide()) {
            // This code only runs on the client side
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(handIn);
        boolean itemInMainHand = false;
        boolean hasFirstIngredient = false;
        boolean hasSecondIngredient = false;
        List<ItemEntity> itemsToRemove = new ArrayList<>();

        if (handIn == InteractionHand.MAIN_HAND) {
            if (heldItem.getItem() == Items.DIAMOND_AXE) {
                // Check if the player is holding correct tool
                itemInMainHand = true;

                List<ItemEntity> itemsOnBlock = worldIn.getEntitiesOfClass(ItemEntity.class, new AABB(pos.getX(), pos.getY() + 1.5D, pos.getZ(), pos.getX() + 1.0D, pos.getY() + 1.5D, pos.getZ() + 1.0D));

                for (ItemEntity itemEntity : itemsOnBlock) {
                    Item item = itemEntity.getItem().getItem();
                    if (item == Items.EMERALD) {
                        // Check for first ingredient
                        hasFirstIngredient = true;
                        itemsToRemove.add(itemEntity);
                    } else if (item == Items.COAL) {
                        // Check for second ingredient
                        hasSecondIngredient = true;
                        itemsToRemove.add(itemEntity);
                    }
                }

            }
            if (itemInMainHand && hasFirstIngredient && hasSecondIngredient) {
                worldIn.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 1.0F, 1.0F);
                for (ItemEntity itemEntity : itemsToRemove) {
                    itemEntity.remove(Entity.RemovalReason.KILLED);
                }

                LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(worldIn);
                if (lightningbolt != null) {
                    worldIn.addFreshEntity(lightningbolt);
                }
                ItemStack diamondStack = new ItemStack(Items.DIAMOND);
                ItemEntity diamondEntity = new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, diamondStack);
                worldIn.addFreshEntity(diamondEntity);
            } else {
                worldIn.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

        }
        return InteractionResult.SUCCESS;
    }
}