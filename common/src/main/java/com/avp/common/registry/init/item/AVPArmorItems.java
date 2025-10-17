package com.avp.common.registry.init.item;

import com.human.common.gameplay.item.MK50ArmorItem;
import com.human.common.gameplay.item.PressureSuitArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.AVPArmorMaterials;

public class AVPArmorItems {

    private static final int STEEL_DURABILITY_MULTIPLIER = 21;

    private static final int TACTICAL_DURABILITY_MULTIPLIER = 18;

    private static final int TITANIUM_DURABILITY_MULTIPLIER = 27;

    private static final int WY_COMMANDO_DURABILITY_MULTIPLIER = 18;

    private static final int WY_ELITE_DURABILITY_MULTIPLIER = 18;

    public static final AVPDeferredHolder<ArmorItem> MK50_BOOTS = register("mk50_boots", () -> new MK50ArmorItem(ArmorItem.Type.BOOTS));

    public static final AVPDeferredHolder<ArmorItem> MK50_CHESTPLATE = register(
        "mk50_chestplate",
        () -> new MK50ArmorItem(ArmorItem.Type.CHESTPLATE)
    );

    public static final AVPDeferredHolder<ArmorItem> MK50_HELMET = register("mk50_helmet", () -> new MK50ArmorItem(ArmorItem.Type.HELMET));

    public static final AVPDeferredHolder<ArmorItem> MK50_LEGGINGS = register(
        "mk50_leggings",
        () -> new MK50ArmorItem(ArmorItem.Type.LEGGINGS)
    );

    public static final AVPDeferredHolder<ArmorItem> PRESSURE_BOOTS = register(
        "pressure_boots",
        () -> new PressureSuitArmorItem(ArmorItem.Type.BOOTS)
    );

    public static final AVPDeferredHolder<ArmorItem> PRESSURE_CHESTPLATE = register(
        "pressure_chestplate",
        () -> new PressureSuitArmorItem(ArmorItem.Type.CHESTPLATE)
    );

    public static final AVPDeferredHolder<ArmorItem> PRESSURE_HELMET = register(
        "pressure_helmet",
        () -> new PressureSuitArmorItem(ArmorItem.Type.HELMET)
    );

    public static final AVPDeferredHolder<ArmorItem> PRESSURE_LEGGINGS = register(
        "pressure_leggings",
        () -> new PressureSuitArmorItem(ArmorItem.Type.LEGGINGS)
    );

