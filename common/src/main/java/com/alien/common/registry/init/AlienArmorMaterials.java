package com.alien.common.registry.init;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.AVPArmorMaterials;
import com.avp.common.registry.init.AVPSoundEvents;

public class AlienArmorMaterials {

    // Should be slightly stronger than iron.
    public static final AVPDeferredHolder<ArmorMaterial> ABERRANT_CHITIN = AVPArmorMaterials.register(
        "aberrant_chitin",
        AVPArmorMaterials.relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        7,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN::getHolder,
        () -> Ingredient.of(AlienItems.ABERRANT_CHITIN.get()),
        0,
        0,
        false
    );

    // Should be slightly stronger than iron.
    public static final AVPDeferredHolder<ArmorMaterial> CHITIN = AVPArmorMaterials.register(
        "chitin",
        AVPArmorMaterials.relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        7,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN::getHolder,
        () -> Ingredient.of(AlienItems.CHITIN.get()),
        0,
        0,
        false
    );

    // Should be slightly stronger than iron.
    public static final AVPDeferredHolder<ArmorMaterial> IRRADIATED_CHITIN = AVPArmorMaterials.register(
        "irradiated_chitin",
        AVPArmorMaterials.relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        7,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN::getHolder,
        () -> Ingredient.of(AlienItems.IRRADIATED_CHITIN.get()),
        0,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> NETHER_CHITIN = AVPArmorMaterials.register(
        "nether_chitin",
        AVPArmorMaterials.relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        7,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN::getHolder,
        () -> Ingredient.of(AlienItems.NETHER_CHITIN.get()),
        0,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> PLATED_ABERRANT_CHITIN = AVPArmorMaterials.register(
        "plated_aberrant_chitin",
        AVPArmorMaterials.relativeDefense(net.minecraft.world.item.ArmorMaterials.DIAMOND, Map.of()),
        7,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN::getHolder,
        () -> Ingredient.of(AlienItems.PLATED_ABERRANT_CHITIN.get()),
        1,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> PLATED_CHITIN = AVPArmorMaterials.register(
        "plated_chitin",
        AVPArmorMaterials.relativeDefense(net.minecraft.world.item.ArmorMaterials.DIAMOND, Map.of()),
        7,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN::getHolder,
        () -> Ingredient.of(AlienItems.PLATED_CHITIN.get()),
        1,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> PLATED_IRRADIATED_CHITIN = AVPArmorMaterials.register(
        "plated_irradiated_chitin",
        AVPArmorMaterials.relativeDefense(net.minecraft.world.item.ArmorMaterials.DIAMOND, Map.of()),
        7,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN::getHolder,
        () -> Ingredient.of(AlienItems.PLATED_IRRADIATED_CHITIN.get()),
        1,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> PLATED_NETHER_CHITIN = AVPArmorMaterials.register(
        "plated_nether_chitin",
        AVPArmorMaterials.relativeDefense(net.minecraft.world.item.ArmorMaterials.DIAMOND, Map.of()),
        7,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN::getHolder,
        () -> Ingredient.of(AlienItems.PLATED_NETHER_CHITIN.get()),
        1,
        0,
        false
    );

    public static void initialize() {}
}
