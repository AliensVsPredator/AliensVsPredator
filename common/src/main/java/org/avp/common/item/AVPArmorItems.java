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

    public final Holder<Item> ALUMINUM_BODY = createHolder("aluminum_body", AVPArmorMaterials.ALUMINUM, ArmorItem.Type.CHESTPLATE);
    public final Holder<Item> ALUMINUM_BOOTS = createHolder("aluminum_boots", AVPArmorMaterials.ALUMINUM, ArmorItem.Type.BOOTS);
    public final Holder<Item> ALUMINUM_HELMET = createHolder("aluminum_helmet", AVPArmorMaterials.ALUMINUM, ArmorItem.Type.HELMET);
    public final Holder<Item> ALUMINUM_LEGGINGS = createHolder("aluminum_leggings", AVPArmorMaterials.ALUMINUM, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> MK50_BODY = createHolder("mk50_body", AVPArmorMaterials.MK50, ArmorItem.Type.CHESTPLATE);
    public final Holder<Item> MK50_BOOTS = createHolder("mk50_boots", AVPArmorMaterials.MK50, ArmorItem.Type.BOOTS);
    public final Holder<Item> MK50_HELMET = createHolder("mk50_helmet", AVPArmorMaterials.MK50, ArmorItem.Type.HELMET);
    public final Holder<Item> MK50_LEGGINGS = createHolder("mk50_leggings", AVPArmorMaterials.MK50, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> ORIONITE_BODY = createHolder("orionite_body", AVPArmorMaterials.ORIONITE, ArmorItem.Type.CHESTPLATE);
    public final Holder<Item> ORIONITE_BOOTS = createHolder("orionite_boots", AVPArmorMaterials.ORIONITE, ArmorItem.Type.BOOTS);
    public final Holder<Item> ORIONITE_HELMET = createHolder("orionite_helmet", AVPArmorMaterials.ORIONITE, ArmorItem.Type.HELMET);
    public final Holder<Item> ORIONITE_LEGGINGS = createHolder("orionite_leggings", AVPArmorMaterials.ORIONITE, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> PRESSURE_BODY = createHolder("pressure_body", AVPArmorMaterials.PRESSURE, ArmorItem.Type.CHESTPLATE);
    public final Holder<Item> PRESSURE_BOOTS = createHolder("pressure_boots", AVPArmorMaterials.PRESSURE, ArmorItem.Type.BOOTS);
    public final Holder<Item> PRESSURE_HELMET = createHolder("pressure_helmet", AVPArmorMaterials.PRESSURE, ArmorItem.Type.HELMET);
    public final Holder<Item> PRESSURE_LEGGINGS = createHolder("pressure_leggings", AVPArmorMaterials.PRESSURE, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> TACTICAL_BODY = createHolder("tactical_body", AVPArmorMaterials.TACTICAL, ArmorItem.Type.CHESTPLATE);
    public final Holder<Item> TACTICAL_BOOTS = createHolder("tactical_boots", AVPArmorMaterials.TACTICAL, ArmorItem.Type.BOOTS);
    public final Holder<Item> TACTICAL_HELMET = createHolder("tactical_helmet", AVPArmorMaterials.TACTICAL, ArmorItem.Type.HELMET);
    public final Holder<Item> TACTICAL_LEGGINGS = createHolder("tactical_leggings", AVPArmorMaterials.TACTICAL, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> TITANIUM_BODY = createHolder("titanium_body", AVPArmorMaterials.TITANIUM, ArmorItem.Type.CHESTPLATE);
    public final Holder<Item> TITANIUM_BOOTS = createHolder("titanium_boots", AVPArmorMaterials.TITANIUM, ArmorItem.Type.BOOTS);
    public final Holder<Item> TITANIUM_HELMET = createHolder("titanium_helmet", AVPArmorMaterials.TITANIUM, ArmorItem.Type.HELMET);
    public final Holder<Item> TITANIUM_LEGGINGS = createHolder("titanium_leggings", AVPArmorMaterials.TITANIUM, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> VERITANIUM_BODY = createHolder("veritanium_body", AVPArmorMaterials.VERITANIUM, ArmorItem.Type.CHESTPLATE);
    public final Holder<Item> VERITANIUM_BOOTS = createHolder("veritanium_boots", AVPArmorMaterials.VERITANIUM, ArmorItem.Type.BOOTS);
    public final Holder<Item> VERITANIUM_HELMET = createHolder("veritanium_helmet", AVPArmorMaterials.VERITANIUM, ArmorItem.Type.HELMET);
    public final Holder<Item> VERITANIUM_LEGGINGS = createHolder("veritanium_leggings", AVPArmorMaterials.VERITANIUM, ArmorItem.Type.LEGGINGS);

    public final Holder<Item> XENOMORPH_BODY = createHolder("xenomorph_chitin_body", AVPArmorMaterials.XENOMORPH_CHITIN, ArmorItem.Type.CHESTPLATE);
    public final Holder<Item> XENOMORPH_BOOTS = createHolder("xenomorph_chitin_boots", AVPArmorMaterials.XENOMORPH_CHITIN, ArmorItem.Type.BOOTS);

    public final Holder<Item> XENOMORPH_HELMET = createHolder("xenomorph_chitin_helmet", AVPArmorMaterials.XENOMORPH_CHITIN, ArmorItem.Type.HELMET);

    public final Holder<Item> XENOMORPH_LEGGINGS = createHolder("xenomorph_chitin_leggings", AVPArmorMaterials.XENOMORPH_CHITIN, ArmorItem.Type.LEGGINGS);

    protected Holder<Item> createHolder(String registryName, ArmorMaterial armorMaterial, ArmorItem.Type armorType) {
        return super.createHolder("armor_" + registryName, () -> new ArmorItem(armorMaterial, armorType, new Item.Properties()));
    }

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return super.createHolder("armor_" + registryName, supplier);
    }

    private AVPArmorItems() {
    }
}
