package net.typyz.mysticanvil.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.typyz.mysticanvil.MysticAnvil;
import net.typyz.mysticanvil.item.custom.MysticWandItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MysticAnvil.MOD_ID);

    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MYSTIC_WAND = ITEMS.register("mystic_wand",
            () -> new MysticWandItem(new Item.Properties().durability(100)));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
