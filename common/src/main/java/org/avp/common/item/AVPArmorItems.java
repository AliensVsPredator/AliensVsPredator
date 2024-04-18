package org.avp.common.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.item.armor.material.AVPArmorMaterials;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPArmorItems extends AVPDeferredItemRegistry {

    public static final AVPArmorItems INSTANCE = new AVPArmorItems();

    public final Holder<Item> aluminumBody = createHolder("aluminum_body", AVPArmorMaterials.ALUMINUM, ArmorItem.Type.CHESTPLATE);

    public final Holder<Item> aluminumBoots = createHolder("aluminum_boots", AVPArmorMaterials.ALUMINUM, ArmorItem.Type.BOOTS);

    public final Holder<Item> aluminumHelmet = createHolder("aluminum_helmet", AVPArmorMaterials.ALUMINUM, ArmorItem.Type.HELMET);

    public final Holder<Item> aluminumLeggings = createHolder("aluminum_leggings", AVPArmorMaterials.ALUMINUM, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> mk50Body = createHolder("mk50_body", AVPArmorMaterials.MK50, ArmorItem.Type.CHESTPLATE);

    public final Holder<Item> mk50Boots = createHolder("mk50_boots", AVPArmorMaterials.MK50, ArmorItem.Type.BOOTS);

    public final Holder<Item> mk50Helmet = createHolder("mk50_helmet", AVPArmorMaterials.MK50, ArmorItem.Type.HELMET);

    public final Holder<Item> mk50Leggings = createHolder("mk50_leggings", AVPArmorMaterials.MK50, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> orioniteBody = createHolder("orionite_body", AVPArmorMaterials.ORIONITE, ArmorItem.Type.CHESTPLATE);

    public final Holder<Item> orioniteBoots = createHolder("orionite_boots", AVPArmorMaterials.ORIONITE, ArmorItem.Type.BOOTS);

    public final Holder<Item> orioniteHelmet = createHolder("orionite_helmet", AVPArmorMaterials.ORIONITE, ArmorItem.Type.HELMET);

    public final Holder<Item> orioniteLeggings = createHolder("orionite_leggings", AVPArmorMaterials.ORIONITE, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> pressureBody = createHolder("pressure_body", AVPArmorMaterials.PRESSURE, ArmorItem.Type.CHESTPLATE);

    public final Holder<Item> pressureBoots = createHolder("pressure_boots", AVPArmorMaterials.PRESSURE, ArmorItem.Type.BOOTS);

    public final Holder<Item> pressureHelmet = createHolder("pressure_helmet", AVPArmorMaterials.PRESSURE, ArmorItem.Type.HELMET);

    public final Holder<Item> pressureLeggings = createHolder("pressure_leggings", AVPArmorMaterials.PRESSURE, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> tacticalBody = createHolder("tactical_body", AVPArmorMaterials.TACTICAL, ArmorItem.Type.CHESTPLATE);

    public final Holder<Item> tacticalBoots = createHolder("tactical_boots", AVPArmorMaterials.TACTICAL, ArmorItem.Type.BOOTS);

    public final Holder<Item> tacticalHelmet = createHolder("tactical_helmet", AVPArmorMaterials.TACTICAL, ArmorItem.Type.HELMET);

    public final Holder<Item> tacticalLeggings = createHolder("tactical_leggings", AVPArmorMaterials.TACTICAL, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> titaniumBody = createHolder("titanium_body", AVPArmorMaterials.TITANIUM, ArmorItem.Type.CHESTPLATE);

    public final Holder<Item> titaniumBoots = createHolder("titanium_boots", AVPArmorMaterials.TITANIUM, ArmorItem.Type.BOOTS);

    public final Holder<Item> titaniumHelmet = createHolder("titanium_helmet", AVPArmorMaterials.TITANIUM, ArmorItem.Type.HELMET);

    public final Holder<Item> titaniumLeggings = createHolder("titanium_leggings", AVPArmorMaterials.TITANIUM, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> veritaniumBody = createHolder("veritanium_body", AVPArmorMaterials.VERITANIUM, ArmorItem.Type.CHESTPLATE);

    public final Holder<Item> veritaniumBoots = createHolder("veritanium_boots", AVPArmorMaterials.VERITANIUM, ArmorItem.Type.BOOTS);

    public final Holder<Item> veritaniumHelmet = createHolder("veritanium_helmet", AVPArmorMaterials.VERITANIUM, ArmorItem.Type.HELMET);

    public final Holder<Item> veritaniumLeggings = createHolder(
        "veritanium_leggings",
        AVPArmorMaterials.VERITANIUM,
        ArmorItem.Type.LEGGINGS
    );

    public final Holder<Item> xenomorphBody = createHolder(
        "xenomorph_chitin_body",
        AVPArmorMaterials.XENOMORPH_CHITIN,
        ArmorItem.Type.CHESTPLATE
    );

    public final Holder<Item> xenomorphBoots = createHolder(
        "xenomorph_chitin_boots",
        AVPArmorMaterials.XENOMORPH_CHITIN,
        ArmorItem.Type.BOOTS
    );

    public final Holder<Item> xenomorphHelmet = createHolder(
        "xenomorph_chitin_helmet",
        AVPArmorMaterials.XENOMORPH_CHITIN,
        ArmorItem.Type.HELMET
    );

    public final Holder<Item> xenomorphLeggings = createHolder(
        "xenomorph_chitin_leggings",
        AVPArmorMaterials.XENOMORPH_CHITIN,
        ArmorItem.Type.LEGGINGS
    );

    protected Holder<Item> createHolder(String registryName, ArmorMaterial armorMaterial, ArmorItem.Type armorType) {
        return super.createHolder("armor_" + registryName, () -> new ArmorItem(armorMaterial, armorType, new Item.Properties()));
    }

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return super.createHolder("armor_" + registryName, supplier);
    }

    private AVPArmorItems() {}
}
