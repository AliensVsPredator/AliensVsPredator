package org.avp.common.item.armor.material;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPItems;

public class AVPArmorMaterials {

    public static final ArmorMaterial ALUMINUM = new AVPArmorMaterial.Builder(
        "aluminum",
        SoundEvents.ARMOR_EQUIP_IRON,
        AVPItems.INSTANCE.INGOT_ALUMINUM::get
    )
        .setProtectionValues(new int[] { 2, 5, 5, 2 })
        .setDurabilityMultiplier(14)
        .setEnchantmentValue(10)
        .build();

    public static final ArmorMaterial MK50 = new AVPArmorMaterial.Builder(
        "mk50",
        SoundEvents.ARMOR_EQUIP_CHAIN,
        () -> Items.COPPER_INGOT
    )
        .setProtectionValues(new int[] { 2, 4, 3, 2 })
        .setDurabilityMultiplier(24)
        .setEnchantmentValue(6)
        .build();

    public static final ArmorMaterial ORIONITE = new AVPArmorMaterial.Builder(
        "orionite",
        SoundEvents.ARMOR_EQUIP_DIAMOND,
        AVPItems.INSTANCE.INGOT_ORIONITE::get
    )
        .setProtectionValues(new int[] { 3, 6, 8, 3 })
        .setDurabilityMultiplier(35)
        .setEnchantmentValue(17)
        .setToughness(2.5F)
        .setKnockbackResistance(0.05F)
        .build();

    public static final ArmorMaterial PRESSURE = new AVPArmorMaterial.Builder(
        "pressure",
        SoundEvents.ARMOR_EQUIP_CHAIN,
        AVPItems.INSTANCE.INGOT_ALUMINUM::get
    )
        .setProtectionValues(new int[] { 2, 4, 3, 2 })
        .setDurabilityMultiplier(22)
        .setEnchantmentValue(6)
        .build();

    public static final ArmorMaterial TACTICAL = new AVPArmorMaterial.Builder(
        "tactical",
        SoundEvents.ARMOR_EQUIP_CHAIN,
        AVPItems.INSTANCE.INGOT_ALUMINUM::get
    )
        .setProtectionValues(new int[] { 2, 6, 3, 2 })
        .setDurabilityMultiplier(26)
        .setEnchantmentValue(5)
        .build();

    public static final ArmorMaterial TITANIUM = new AVPArmorMaterial.Builder(
        "titanium",
        SoundEvents.ARMOR_EQUIP_IRON,
        AVPItems.INSTANCE.INGOT_TITANIUM::get
    )
        .setProtectionValues(new int[] { 2, 5, 6, 2 })
        .setDurabilityMultiplier(24)
        .setEnchantmentValue(9)
        .setToughness(1.0F)
        .build();

    public static final ArmorMaterial VERITANIUM = new AVPArmorMaterial.Builder(
        "veritanium",
        SoundEvents.ARMOR_EQUIP_IRON,
        AVPItems.INSTANCE.YAUTJA_ARTIFACT::get
    )
        .setProtectionValues(new int[] { 4, 7, 9, 4 })
        .setDurabilityMultiplier(34)
        .setEnchantmentValue(20)
        .setToughness(5.0F)
        .build();

    public static final ArmorMaterial XENOMORPH_CHITIN = new AVPArmorMaterial.Builder(
        "xenomorph_chitin",
        SoundEvents.HONEY_BLOCK_STEP,
        AVPItems.INSTANCE.ROYAL_JELLY::get
    )
        .setProtectionValues(new int[] { 2, 7, 5, 3 })
        .setDurabilityMultiplier(30)
        .setEnchantmentValue(7)
        .build();

    private AVPArmorMaterials() {
        throw new UnsupportedOperationException();
    }
}
