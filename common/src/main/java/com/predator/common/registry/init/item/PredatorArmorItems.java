package com.predator.common.registry.init.item;

import com.predator.common.registry.init.PredatorArmorMaterials;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPArmorItems;

public class PredatorArmorItems {

    private static final int VERITANIUM_DURABILITY_MULTIPLIER = 40;

    public static final AVPDeferredHolder<Item> JUNGLE_PREDATOR_LEGGINGS = AVPArmorItems.register(
        "jungle_predator_leggings",
        PredatorArmorMaterials.VERITANIUM::getHolder,
        ArmorItem.Type.LEGGINGS,
        VERITANIUM_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> JUNGLE_PREDATOR_HELMET = AVPArmorItems.register(
        "jungle_predator_helmet",
        PredatorArmorMaterials.VERITANIUM::getHolder,
        ArmorItem.Type.HELMET,
        VERITANIUM_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> JUNGLE_PREDATOR_CHESTPLATE = AVPArmorItems.register(
        "jungle_predator_chestplate",
        PredatorArmorMaterials.VERITANIUM::getHolder,
        ArmorItem.Type.CHESTPLATE,
        VERITANIUM_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> JUNGLE_PREDATOR_BOOTS = AVPArmorItems.register(
        "jungle_predator_boots",
        PredatorArmorMaterials.VERITANIUM::getHolder,
        ArmorItem.Type.BOOTS,
        VERITANIUM_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static void initialize() {}
}
