package net.typyz.mythicanvil.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.typyz.mythicanvil.MythicAnvil;
import net.typyz.mythicanvil.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MythicAnvil.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.MYTHIC_LOG.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.MYTHIC_ANVIL.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.MYTHIC_ANVIL.get(),
                        ModBlocks.MYTHIC_LOG.get());

//        this.tag(BlockTags.NEEDS_IRON_TOOL)
//                .add(ModBlocks.SAPPHIRE_BLOCK.get());

//        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
//                .add(ModBlocks.RAW_SAPPHIRE_BLOCK.get());

//        this.tag(BlockTags.NEEDS_STONE_TOOL)
//                .add(ModBlocks.NETHER_SAPPHIRE_ORE.get());

//        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
//                .add(ModBlocks.END_STONE_SAPPHIRE_ORE.get());


    }
}
