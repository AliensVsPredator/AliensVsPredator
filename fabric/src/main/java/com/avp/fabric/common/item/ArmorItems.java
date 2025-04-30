package com.avp.fabric.common.item;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import com.avp.common.AVPResources;
import com.avp.fabric.common.armor.ArmorMaterials;

public class ArmorItems {

    private static final int CHITIN_DURABILITY_MULTIPLIER = 21;

    private static final int PLATED_CHITIN_DURABILITY_MULTIPLIER = 27;

    private static final int STEEL_DURABILITY_MULTIPLIER = 21;

    private static final int TACTICAL_DURABILITY_MULTIPLIER = 18;

    private static final int TITANIUM_DURABILITY_MULTIPLIER = 27;

    private static final int VERITANIUM_DURABILITY_MULTIPLIER = 40;

    public static final Item ABERRANT_CHITIN_BOOTS = register(
        "aberrant_chitin_boots",
        ArmorMaterials.ABERRANT_CHITIN,
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item ABERRANT_CHITIN_CHESTPLATE = register(
        "aberrant_chitin_chestplate",
        ArmorMaterials.ABERRANT_CHITIN,
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item ABERRANT_CHITIN_HELMET = register(
        "aberrant_chitin_helmet",
        ArmorMaterials.ABERRANT_CHITIN,
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item ABERRANT_CHITIN_LEGGINGS = register(
        "aberrant_chitin_leggings",
        ArmorMaterials.ABERRANT_CHITIN,
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item CHITIN_BOOTS = register(
        "chitin_boots",
        ArmorMaterials.CHITIN,
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item CHITIN_CHESTPLATE = register(
        "chitin_chestplate",
        ArmorMaterials.CHITIN,
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item CHITIN_HELMET = register(
        "chitin_helmet",
        ArmorMaterials.CHITIN,
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item CHITIN_LEGGINGS = register(
        "chitin_leggings",
        ArmorMaterials.CHITIN,
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item IRRADIATED_CHITIN_BOOTS = register(
        "irradiated_chitin_boots",
        ArmorMaterials.IRRADIATED_CHITIN,
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item IRRADIATED_CHITIN_CHESTPLATE = register(
        "irradiated_chitin_chestplate",
        ArmorMaterials.IRRADIATED_CHITIN,
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item IRRADIATED_CHITIN_HELMET = register(
        "irradiated_chitin_helmet",
        ArmorMaterials.IRRADIATED_CHITIN,
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item IRRADIATED_CHITIN_LEGGINGS = register(
        "irradiated_chitin_leggings",
        ArmorMaterials.IRRADIATED_CHITIN,
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item JUNGLE_PREDATOR_BOOTS = register(
        "jungle_predator_boots",
        ArmorMaterials.VERITANIUM,
        ArmorItem.Type.BOOTS,
        VERITANIUM_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item JUNGLE_PREDATOR_CHESTPLATE = register(
        "jungle_predator_chestplate",
        ArmorMaterials.VERITANIUM,
        ArmorItem.Type.CHESTPLATE,
        VERITANIUM_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item JUNGLE_PREDATOR_HELMET = register(
        "jungle_predator_helmet",
        ArmorMaterials.VERITANIUM,
        ArmorItem.Type.HELMET,
        VERITANIUM_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item JUNGLE_PREDATOR_LEGGINGS = register(
        "jungle_predator_leggings",
        ArmorMaterials.VERITANIUM,
        ArmorItem.Type.LEGGINGS,
        VERITANIUM_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item MK50_BOOTS = register("mk50_boots", () -> new MK50ArmorItem(ArmorItem.Type.BOOTS));

    public static final Item MK50_CHESTPLATE = register("mk50_chestplate", () -> new MK50ArmorItem(ArmorItem.Type.CHESTPLATE));

    public static final Item MK50_HELMET = register("mk50_helmet", () -> new MK50ArmorItem(ArmorItem.Type.HELMET));

    public static final Item MK50_LEGGINGS = register("mk50_leggings", () -> new MK50ArmorItem(ArmorItem.Type.LEGGINGS));

    public static final Item NETHER_CHITIN_BOOTS = register(
        "nether_chitin_boots",
        ArmorMaterials.NETHER_CHITIN,
        ArmorItem.Type.BOOTS,
        CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item NETHER_CHITIN_CHESTPLATE = register(
        "nether_chitin_chestplate",
        ArmorMaterials.NETHER_CHITIN,
        ArmorItem.Type.CHESTPLATE,
        CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item NETHER_CHITIN_HELMET = register(
        "nether_chitin_helmet",
        ArmorMaterials.NETHER_CHITIN,
        ArmorItem.Type.HELMET,
        CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item NETHER_CHITIN_LEGGINGS = register(
        "nether_chitin_leggings",
        ArmorMaterials.NETHER_CHITIN,
        ArmorItem.Type.LEGGINGS,
        CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item PLATED_ABERRANT_CHITIN_BOOTS = register(
        "plated_aberrant_chitin_boots",
        ArmorMaterials.PLATED_ABERRANT_CHITIN,
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_ABERRANT_CHITIN_CHESTPLATE = register(
        "plated_aberrant_chitin_chestplate",
        ArmorMaterials.PLATED_ABERRANT_CHITIN,
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_ABERRANT_CHITIN_HELMET = register(
        "plated_aberrant_chitin_helmet",
        ArmorMaterials.PLATED_ABERRANT_CHITIN,
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_ABERRANT_CHITIN_LEGGINGS = register(
        "plated_aberrant_chitin_leggings",
        ArmorMaterials.PLATED_ABERRANT_CHITIN,
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_CHITIN_BOOTS = register(
        "plated_chitin_boots",
        ArmorMaterials.PLATED_CHITIN,
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_CHITIN_CHESTPLATE = register(
        "plated_chitin_chestplate",
        ArmorMaterials.PLATED_CHITIN,
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_CHITIN_HELMET = register(
        "plated_chitin_helmet",
        ArmorMaterials.PLATED_CHITIN,
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_CHITIN_LEGGINGS = register(
        "plated_chitin_leggings",
        ArmorMaterials.PLATED_CHITIN,
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_IRRADIATED_CHITIN_BOOTS = register(
        "plated_irradiated_chitin_boots",
        ArmorMaterials.PLATED_IRRADIATED_CHITIN,
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_IRRADIATED_CHITIN_CHESTPLATE = register(
        "plated_irradiated_chitin_chestplate",
        ArmorMaterials.PLATED_IRRADIATED_CHITIN,
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_IRRADIATED_CHITIN_HELMET = register(
        "plated_irradiated_chitin_helmet",
        ArmorMaterials.PLATED_IRRADIATED_CHITIN,
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_IRRADIATED_CHITIN_LEGGINGS = register(
        "plated_irradiated_chitin_leggings",
        ArmorMaterials.PLATED_IRRADIATED_CHITIN,
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER
    );

    public static final Item PLATED_NETHER_CHITIN_BOOTS = register(
        "plated_nether_chitin_boots",
        ArmorMaterials.PLATED_NETHER_CHITIN,
        ArmorItem.Type.BOOTS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item PLATED_NETHER_CHITIN_CHESTPLATE = register(
        "plated_nether_chitin_chestplate",
        ArmorMaterials.PLATED_NETHER_CHITIN,
        ArmorItem.Type.CHESTPLATE,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item PLATED_NETHER_CHITIN_HELMET = register(
        "plated_nether_chitin_helmet",
        ArmorMaterials.PLATED_NETHER_CHITIN,
        ArmorItem.Type.HELMET,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item PLATED_NETHER_CHITIN_LEGGINGS = register(
        "plated_nether_chitin_leggings",
        ArmorMaterials.PLATED_NETHER_CHITIN,
        ArmorItem.Type.LEGGINGS,
        PLATED_CHITIN_DURABILITY_MULTIPLIER,
        new Item.Properties().fireResistant()
    );

    public static final Item PRESSURE_BOOTS = register("pressure_boots", () -> new PressureSuitArmorItem(ArmorItem.Type.BOOTS));

    public static final Item PRESSURE_CHESTPLATE = register(
        "pressure_chestplate",
        () -> new PressureSuitArmorItem(ArmorItem.Type.CHESTPLATE)
    );

    public static final Item PRESSURE_HELMET = register("pressure_helmet", () -> new PressureSuitArmorItem(ArmorItem.Type.HELMET));

    public static final Item PRESSURE_LEGGINGS = register("pressure_leggings", () -> new PressureSuitArmorItem(ArmorItem.Type.LEGGINGS));

    public static final Item STEEL_BOOTS = register(
        "steel_boots",
        ArmorMaterials.STEEL,
        ArmorItem.Type.BOOTS,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final Item STEEL_CHESTPLATE = register(
        "steel_chestplate",
        ArmorMaterials.STEEL,
        ArmorItem.Type.CHESTPLATE,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final Item STEEL_HELMET = register(
        "steel_helmet",
        ArmorMaterials.STEEL,
        ArmorItem.Type.HELMET,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final Item STEEL_LEGGINGS = register(
        "steel_leggings",
        ArmorMaterials.STEEL,
        ArmorItem.Type.LEGGINGS,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final Item TACTICAL_BOOTS = register(
        "tactical_boots",
        ArmorMaterials.TACTICAL,
        ArmorItem.Type.BOOTS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Item TACTICAL_CHESTPLATE = register(
        "tactical_chestplate",
        ArmorMaterials.TACTICAL,
        ArmorItem.Type.CHESTPLATE,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Item TACTICAL_HELMET = register(
        "tactical_helmet",
        ArmorMaterials.TACTICAL,
        ArmorItem.Type.HELMET,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Item TACTICAL_LEGGINGS = register(
        "tactical_leggings",
        ArmorMaterials.TACTICAL,
        ArmorItem.Type.LEGGINGS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Item TACTICAL_CAMO_BOOTS = register(
        "tactical_camo_boots",
        ArmorMaterials.TACTICAL,
        ArmorItem.Type.BOOTS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Item TACTICAL_CAMO_CHESTPLATE = register(
        "tactical_camo_chestplate",
        ArmorMaterials.TACTICAL,
        ArmorItem.Type.CHESTPLATE,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Item TACTICAL_CAMO_HELMET = register(
        "tactical_camo_helmet",
        ArmorMaterials.TACTICAL,
        ArmorItem.Type.HELMET,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Item TACTICAL_CAMO_LEGGINGS = register(
        "tactical_camo_leggings",
        ArmorMaterials.TACTICAL,
        ArmorItem.Type.LEGGINGS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final Item TITANIUM_BOOTS = register(
        "titanium_boots",
        ArmorMaterials.TITANIUM,
        ArmorItem.Type.BOOTS,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final Item TITANIUM_CHESTPLATE = register(
        "titanium_chestplate",
        ArmorMaterials.TITANIUM,
        ArmorItem.Type.CHESTPLATE,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final Item TITANIUM_HELMET = register(
        "titanium_helmet",
        ArmorMaterials.TITANIUM,
        ArmorItem.Type.HELMET,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final Item TITANIUM_LEGGINGS = register(
        "titanium_leggings",
        ArmorMaterials.TITANIUM,
        ArmorItem.Type.LEGGINGS,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    // TODO: Make this return a Supplier<Item>.
    private static Item register(String id, Holder<ArmorMaterial> holder, ArmorItem.Type type, int durabilityMultiplier) {
        return register(id, holder, type, durabilityMultiplier, new Item.Properties());
    }

    // TODO: Make this return a Supplier<Item>.
    private static Item register(
        String id,
        Holder<ArmorMaterial> holder,
        ArmorItem.Type type,
        int durabilityMultiplier,
        Item.Properties properties
    ) {
        return register(id, () -> createArmorItem(holder, type, durabilityMultiplier, properties));
    }

    // TODO: Make this return a Supplier<Item>.
    private static Item register(String id, Supplier<Item> itemSupplier) {
        var resourceLocation = AVPResources.location(id);
        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, itemSupplier.get());
    }

    private static Item createArmorItem(
        Holder<ArmorMaterial> holder,
        ArmorItem.Type type,
        int durabilityMultiplier,
        Item.Properties properties
    ) {
        var durability = type.getDurability(durabilityMultiplier);
        properties = properties.durability(durability);
        return new ArmorItem(holder, type, properties);
    }

    public static void initialize() {}
}
