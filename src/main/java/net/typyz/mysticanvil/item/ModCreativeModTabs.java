package net.typyz.mysticanvil.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.typyz.mysticanvil.MysticAnvil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.typyz.mysticanvil.block.ModBlocks;


public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MysticAnvil.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MYSTIC_ANVIL_TAB = CREATIVE_MODE_TABS.register("tutorial_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WRENCH.get()))
                    .title(Component.translatable("creativetab.mysticanvil_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        //adds item into custom tab ( .get() is needed only for custom item)
                        pOutput.accept(ModItems.WRENCH.get());
                        pOutput.accept(ModBlocks.MYSTIC_LOG.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
