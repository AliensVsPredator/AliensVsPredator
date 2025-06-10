package com.predator.common.registry.init;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.AVPArmorMaterials;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.init.item.AVPItems;

public class PredatorArmorMaterials {

    public static final AVPDeferredHolder<ArmorMaterial> VERITANIUM = AVPArmorMaterials.register(
        "veritanium",
        AVPArmorMaterials.relativeDefense(
            ArmorMaterials.NETHERITE,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 1),
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.LEGGINGS, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        6,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_VERITANIUM::getHolder,
        () -> Ingredient.of(AVPItems.VERITANIUM_SHARD.get()),
        4,
        0.15F,
        false
    );

    public static void initialize() {}
}
