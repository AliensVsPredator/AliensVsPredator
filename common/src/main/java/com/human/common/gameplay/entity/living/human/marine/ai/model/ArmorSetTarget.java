package com.human.common.gameplay.entity.living.human.marine.ai.model;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.function.BiConsumer;

public record ArmorSetTarget(
    ItemTarget helmet,
    ItemTarget chestplate,
    ItemTarget leggings,
    ItemTarget boots
) {

    public static final ArmorSetTarget EMPTY = new ArmorSetTarget(
        ItemTarget.None.INSTANCE,
        ItemTarget.None.INSTANCE,
        ItemTarget.None.INSTANCE,
        ItemTarget.None.INSTANCE
    );

    public void forEach(BiConsumer<ItemTarget, EquipmentSlot> itemTargetConsumer) {
        itemTargetConsumer.accept(helmet, EquipmentSlot.HEAD);
        itemTargetConsumer.accept(chestplate, EquipmentSlot.CHEST);
        itemTargetConsumer.accept(leggings, EquipmentSlot.LEGS);
        itemTargetConsumer.accept(boots, EquipmentSlot.FEET);
    }

    public boolean isEmpty() {
        return this == EMPTY || allMatch(ItemTarget.Location.NONE);
    }

    public boolean allMatch(ItemTarget.Location location) {
        return helmet.location() == location
            && chestplate.location() == location
            && leggings.location() == location
            && boots.location() == location;
    }

    public boolean allNoneOrMatch(ItemTarget.Location location) {
        return (helmet.location() == ItemTarget.Location.NONE || helmet.location() == location)
            && (chestplate.location() == ItemTarget.Location.NONE || chestplate.location() == location)
            && (leggings.location() == ItemTarget.Location.NONE || leggings.location() == location)
            && (boots.location() == ItemTarget.Location.NONE || boots.location() == location);
    }

    public boolean anyMatch(ItemTarget.Location location) {
        return helmet.location() == location
            || chestplate.location() == location
            || leggings.location() == location
            || boots.location() == location;
    }
}
