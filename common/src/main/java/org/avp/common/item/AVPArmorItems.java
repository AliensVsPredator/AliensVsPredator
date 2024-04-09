package org.avp.common.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.item.armor.material.AVPArmorMaterials;
import org.avp.common.service.Services;

public class AVPArmorItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> ALUMINUM_BODY;

    public static final GameObject<Item> ALUMINUM_BOOTS;

    public static final GameObject<Item> ALUMINUM_HELMET;

    public static final GameObject<Item> ALUMINUM_LEGGINGS;

    public static final GameObject<Item> CELTIC_BODY;

    public static final GameObject<Item> CELTIC_BOOTS;

    public static final GameObject<Item> CELTIC_HELMET;

    public static final GameObject<Item> CELTIC_LEGGINGS;

    public static final GameObject<Item> MK50_BODY;

    public static final GameObject<Item> MK50_BOOTS;

    public static final GameObject<Item> MK50_HELMET;

    public static final GameObject<Item> MK50_LEGGINGS;

    public static final GameObject<Item> PRESSURE_BODY;

    public static final GameObject<Item> PRESSURE_BOOTS;

    public static final GameObject<Item> PRESSURE_HELMET;

    public static final GameObject<Item> PRESSURE_LEGGINGS;

    public static final GameObject<Item> TACTICAL_BODY;

    public static final GameObject<Item> TACTICAL_BOOTS;

    public static final GameObject<Item> TACTICAL_HELMET;

    public static final GameObject<Item> TACTICAL_LEGGINGS;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register(registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPArmorItems() {}

    static {
        var aluminumMaterial = AVPArmorMaterials.getInstance().ALUMINUM;
        ALUMINUM_BODY = register(
            "armor_aluminum_body",
            () -> new ArmorItem(aluminumMaterial, ArmorItem.Type.CHESTPLATE, new Item.Properties())
        );
        ALUMINUM_BOOTS = register(
            "armor_aluminum_boots",
            () -> new ArmorItem(aluminumMaterial, ArmorItem.Type.BOOTS, new Item.Properties())
        );
        ALUMINUM_HELMET = register(
            "armor_aluminum_helmet",
            () -> new ArmorItem(aluminumMaterial, ArmorItem.Type.HELMET, new Item.Properties())
        );
        ALUMINUM_LEGGINGS = register(
            "armor_aluminum_leggings",
            () -> new ArmorItem(aluminumMaterial, ArmorItem.Type.LEGGINGS, new Item.Properties())
        );

        var celticMaterial = AVPArmorMaterials.getInstance().CELTIC;
        CELTIC_BODY = register(
            "armor_celtic_body",
            () -> new ArmorItem(celticMaterial, ArmorItem.Type.CHESTPLATE, new Item.Properties())
        );
        CELTIC_BOOTS = register(
            "armor_celtic_boots",
            () -> new ArmorItem(celticMaterial, ArmorItem.Type.BOOTS, new Item.Properties())
        );
        CELTIC_HELMET = register(
            "armor_celtic_helmet",
            () -> new ArmorItem(celticMaterial, ArmorItem.Type.HELMET, new Item.Properties())
        );
        CELTIC_LEGGINGS = register(
            "armor_celtic_leggings",
            () -> new ArmorItem(celticMaterial, ArmorItem.Type.LEGGINGS, new Item.Properties())
        );

        var mk50Material = AVPArmorMaterials.getInstance().MK50;
        MK50_BODY = register(
            "armor_mk50_body",
            () -> new ArmorItem(mk50Material, ArmorItem.Type.CHESTPLATE, new Item.Properties())
        );
        MK50_BOOTS = register(
            "armor_mk50_boots",
            () -> new ArmorItem(mk50Material, ArmorItem.Type.BOOTS, new Item.Properties())
        );
        MK50_HELMET = register(
            "armor_mk50_helmet",
            () -> new ArmorItem(mk50Material, ArmorItem.Type.HELMET, new Item.Properties())
        );
        MK50_LEGGINGS = register(
            "armor_mk50_leggings",
            () -> new ArmorItem(mk50Material, ArmorItem.Type.LEGGINGS, new Item.Properties())
        );

        var pressureMaterial = AVPArmorMaterials.getInstance().PRESSURE;
        PRESSURE_BODY = register(
            "armor_pressure_body",
            () -> new ArmorItem(pressureMaterial, ArmorItem.Type.CHESTPLATE, new Item.Properties())
        );
        PRESSURE_BOOTS = register(
            "armor_pressure_boots",
            () -> new ArmorItem(pressureMaterial, ArmorItem.Type.BOOTS, new Item.Properties())
        );
        PRESSURE_HELMET = register(
            "armor_pressure_helmet",
            () -> new ArmorItem(pressureMaterial, ArmorItem.Type.HELMET, new Item.Properties())
        );
        PRESSURE_LEGGINGS = register(
            "armor_pressure_leggings",
            () -> new ArmorItem(pressureMaterial, ArmorItem.Type.LEGGINGS, new Item.Properties())
        );

        var tacticalMaterial = AVPArmorMaterials.getInstance().TACTICAL;
        TACTICAL_BODY = register(
            "armor_tactical_body",
            () -> new ArmorItem(tacticalMaterial, ArmorItem.Type.CHESTPLATE, new Item.Properties())
        );
        TACTICAL_BOOTS = register(
            "armor_tactical_boots",
            () -> new ArmorItem(tacticalMaterial, ArmorItem.Type.BOOTS, new Item.Properties())
        );
        TACTICAL_HELMET = register(
            "armor_tactical_helmet",
            () -> new ArmorItem(tacticalMaterial, ArmorItem.Type.HELMET, new Item.Properties())
        );
        TACTICAL_LEGGINGS = register(
            "armor_tactical_leggings",
            () -> new ArmorItem(tacticalMaterial, ArmorItem.Type.LEGGINGS, new Item.Properties())
        );

        var xenomorphChitinMaterial = AVPArmorMaterials.getInstance().XENOMORPH_CHITIN;
        register(
            "armor_xenomorph_chitin_body",
            () -> new ArmorItem(xenomorphChitinMaterial, ArmorItem.Type.CHESTPLATE, new Item.Properties())
        );
        register(
            "armor_xenomorph_chitin_boots",
            () -> new ArmorItem(xenomorphChitinMaterial, ArmorItem.Type.BOOTS, new Item.Properties())
        );
        register(
            "armor_xenomorph_chitin_helmet",
            () -> new ArmorItem(xenomorphChitinMaterial, ArmorItem.Type.HELMET, new Item.Properties())
        );
        register(
            "armor_xenomorph_chitin_leggings",
            () -> new ArmorItem(xenomorphChitinMaterial, ArmorItem.Type.LEGGINGS, new Item.Properties())
        );
    }
}
