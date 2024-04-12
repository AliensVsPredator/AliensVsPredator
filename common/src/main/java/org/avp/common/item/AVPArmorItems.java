package org.avp.common.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

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

    public static final GameObject<Item> XENOMORPH_BODY;

    public static final GameObject<Item> XENOMORPH_BOOTS;

    public static final GameObject<Item> XENOMORPH_HELMET;

    public static final GameObject<Item> XENOMORPH_LEGGINGS;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName, ArmorMaterial armorMaterial, ArmorItem.Type armorType) {
        var gameObject = Services.ITEM_REGISTRY.register("armor_" + registryName, () -> new ArmorItem(armorMaterial, armorType, new Item.Properties()));
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPArmorItems() {}

    static {
        var aluminumMaterial = AVPArmorMaterials.getInstance().ALUMINUM;
        ALUMINUM_BODY = register("aluminum_body", aluminumMaterial, ArmorItem.Type.CHESTPLATE);
        ALUMINUM_BOOTS = register("aluminum_boots", aluminumMaterial, ArmorItem.Type.BOOTS);
        ALUMINUM_HELMET = register("aluminum_helmet", aluminumMaterial, ArmorItem.Type.HELMET);
        ALUMINUM_LEGGINGS = register("aluminum_leggings", aluminumMaterial, ArmorItem.Type.LEGGINGS);

        var celticMaterial = AVPArmorMaterials.getInstance().CELTIC;
        CELTIC_BODY = register("celtic_body", celticMaterial, ArmorItem.Type.CHESTPLATE);
        CELTIC_BOOTS = register("celtic_boots", celticMaterial, ArmorItem.Type.BOOTS);
        CELTIC_HELMET = register("celtic_helmet", celticMaterial, ArmorItem.Type.HELMET);
        CELTIC_LEGGINGS = register("celtic_leggings", celticMaterial, ArmorItem.Type.LEGGINGS);

        var mk50Material = AVPArmorMaterials.getInstance().MK50;
        MK50_BODY = register("mk50_body", mk50Material, ArmorItem.Type.CHESTPLATE);
        MK50_BOOTS = register("mk50_boots", mk50Material, ArmorItem.Type.BOOTS);
        MK50_HELMET = register("mk50_helmet", mk50Material, ArmorItem.Type.HELMET);
        MK50_LEGGINGS = register("mk50_leggings", mk50Material, ArmorItem.Type.LEGGINGS);

        var pressureMaterial = AVPArmorMaterials.getInstance().PRESSURE;
        PRESSURE_BODY = register("pressure_body", pressureMaterial, ArmorItem.Type.CHESTPLATE);
        PRESSURE_BOOTS = register("pressure_boots", pressureMaterial, ArmorItem.Type.BOOTS);
        PRESSURE_HELMET = register("pressure_helmet", pressureMaterial, ArmorItem.Type.HELMET);
        PRESSURE_LEGGINGS = register("pressure_leggings", pressureMaterial, ArmorItem.Type.LEGGINGS);

        var tacticalMaterial = AVPArmorMaterials.getInstance().TACTICAL;
        TACTICAL_BODY = register("tactical_body", tacticalMaterial, ArmorItem.Type.CHESTPLATE);
        TACTICAL_BOOTS = register("tactical_boots", tacticalMaterial, ArmorItem.Type.BOOTS);
        TACTICAL_HELMET = register("tactical_helmet", tacticalMaterial, ArmorItem.Type.HELMET);
        TACTICAL_LEGGINGS = register("tactical_leggings", tacticalMaterial, ArmorItem.Type.LEGGINGS);

        var xenomorphChitinMaterial = AVPArmorMaterials.getInstance().XENOMORPH_CHITIN;
        XENOMORPH_BODY = register("xenomorph_chitin_body", xenomorphChitinMaterial, ArmorItem.Type.CHESTPLATE);
        XENOMORPH_BOOTS = register("xenomorph_chitin_boots", xenomorphChitinMaterial, ArmorItem.Type.BOOTS);
        XENOMORPH_HELMET = register("xenomorph_chitin_helmet", xenomorphChitinMaterial, ArmorItem.Type.HELMET);
        XENOMORPH_LEGGINGS = register("xenomorph_chitin_leggings", xenomorphChitinMaterial, ArmorItem.Type.LEGGINGS);
    }
}
