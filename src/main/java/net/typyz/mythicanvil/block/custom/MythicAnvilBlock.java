package net.typyz.mythicanvil.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


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
}