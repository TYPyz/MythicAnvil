package net.typyz.mythicanvil.item.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static net.typyz.mythicanvil.block.ModBlocks.MYTHIC_LOG;

public class MythicWandItem extends Item {
    public MythicWandItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            BlockPos positionClicked = context.getClickedPos();
            BlockState clickedBlockState = context.getLevel().getBlockState(positionClicked);

            if (isMysticLogBlock(clickedBlockState)) {
                Player player = context.getPlayer();
                if (player != null) {


                    player.sendSystemMessage(Component.literal("Alessandro è un demone di JAVA!"));
                    // Sostituisci il blocco MYSTIC_LOG con un blocco di Anvil
                    context.getLevel().setBlockAndUpdate(positionClicked, Blocks.ANVIL.defaultBlockState());
                    // Dopo aver piazzato l'Anvil
                        context.getLevel().playSound(null, positionClicked, SoundEvents.LIGHTNING_BOLT_THUNDER , SoundSource.BLOCKS, 1.0F, 1.0F);
                        context.getLevel().playSound(null, positionClicked, SoundEvents.ANVIL_USE , SoundSource.BLOCKS, 1.0F, 1.0F);



                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private boolean isMysticLogBlock(BlockState state) {
        return state.getBlock() == MYTHIC_LOG.get();
    }

}