    public static final AVPDeferredHolder<ArmorItem> STEEL_BOOTS = register(
        "steel_boots",
        AVPArmorMaterials.STEEL::getHolder,
        ArmorItem.Type.BOOTS,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> STEEL_CHESTPLATE = register(
        "steel_chestplate",
        AVPArmorMaterials.STEEL::getHolder,
        ArmorItem.Type.CHESTPLATE,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> STEEL_HELMET = register(
        "steel_helmet",
        AVPArmorMaterials.STEEL::getHolder,
        ArmorItem.Type.HELMET,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> STEEL_LEGGINGS = register(
        "steel_leggings",
        AVPArmorMaterials.STEEL::getHolder,
        ArmorItem.Type.LEGGINGS,
        STEEL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TACTICAL_BOOTS = register(
        "tactical_boots",
        AVPArmorMaterials.TACTICAL::getHolder,
        ArmorItem.Type.BOOTS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TACTICAL_CHESTPLATE = register(
        "tactical_chestplate",
        AVPArmorMaterials.TACTICAL::getHolder,
        ArmorItem.Type.CHESTPLATE,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TACTICAL_HELMET = register(
        "tactical_helmet",
        AVPArmorMaterials.TACTICAL::getHolder,
        ArmorItem.Type.HELMET,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TACTICAL_LEGGINGS = register(
        "tactical_leggings",
        AVPArmorMaterials.TACTICAL::getHolder,
        ArmorItem.Type.LEGGINGS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TACTICAL_CAMO_BOOTS = register(
        "tactical_camo_boots",
        AVPArmorMaterials.TACTICAL::getHolder,
        ArmorItem.Type.BOOTS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TACTICAL_CAMO_CHESTPLATE = register(
        "tactical_camo_chestplate",
        AVPArmorMaterials.TACTICAL::getHolder,
        ArmorItem.Type.CHESTPLATE,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TACTICAL_CAMO_HELMET = register(
        "tactical_camo_helmet",
        AVPArmorMaterials.TACTICAL::getHolder,
        ArmorItem.Type.HELMET,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TACTICAL_CAMO_LEGGINGS = register(
        "tactical_camo_leggings",
        AVPArmorMaterials.TACTICAL::getHolder,
        ArmorItem.Type.LEGGINGS,
        TACTICAL_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TITANIUM_BOOTS = register(
        "titanium_boots",
        AVPArmorMaterials.TITANIUM::getHolder,
        ArmorItem.Type.BOOTS,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TITANIUM_CHESTPLATE = register(
        "titanium_chestplate",
        AVPArmorMaterials.TITANIUM::getHolder,
        ArmorItem.Type.CHESTPLATE,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TITANIUM_HELMET = register(
        "titanium_helmet",
        AVPArmorMaterials.TITANIUM::getHolder,
        ArmorItem.Type.HELMET,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> TITANIUM_LEGGINGS = register(
        "titanium_leggings",
        AVPArmorMaterials.TITANIUM::getHolder,
        ArmorItem.Type.LEGGINGS,
        TITANIUM_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> WY_COMMANDO_BOOTS = register(
        "wy_commando_boots",
        AVPArmorMaterials.WY_COMMANDO::getHolder,
        ArmorItem.Type.BOOTS,
        WY_COMMANDO_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> WY_COMMANDO_CHESTPLATE = register(
        "wy_commando_chestplate",
        AVPArmorMaterials.WY_COMMANDO::getHolder,
        ArmorItem.Type.CHESTPLATE,
        WY_COMMANDO_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> WY_COMMANDO_HELMET = register(
        "wy_commando_helmet",
        AVPArmorMaterials.WY_COMMANDO::getHolder,
        ArmorItem.Type.HELMET,
        WY_COMMANDO_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> WY_COMMANDO_LEGGINGS = register(
        "wy_commando_leggings",
        AVPArmorMaterials.WY_COMMANDO::getHolder,
        ArmorItem.Type.LEGGINGS,
        WY_COMMANDO_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> WY_ELITE_BOOTS = register(
        "wy_elite_boots",
        AVPArmorMaterials.WY_ELITE::getHolder,
        ArmorItem.Type.BOOTS,
        WY_ELITE_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> WY_ELITE_CHESTPLATE = register(
        "wy_elite_chestplate",
        AVPArmorMaterials.WY_ELITE::getHolder,
        ArmorItem.Type.CHESTPLATE,
        WY_ELITE_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> WY_ELITE_HELMET = register(
        "wy_elite_helmet",
        AVPArmorMaterials.WY_ELITE::getHolder,
        ArmorItem.Type.HELMET,
        WY_ELITE_DURABILITY_MULTIPLIER
    );

    public static final AVPDeferredHolder<ArmorItem> WY_ELITE_LEGGINGS = register(
        "wy_elite_leggings",
        AVPArmorMaterials.WY_ELITE::getHolder,
        ArmorItem.Type.LEGGINGS,
        WY_ELITE_DURABILITY_MULTIPLIER
    );

    public static AVPDeferredHolder<ArmorItem> register(
        String id,
        Supplier<Holder<ArmorMaterial>> holderSupplier,
        ArmorItem.Type type,
        int durabilityMultiplier
    ) {
        return register(id, holderSupplier, type, durabilityMultiplier, new Item.Properties());
    }

    public static AVPDeferredHolder<ArmorItem> register(
        String id,
        Supplier<Holder<ArmorMaterial>> holderSupplier,
        ArmorItem.Type type,
        int durabilityMultiplier,
        Item.Properties properties
    ) {
        return register(id, () -> createArmorItem(holderSupplier.get(), type, durabilityMultiplier, properties));
    }

    public static <T extends Item> AVPDeferredHolder<T> register(String id, Supplier<T> itemSupplier) {
        return AVPItems.register(id, itemSupplier);
    }

    public static ArmorItem createArmorItem(
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
