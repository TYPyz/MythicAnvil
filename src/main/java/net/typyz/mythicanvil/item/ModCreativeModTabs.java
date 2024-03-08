package net.typyz.mythicanvil.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.typyz.mythicanvil.MythicAnvil;
import net.typyz.mythicanvil.block.ModBlocks;


public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MythicAnvil.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MYTHIC_ANVIL_TAB = CREATIVE_MODE_TABS.register("mythicanvil_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WRENCH.get()))
                    .title(Component.translatable("creativetab.mythicanvil_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        //adds item into custom tab ( .get() is needed only for custom item)
                        pOutput.accept(ModItems.WRENCH.get());
                        pOutput.accept(ModBlocks.MYTHIC_LOG.get());
                        pOutput.accept(ModBlocks.MYTHIC_ANVIL.get());
                        pOutput.accept(ModItems.MYTHIC_WAND.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
