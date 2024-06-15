package org.avp.common.game.item.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;

import java.util.Map;

import org.avp.api.common.game.item.armor.CustomArmorMaterial;
import org.avp.common.game.sound.AVPSoundEventRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

public class AVPArmorMaterials {

    public static final ArmorMaterial ALUMINUM = new CustomArmorMaterial.Builder(
        "aluminum",
        AVPSoundEventRegistry.INSTANCE.itemArmorEquipAluminum,
        AVPItemRegistry.INSTANCE.ingotAluminum::get
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

    public static final ArmorMaterial MK50 = new CustomArmorMaterial.Builder(
        "mk50",
        AVPSoundEventRegistry.INSTANCE.itemArmorEquipMK50,
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

    public static final ArmorMaterial ORIONITE = new CustomArmorMaterial.Builder(
        "orionite",
        AVPSoundEventRegistry.INSTANCE.itemArmorEquipOrionite,
        AVPItemRegistry.INSTANCE.ingotOrionite::get
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

    public static final ArmorMaterial PRESSURE = new CustomArmorMaterial.Builder(
        "pressure",
        AVPSoundEventRegistry.INSTANCE.itemArmorEquipPressure,
        AVPItemRegistry.INSTANCE.ingotAluminum::get
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

    public static final ArmorMaterial TACTICAL = new CustomArmorMaterial.Builder(
        "tactical",
        AVPSoundEventRegistry.INSTANCE.itemArmorEquipTactical,
        AVPItemRegistry.INSTANCE.ingotAluminum::get
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

    public static final ArmorMaterial TITANIUM = new CustomArmorMaterial.Builder(
        "titanium",
        AVPSoundEventRegistry.INSTANCE.itemArmorEquipTitanium,
        AVPItemRegistry.INSTANCE.ingotTitanium::get
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

    public static final ArmorMaterial VERITANIUM = new CustomArmorMaterial.Builder(
        "veritanium",
        AVPSoundEventRegistry.INSTANCE.itemArmorEquipVeritanium,
        AVPItemRegistry.INSTANCE.veritaniumShard::get
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

    public static final ArmorMaterial XENOMORPH_CHITIN = new CustomArmorMaterial.Builder(
        "xenomorph_chitin",
        AVPSoundEventRegistry.INSTANCE.itemArmorEquipXenomorphChitin,
        AVPItemRegistry.INSTANCE.xenomorphChitin::get
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
