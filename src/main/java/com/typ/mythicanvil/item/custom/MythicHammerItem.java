package com.typ.mythicanvil.item.custom;

import com.typ.mythicanvil.MythicAnvil;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.resources.ResourceLocation;

public class MythicHammerItem extends MaceItem {
    public MythicHammerItem(Properties properties) {
        super(properties.attributes(createMythicHammerAttributes()));
    }

    private static ItemAttributeModifiers createMythicHammerAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                     new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID, "mythic_hammer_attack_damage"),
                             11.0, // 12.0 total damage (11.0 + 1.0 base player damage)
                             AttributeModifier.Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                     new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MythicAnvil.MOD_ID, "mythic_hammer_attack_speed"),
                             -3.4F, // Same as mace's attack speed modifier
                             AttributeModifier.Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND)
                .build();
    }
}
