package net.typyz.mythicanvil.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.typyz.mythicanvil.MythicAnvil;
import net.typyz.mythicanvil.item.custom.MythicWandItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MythicAnvil.MOD_ID);

    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MYTHIC_WAND = ITEMS.register("mythic_wand",
            () -> new MythicWandItem(new Item.Properties().durability(100)));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
