package org.avp.common.item.armor.material;

import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPItems;
import org.avp.common.sound.AVPSoundEvents;

public class AVPArmorMaterials {

    public static final ArmorMaterial ALUMINUM = new AVPArmorMaterial.Builder(
        "aluminum",
        AVPSoundEvents.INSTANCE.itemArmorEquipAluminum,
        AVPItems.INSTANCE.ingotAluminum::get
    )
        .setProtectionValues(new int[] { 2, 5, 5, 2 })
        .setDurabilityMultiplier(14)
        .setEnchantmentValue(10)
        .build();

    public static final ArmorMaterial MK50 = new AVPArmorMaterial.Builder(
        "mk50",
        AVPSoundEvents.INSTANCE.itemArmorEquipMK50,
        () -> Items.COPPER_INGOT
    )
        .setProtectionValues(new int[] { 2, 4, 3, 2 })
        .setDurabilityMultiplier(24)
        .setEnchantmentValue(6)
        .build();

    public static final ArmorMaterial ORIONITE = new AVPArmorMaterial.Builder(
        "orionite",
        AVPSoundEvents.INSTANCE.itemArmorEquipOrionite,
        AVPItems.INSTANCE.ingotOrionite::get
    )
        .setProtectionValues(new int[] { 3, 6, 8, 3 })
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
        .setProtectionValues(new int[] { 2, 4, 3, 2 })
        .setDurabilityMultiplier(22)
        .setEnchantmentValue(6)
        .build();

    public static final ArmorMaterial TACTICAL = new AVPArmorMaterial.Builder(
        "tactical",
        AVPSoundEvents.INSTANCE.itemArmorEquipTactical,
        AVPItems.INSTANCE.ingotAluminum::get
    )
        .setProtectionValues(new int[] { 2, 6, 3, 2 })
        .setDurabilityMultiplier(26)
        .setEnchantmentValue(5)
        .build();

    public static final ArmorMaterial TITANIUM = new AVPArmorMaterial.Builder(
        "titanium",
        AVPSoundEvents.INSTANCE.itemArmorEquipTitanium,
        AVPItems.INSTANCE.ingotTitanium::get
    )
        .setProtectionValues(new int[] { 2, 5, 6, 2 })
        .setDurabilityMultiplier(24)
        .setEnchantmentValue(9)
        .setToughness(1)
        .build();

    public static final ArmorMaterial VERITANIUM = new AVPArmorMaterial.Builder(
        "veritanium",
        AVPSoundEvents.INSTANCE.itemArmorEquipVeritanium,
        AVPItems.INSTANCE.veritaniumShard::get
    )
        .setProtectionValues(new int[] { 4, 7, 9, 4 })
        .setDurabilityMultiplier(34)
        .setEnchantmentValue(0)
        .setToughness(5)
        .build();

    public static final ArmorMaterial XENOMORPH_CHITIN = new AVPArmorMaterial.Builder(
        "xenomorph_chitin",
        AVPSoundEvents.INSTANCE.itemArmorEquipXenomorphChitin,
        AVPItems.INSTANCE.xenomorphChitin::get
    )
        .setProtectionValues(new int[] { 2, 7, 5, 3 })
        .setDurabilityMultiplier(30)
        .setEnchantmentValue(7)
        .build();

    private AVPArmorMaterials() {
        throw new UnsupportedOperationException();
    }
}
