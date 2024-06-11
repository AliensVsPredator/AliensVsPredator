package org.avp.common.item.armor.material;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPItems;
import org.avp.common.sound.AVPSoundEvents;

import java.util.Map;

public class AVPArmorMaterials {

    public static final ArmorMaterial ALUMINUM = new AVPArmorMaterial.Builder(
        "aluminum",
        AVPSoundEvents.INSTANCE.itemArmorEquipAluminum,
        AVPItems.INSTANCE.ingotAluminum::get
    )
        .setProtectionValues(
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 2),
                Map.entry(ArmorItem.Type.CHESTPLATE, 5),
                Map.entry(ArmorItem.Type.LEGGINGS, 5),
                Map.entry(ArmorItem.Type.BOOTS, 2)
            )
        )
        .setDurabilityMultiplier(14)
        .setEnchantmentValue(10)
        .build();

    public static final ArmorMaterial MK50 = new AVPArmorMaterial.Builder(
        "mk50",
        AVPSoundEvents.INSTANCE.itemArmorEquipMK50,
        () -> Items.COPPER_INGOT
    )
        .setProtectionValues(
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 2),
                Map.entry(ArmorItem.Type.CHESTPLATE, 4),
                Map.entry(ArmorItem.Type.LEGGINGS, 3),
                Map.entry(ArmorItem.Type.BOOTS, 2)
            )
        )
        .setDurabilityMultiplier(24)
        .setEnchantmentValue(6)
        .build();

    public static final ArmorMaterial ORIONITE = new AVPArmorMaterial.Builder(
        "orionite",
        AVPSoundEvents.INSTANCE.itemArmorEquipOrionite,
        AVPItems.INSTANCE.ingotOrionite::get
    )
        .setProtectionValues(
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 3),
                Map.entry(ArmorItem.Type.CHESTPLATE, 8),
                Map.entry(ArmorItem.Type.LEGGINGS, 6),
                Map.entry(ArmorItem.Type.BOOTS, 3)
            )
        )
        .setDurabilityMultiplier(35)
        .setEnchantmentValue(17)
        .setToughness(2.5F)
        .setKnockbackResistance(0.05F)
        .build();

    public static final ArmorMaterial PRESSURE = new AVPArmorMaterial.Builder(
        "pressure",
        AVPSoundEvents.INSTANCE.itemArmorEquipPressure,
        AVPItems.INSTANCE.ingotAluminum::get
    )
        .setProtectionValues(
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 2),
                Map.entry(ArmorItem.Type.CHESTPLATE, 4),
                Map.entry(ArmorItem.Type.LEGGINGS, 3),
                Map.entry(ArmorItem.Type.BOOTS, 2)
            )
        )
        .setDurabilityMultiplier(22)
        .setEnchantmentValue(6)
        .build();

    public static final ArmorMaterial TACTICAL = new AVPArmorMaterial.Builder(
        "tactical",
        AVPSoundEvents.INSTANCE.itemArmorEquipTactical,
        AVPItems.INSTANCE.ingotAluminum::get
    )
        .setProtectionValues(
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 2),
                Map.entry(ArmorItem.Type.CHESTPLATE, 6),
                Map.entry(ArmorItem.Type.LEGGINGS, 3),
                Map.entry(ArmorItem.Type.BOOTS, 2)
            )
        )
        .setDurabilityMultiplier(26)
        .setEnchantmentValue(5)
        .build();

    public static final ArmorMaterial TITANIUM = new AVPArmorMaterial.Builder(
        "titanium",
        AVPSoundEvents.INSTANCE.itemArmorEquipTitanium,
        AVPItems.INSTANCE.ingotTitanium::get
    )
        .setProtectionValues(
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 2),
                Map.entry(ArmorItem.Type.CHESTPLATE, 6),
                Map.entry(ArmorItem.Type.LEGGINGS, 5),
                Map.entry(ArmorItem.Type.BOOTS, 2)
            )
        )
        .setDurabilityMultiplier(24)
        .setEnchantmentValue(9)
        .setToughness(1)
        .build();

    public static final ArmorMaterial VERITANIUM = new AVPArmorMaterial.Builder(
        "veritanium",
        AVPSoundEvents.INSTANCE.itemArmorEquipVeritanium,
        AVPItems.INSTANCE.veritaniumShard::get
    )
        .setProtectionValues(
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 4),
                Map.entry(ArmorItem.Type.CHESTPLATE, 9),
                Map.entry(ArmorItem.Type.LEGGINGS, 7),
                Map.entry(ArmorItem.Type.BOOTS, 4)
            )
        )
        .setDurabilityMultiplier(34)
        .setEnchantmentValue(0)
        .setToughness(5)
        .build();

    public static final ArmorMaterial XENOMORPH_CHITIN = new AVPArmorMaterial.Builder(
        "xenomorph_chitin",
        AVPSoundEvents.INSTANCE.itemArmorEquipXenomorphChitin,
        AVPItems.INSTANCE.xenomorphChitin::get
    )
        .setProtectionValues(
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 2),
                Map.entry(ArmorItem.Type.CHESTPLATE, 7),
                Map.entry(ArmorItem.Type.LEGGINGS, 5),
                Map.entry(ArmorItem.Type.BOOTS, 3)
            )
        )
        .setDurabilityMultiplier(30)
        .setEnchantmentValue(7)
        .build();

    private AVPArmorMaterials() {
        throw new UnsupportedOperationException();
    }
}
