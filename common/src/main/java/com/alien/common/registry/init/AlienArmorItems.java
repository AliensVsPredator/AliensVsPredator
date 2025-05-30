package com.alien.common.registry.init;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPArmorItems;

public class AlienArmorItems {

    private static final int CHITIN_DURABILITY_MULTIPLIER = 21;

    private static final int PLATED_CHITIN_DURABILITY_MULTIPLIER = 27;

    public static final AVPDeferredHolder<Item> ABERRANT_CHITIN_BOOTS = AVPArmorItems.register(
        "aberrant_chitin_boots",
        AlienArmorMaterials.ABERRANT_CHITIN::getHolder,
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> ABERRANT_CHITIN_CHESTPLATE = AVPArmorItems.register(
        "aberrant_chitin_chestplate",
        AlienArmorMaterials.ABERRANT_CHITIN::getHolder,
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> ABERRANT_CHITIN_HELMET = AVPArmorItems.register(
        "aberrant_chitin_helmet",
        AlienArmorMaterials.ABERRANT_CHITIN::getHolder,
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> ABERRANT_CHITIN_LEGGINGS = AVPArmorItems.register(
        "aberrant_chitin_leggings",
        AlienArmorMaterials.ABERRANT_CHITIN::getHolder,
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> CHITIN_BOOTS = AVPArmorItems.register(
        "chitin_boots",
        AlienArmorMaterials.CHITIN::getHolder,
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> CHITIN_CHESTPLATE = AVPArmorItems.register(
        "chitin_chestplate",
        AlienArmorMaterials.CHITIN::getHolder,
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> CHITIN_HELMET = AVPArmorItems.register(
        "chitin_helmet",
        AlienArmorMaterials.CHITIN::getHolder,
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> CHITIN_LEGGINGS = AVPArmorItems.register(
        "chitin_leggings",
        AlienArmorMaterials.CHITIN::getHolder,
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_CHITIN_BOOTS = AVPArmorItems.register(
        "irradiated_chitin_boots",
        AlienArmorMaterials.IRRADIATED_CHITIN::getHolder,
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_CHITIN_CHESTPLATE = AVPArmorItems.register(
        "irradiated_chitin_chestplate",
        AlienArmorMaterials.IRRADIATED_CHITIN::getHolder,
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_CHITIN_HELMET = AVPArmorItems.register(
        "irradiated_chitin_helmet",
        AlienArmorMaterials.IRRADIATED_CHITIN::getHolder,
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_CHITIN_LEGGINGS = AVPArmorItems.register(
        "irradiated_chitin_leggings",
        AlienArmorMaterials.IRRADIATED_CHITIN::getHolder,
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> NETHER_CHITIN_BOOTS = AVPArmorItems.register(
        "nether_chitin_boots",
        AlienArmorMaterials.NETHER_CHITIN::getHolder,
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> NETHER_CHITIN_CHESTPLATE = AVPArmorItems.register(
        "nether_chitin_chestplate",
        AlienArmorMaterials.NETHER_CHITIN::getHolder,
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> NETHER_CHITIN_HELMET = AVPArmorItems.register(
        "nether_chitin_helmet",
        AlienArmorMaterials.NETHER_CHITIN::getHolder,
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> NETHER_CHITIN_LEGGINGS = AVPArmorItems.register(
        "nether_chitin_leggings",
        AlienArmorMaterials.NETHER_CHITIN::getHolder,
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> PLATED_ABERRANT_CHITIN_BOOTS = AVPArmorItems.register(
        "plated_aberrant_chitin_boots",
        AlienArmorMaterials.PLATED_ABERRANT_CHITIN::getHolder,
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_ABERRANT_CHITIN_CHESTPLATE = AVPArmorItems.register(
        "plated_aberrant_chitin_chestplate",
        AlienArmorMaterials.PLATED_ABERRANT_CHITIN::getHolder,
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_ABERRANT_CHITIN_HELMET = AVPArmorItems.register(
        "plated_aberrant_chitin_helmet",
        AlienArmorMaterials.PLATED_ABERRANT_CHITIN::getHolder,
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_ABERRANT_CHITIN_LEGGINGS = AVPArmorItems.register(
        "plated_aberrant_chitin_leggings",
        AlienArmorMaterials.PLATED_ABERRANT_CHITIN::getHolder,
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_CHITIN_BOOTS = AVPArmorItems.register(
        "plated_chitin_boots",
        AlienArmorMaterials.PLATED_CHITIN::getHolder,
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_CHITIN_CHESTPLATE = AVPArmorItems.register(
        "plated_chitin_chestplate",
        AlienArmorMaterials.PLATED_CHITIN::getHolder,
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_CHITIN_HELMET = AVPArmorItems.register(
        "plated_chitin_helmet",
        AlienArmorMaterials.PLATED_CHITIN::getHolder,
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_CHITIN_LEGGINGS = AVPArmorItems.register(
        "plated_chitin_leggings",
        AlienArmorMaterials.PLATED_CHITIN::getHolder,
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_IRRADIATED_CHITIN_BOOTS = AVPArmorItems.register(
        "plated_irradiated_chitin_boots",
        AlienArmorMaterials.PLATED_IRRADIATED_CHITIN::getHolder,
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_IRRADIATED_CHITIN_CHESTPLATE = AVPArmorItems.register(
        "plated_irradiated_chitin_chestplate",
        AlienArmorMaterials.PLATED_IRRADIATED_CHITIN::getHolder,
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_IRRADIATED_CHITIN_HELMET = AVPArmorItems.register(
        "plated_irradiated_chitin_helmet",
        AlienArmorMaterials.PLATED_IRRADIATED_CHITIN::getHolder,
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_IRRADIATED_CHITIN_LEGGINGS = AVPArmorItems.register(
        "plated_irradiated_chitin_leggings",
        AlienArmorMaterials.PLATED_IRRADIATED_CHITIN::getHolder,
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<Item> PLATED_NETHER_CHITIN_BOOTS = AVPArmorItems.register(
        "plated_nether_chitin_boots",
        AlienArmorMaterials.PLATED_NETHER_CHITIN::getHolder,
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> PLATED_NETHER_CHITIN_CHESTPLATE = AVPArmorItems.register(
        "plated_nether_chitin_chestplate",
        AlienArmorMaterials.PLATED_NETHER_CHITIN::getHolder,
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> PLATED_NETHER_CHITIN_HELMET = AVPArmorItems.register(
        "plated_nether_chitin_helmet",
        AlienArmorMaterials.PLATED_NETHER_CHITIN::getHolder,
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> PLATED_NETHER_CHITIN_LEGGINGS = AVPArmorItems.register(
        "plated_nether_chitin_leggings",
        AlienArmorMaterials.PLATED_NETHER_CHITIN::getHolder,
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static void initialize() {}
}
