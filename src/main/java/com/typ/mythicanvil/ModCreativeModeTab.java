package com.typ.mythicanvil;

import com.typ.mythicanvil.block.ModBlocks;
import com.typ.mythicanvil.item.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MythicAnvil.MOD_ID);

    public static final Supplier<CreativeModeTab> BISMUTH_ITEMS_TAB = CREATIVE_MODE_TAB.register("bismuth_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TRIGGER.get()))
                    .title(Component.translatable("creativetab.mythicanvil.mythicanvil_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.TRIGGER);
                        output.accept(ModBlocks.BISMUTH_BLOCK);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
