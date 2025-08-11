package com.typ.mythicanvil.item;

import com.typ.mythicanvil.MythicAnvil;
import com.typ.mythicanvil.item.custom.MythicHammerItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MythicAnvil.MOD_ID);

//    public static final DeferredItem<Item> TRIGGER = ITEMS.register("trigger",
//            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MYTHIC_HAMMER = ITEMS.register("mythic_hammer",
            () -> new MythicHammerItem(new Item.Properties()
                    .durability(500))); // Same durability as mace, attributes handled in MythicHammerItem

    public static void register(IEventBus eventBus)  {
        ITEMS.register(eventBus);
    }
}
