package com.predator.common.registry.init.item;

import com.predator.common.gameplay.item.VeritaniumArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPArmorItems;

public class PredatorArmorItems {

    public static final AVPDeferredHolder<Item> JUNGLE_PREDATOR_LEGGINGS = AVPArmorItems.register(
        "jungle_predator_leggings",
        () -> new VeritaniumArmorItem(ArmorItem.Type.LEGGINGS)
    );

    public static final AVPDeferredHolder<Item> JUNGLE_PREDATOR_HELMET = AVPArmorItems.register(
        "jungle_predator_helmet",
        () -> new VeritaniumArmorItem(ArmorItem.Type.HELMET)
    );

    public static final AVPDeferredHolder<Item> JUNGLE_PREDATOR_CHESTPLATE = AVPArmorItems.register(
        "jungle_predator_chestplate",
        () -> new VeritaniumArmorItem(ArmorItem.Type.CHESTPLATE)
    );

    public static final AVPDeferredHolder<Item> JUNGLE_PREDATOR_BOOTS = AVPArmorItems.register(
        "jungle_predator_boots",
        () -> new VeritaniumArmorItem(ArmorItem.Type.BOOTS)
    );

    public static void initialize() {}
}
