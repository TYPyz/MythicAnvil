package com.typ.mythicanvil.item;

import com.typ.mythicanvil.MythicAnvil;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MythicAnvil.MOD_ID);

    public static final DeferredItem<Item> TRIGGER = ITEMS.register("trigger",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus)  {
        ITEMS.register(eventBus);
    }
}
