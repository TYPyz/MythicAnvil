package net.typyz.mythicanvil.block;

import net.minecraft.client.renderer.RenderType;
import net.typyz.mythicanvil.MythicAnvil;
import net.typyz.mythicanvil.block.custom.MythicAnvilBlock;
import net.typyz.mythicanvil.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MythicAnvil.MOD_ID);

    public static final RegistryObject<Block> MYTHIC_LOG = registerBlock("mythic_log",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).sound(SoundType.WOOD)));

    //"Questo qui sotto serve se vuoi che il blocco droppi exp, ma anche se vuoi impostare una durezza del blocco
    // e se vuoi che il blocco non droppi nulla in caso non venga minato con il tool corretto."
//    public static final RegistryObject<Block> SAPPHIRE_ORE = registerBlock("sapphire_ore",
//            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
//                    .strength(2f).requiresCorrectToolForDrops(), UniformInt.of(3, 6)));
                                                                              /* UniformInt.of(3, 6) server per
                                                                              dichiarare quanta ne vuoi droppare di exp
                                                                              in questo caso è da 3 a 6." */
    public static final RegistryObject<Block> MYTHIC_ANVIL = registerBlock("mythic_anvil",
            () -> new MythicAnvilBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void  register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
