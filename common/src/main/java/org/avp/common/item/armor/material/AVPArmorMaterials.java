package org.avp.common.item.armor.material;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPItems;

public class AVPArmorMaterials {

    private static final AVPArmorMaterials INSTANCE = new AVPArmorMaterials();

    public final ArmorMaterial ALUMINUM = new AVPArmorMaterial.Builder(
        "aluminum",
        SoundEvents.ARMOR_EQUIP_IRON,
        () -> AVPItems.INGOT_ALUMINUM.get()
    )
        .setProtectionValues(new int[] { 2, 5, 5, 2 })
        .setDurabilityMultiplier(14)
        .setEnchantmentValue(10)
        .build();

    public final ArmorMaterial CELTIC = new AVPArmorMaterial.Builder(
        "celtic",
        SoundEvents.ARMOR_EQUIP_IRON,
        () -> AVPItems.YAUTJA_ARTIFACT.get()
    )
        .setProtectionValues(new int[] { 4, 7, 9, 4 })
        .setDurabilityMultiplier(34)
        .setEnchantmentValue(20)
        .setToughness(5.0F)
        .build();

    public final ArmorMaterial MK50 = new AVPArmorMaterial.Builder(
        "mk50",
        SoundEvents.ARMOR_EQUIP_CHAIN,
        () -> Items.COPPER_INGOT
    )
        .setProtectionValues(new int[] { 2, 4, 3, 2 })
        .setDurabilityMultiplier(24)
        .setEnchantmentValue(6)
        .build();

    public final ArmorMaterial PRESSURE = new AVPArmorMaterial.Builder(
        "pressure",
        SoundEvents.ARMOR_EQUIP_CHAIN,
        () -> AVPItems.INGOT_ALUMINUM.get()
    )
        .setProtectionValues(new int[] { 2, 4, 3, 2 })
        .setDurabilityMultiplier(22)
        .setEnchantmentValue(6)
        .build();

    public final ArmorMaterial TACTICAL = new AVPArmorMaterial.Builder(
        "tactical",
        SoundEvents.ARMOR_EQUIP_CHAIN,
        () -> AVPItems.INGOT_ALUMINUM.get()
    )
        .setProtectionValues(new int[] { 2, 6, 3, 2 })
        .setDurabilityMultiplier(26)
        .setEnchantmentValue(5)
        .build();

    public final ArmorMaterial XENOMORPH_CHITIN = new AVPArmorMaterial.Builder(
        "xenomorph_chitin",
        SoundEvents.HONEY_BLOCK_STEP,
        () -> AVPItems.ROYAL_JELLY.get()
    )
        .setProtectionValues(new int[] { 2, 7, 5, 3 })
        .setDurabilityMultiplier(30)
        .setEnchantmentValue(7)
        .build();

    public static AVPArmorMaterials getInstance() {
        return INSTANCE;
    }
}
