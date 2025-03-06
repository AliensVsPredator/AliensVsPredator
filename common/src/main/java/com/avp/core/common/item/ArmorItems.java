package com.avp.core.common.item;

import com.avp.core.platform.service.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.avp.core.common.armor.ArmorMaterials;

public class ArmorItems {

    private static final int CHITIN_DURABILITY_MULTIPLIER = 21;

    private static final int MK50_DURABILITY_MULTIPLIER = 14;

    private static final int PLATED_CHITIN_DURABILITY_MULTIPLIER = 27;

    private static final int PRESSURE_DURABILITY_MULTIPLIER = 12;

    private static final int STEEL_DURABILITY_MULTIPLIER = 21;

    private static final int TACTICAL_DURABILITY_MULTIPLIER = 18;

    private static final int TITANIUM_DURABILITY_MULTIPLIER = 27;

    private static final int VERITANIUM_DURABILITY_MULTIPLIER = 40;

    public static final Supplier<Item> CHITIN_BOOTS = register(
        "chitin_boots",
        ArmorMaterials.CHITIN.get(),
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> CHITIN_CHESTPLATE = register(
        "chitin_chestplate",
        ArmorMaterials.CHITIN.get(),
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> CHITIN_HELMET = register(
        "chitin_helmet",
        ArmorMaterials.CHITIN.get(),
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> CHITIN_LEGGINGS = register(
        "chitin_leggings",
        ArmorMaterials.CHITIN.get(),
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> JUNGLE_PREDATOR_BOOTS = register(
        "jungle_predator_boots",
        ArmorMaterials.VERITANIUM.get(),
        ArmorItem.Type.BOOTS,
        VERITANIUM_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> JUNGLE_PREDATOR_CHESTPLATE = register(
        "jungle_predator_chestplate",
        ArmorMaterials.VERITANIUM.get(),
        ArmorItem.Type.CHESTPLATE,
        VERITANIUM_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> JUNGLE_PREDATOR_HELMET = register(
        "jungle_predator_helmet",
        ArmorMaterials.VERITANIUM.get(),
        ArmorItem.Type.HELMET,
        VERITANIUM_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> JUNGLE_PREDATOR_LEGGINGS = register(
        "jungle_predator_leggings",
        ArmorMaterials.VERITANIUM.get(),
        ArmorItem.Type.LEGGINGS,
        VERITANIUM_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> MK50_BOOTS = register(
        "mk50_boots",
        ArmorMaterials.MK50.get(),
        ArmorItem.Type.BOOTS,
        MK50_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> MK50_CHESTPLATE = register(
        "mk50_chestplate",
        ArmorMaterials.MK50.get(),
        ArmorItem.Type.CHESTPLATE,
        MK50_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> MK50_HELMET = register(
        "mk50_helmet",
        ArmorMaterials.MK50.get(),
        ArmorItem.Type.HELMET,
        MK50_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> MK50_LEGGINGS = register(
        "mk50_leggings",
        ArmorMaterials.MK50.get(),
        ArmorItem.Type.LEGGINGS,
        MK50_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> NETHER_CHITIN_BOOTS = register(
        "nether_chitin_boots",
        ArmorMaterials.NETHER_CHITIN.get(),
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> NETHER_CHITIN_CHESTPLATE = register(
        "nether_chitin_chestplate",
        ArmorMaterials.NETHER_CHITIN.get(),
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> NETHER_CHITIN_HELMET = register(
        "nether_chitin_helmet",
        ArmorMaterials.NETHER_CHITIN.get(),
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> NETHER_CHITIN_LEGGINGS = register(
        "nether_chitin_leggings",
        ArmorMaterials.NETHER_CHITIN.get(),
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> PLATED_CHITIN_BOOTS = register(
        "plated_chitin_boots",
        ArmorMaterials.PLATED_CHITIN.get(),
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> PLATED_CHITIN_CHESTPLATE = register(
        "plated_chitin_chestplate",
        ArmorMaterials.PLATED_CHITIN.get(),
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> PLATED_CHITIN_HELMET = register(
        "plated_chitin_helmet",
        ArmorMaterials.PLATED_CHITIN.get(),
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> PLATED_CHITIN_LEGGINGS = register(
        "plated_chitin_leggings",
        ArmorMaterials.PLATED_CHITIN.get(),
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> PLATED_NETHER_CHITIN_BOOTS = register(
        "plated_nether_chitin_boots",
        ArmorMaterials.PLATED_NETHER_CHITIN.get(),
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> PLATED_NETHER_CHITIN_CHESTPLATE = register(
        "plated_nether_chitin_chestplate",
        ArmorMaterials.PLATED_NETHER_CHITIN.get(),
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> PLATED_NETHER_CHITIN_HELMET = register(
        "plated_nether_chitin_helmet",
        ArmorMaterials.PLATED_NETHER_CHITIN.get(),
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> PLATED_NETHER_CHITIN_LEGGINGS = register(
        "plated_nether_chitin_leggings",
        ArmorMaterials.PLATED_NETHER_CHITIN.get(),
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        Item.Properties::fireResistant
    );

    public static final Supplier<Item> PRESSURE_BOOTS = register(
        "pressure_boots",
        ArmorMaterials.PRESSURE.get(),
        ArmorItem.Type.BOOTS,
        PRESSURE_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> PRESSURE_CHESTPLATE = register(
        "pressure_chestplate",
        ArmorMaterials.PRESSURE.get(),
        ArmorItem.Type.CHESTPLATE,
        PRESSURE_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> PRESSURE_HELMET = register(
        "pressure_helmet",
        ArmorMaterials.PRESSURE.get(),
        ArmorItem.Type.HELMET,
        PRESSURE_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> PRESSURE_LEGGINGS = register(
        "pressure_leggings",
        ArmorMaterials.PRESSURE.get(),
        ArmorItem.Type.LEGGINGS,
        PRESSURE_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> STEEL_BOOTS = register(
        "steel_boots",
        ArmorMaterials.STEEL.get(),
        ArmorItem.Type.BOOTS,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> STEEL_CHESTPLATE = register(
        "steel_chestplate",
        ArmorMaterials.STEEL.get(),
        ArmorItem.Type.CHESTPLATE,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> STEEL_HELMET = register(
        "steel_helmet",
        ArmorMaterials.STEEL.get(),
        ArmorItem.Type.HELMET,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> STEEL_LEGGINGS = register(
        "steel_leggings",
        ArmorMaterials.STEEL.get(),
        ArmorItem.Type.LEGGINGS,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> TACTICAL_BOOTS = register(
        "tactical_boots",
        ArmorMaterials.TACTICAL.get(),
        ArmorItem.Type.BOOTS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> TACTICAL_CHESTPLATE = register(
        "tactical_chestplate",
        ArmorMaterials.TACTICAL.get(),
        ArmorItem.Type.CHESTPLATE,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> TACTICAL_HELMET = register(
        "tactical_helmet",
        ArmorMaterials.TACTICAL.get(),
        ArmorItem.Type.HELMET,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> TACTICAL_LEGGINGS = register(
        "tactical_leggings",
        ArmorMaterials.TACTICAL.get(),
        ArmorItem.Type.LEGGINGS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> TITANIUM_BOOTS = register(
        "titanium_boots",
        ArmorMaterials.TITANIUM.get(),
        ArmorItem.Type.BOOTS,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> TITANIUM_CHESTPLATE = register(
        "titanium_chestplate",
        ArmorMaterials.TITANIUM.get(),
        ArmorItem.Type.CHESTPLATE,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> TITANIUM_HELMET = register(
        "titanium_helmet",
        ArmorMaterials.TITANIUM.get(),
        ArmorItem.Type.HELMET,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final Supplier<Item> TITANIUM_LEGGINGS = register(
        "titanium_leggings",
        ArmorMaterials.TITANIUM.get(),
        ArmorItem.Type.LEGGINGS,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static Supplier<Item> register(String id, ArmorMaterial armorMaterialSupplier, ArmorItem.Type type, int durabilityMultiplier) {
        return register(id, armorMaterialSupplier, type, durabilityMultiplier, UnaryOperator.identity());
    }

    public static Supplier<Item> register(
        String id,
        ArmorMaterial armorMaterialSupplier,
        ArmorItem.Type type,
        int durabilityMultiplier,
        UnaryOperator<Item.Properties> propertiesUnaryOperator
    ) {
        var durability = type.getDurability(durabilityMultiplier);
        var properties = propertiesUnaryOperator.apply(new Item.Properties().durability(durability));
        return register(() -> new ArmorItem(Holder.direct(armorMaterialSupplier), type, properties), id);
    }

    public static Supplier<Item> register(Supplier<Item> itemSupplier, String id) {
        return Services.REGISTRY.registerItem(id, itemSupplier);
    }

    public static void initialize() {}
}
