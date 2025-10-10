package com.avp.common.registry.init;

import com.compat.CommonItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPArmorMaterials {

    public static final AVPDeferredHolder<ArmorMaterial> MK50 = register(
        "mk50",
        relativeDefense(
            ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, -2),
                Map.entry(ArmorItem.Type.LEGGINGS, -1)
            )
        ),
        6,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_MK50::getHolder,
        () -> Ingredient.of(CommonItemTags.INGOTS_LEAD),
        0,
        0,
        true
    );

    public static final AVPDeferredHolder<ArmorMaterial> PRESSURE = register(
        "pressure",
        relativeDefense(
            ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, -2),
                Map.entry(ArmorItem.Type.LEGGINGS, -1)
            )
        ),
        6,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_PRESSURE::getHolder,
        () -> Ingredient.of(CommonItemTags.INGOTS_ALUMINUM),
        0,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> STEEL = register(
        "steel",
        relativeDefense(
            ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 1),
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.LEGGINGS, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        5, // TODO:
        AVPSoundEvents.ITEM_ARMOR_EQUIP_STEEL::getHolder,
        () -> Ingredient.of(CommonItemTags.INGOTS_STEEL),
        0,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> TACTICAL = register(
        "tactical",
        Map.ofEntries(
            Map.entry(ArmorItem.Type.HELMET, 2),
            Map.entry(ArmorItem.Type.CHESTPLATE, 6),
            Map.entry(ArmorItem.Type.LEGGINGS, 3),
            Map.entry(ArmorItem.Type.BOOTS, 2)
        ),
        5,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL::getHolder,
        () -> Ingredient.of(CommonItemTags.INGOTS_STEEL),
        0,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> TITANIUM = register(
        "titanium",
        relativeDefense(
            ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 1),
                Map.entry(ArmorItem.Type.CHESTPLATE, 2),
                Map.entry(ArmorItem.Type.LEGGINGS, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        5,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_TITANIUM::getHolder,
        () -> Ingredient.of(CommonItemTags.INGOTS_TITANIUM),
        1,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> WY_COMMANDO = register(
        "wy_commando",
        Map.ofEntries(
            Map.entry(ArmorItem.Type.HELMET, 2),
            Map.entry(ArmorItem.Type.CHESTPLATE, 6),
            Map.entry(ArmorItem.Type.LEGGINGS, 3),
            Map.entry(ArmorItem.Type.BOOTS, 2)
        ),
        5,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL::getHolder,
        () -> Ingredient.of(CommonItemTags.INGOTS_STEEL),
        0,
        0,
        false
    );

    public static final AVPDeferredHolder<ArmorMaterial> WY_ELITE = register(
        "wy_elite",
        Map.ofEntries(
            Map.entry(ArmorItem.Type.HELMET, 2),
            Map.entry(ArmorItem.Type.CHESTPLATE, 6),
            Map.entry(ArmorItem.Type.LEGGINGS, 3),
            Map.entry(ArmorItem.Type.BOOTS, 2)
        ),
        5,
        AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL::getHolder,
        () -> Ingredient.of(CommonItemTags.INGOTS_STEEL),
        0,
        0,
        false
    );

    public static AVPDeferredHolder<ArmorMaterial> register(
        String id,
        Map<ArmorItem.Type, Integer> defensePoints,
        int enchantability,
        Supplier<Holder<SoundEvent>> equipSoundHolderSupplier,
        Supplier<Ingredient> repairIngredientSupplier,
        float toughness,
        float knockbackResistance,
        boolean dyeable
    ) {
        var resourceLocation = AVPResources.location(id);

        List<ArmorMaterial.Layer> layers = List.of(
            new ArmorMaterial.Layer(resourceLocation, "", dyeable)
        );

        return Services.REGISTRY.register(
            BuiltInRegistries.ARMOR_MATERIAL,
            id,
            () -> new ArmorMaterial(
                defensePoints,
                enchantability,
                equipSoundHolderSupplier.get(),
                repairIngredientSupplier,
                layers,
                toughness,
                knockbackResistance
            )
        );
    }

    public static Map<ArmorItem.Type, Integer> relativeDefense(
        Holder<ArmorMaterial> armorMaterialHolder,
        Map<ArmorItem.Type, Integer> additiveDefense
    ) {
        var armorMaterial = armorMaterialHolder.value();

        return Map.ofEntries(
            compute(ArmorItem.Type.HELMET, additiveDefense, armorMaterial),
            compute(ArmorItem.Type.CHESTPLATE, additiveDefense, armorMaterial),
            compute(ArmorItem.Type.LEGGINGS, additiveDefense, armorMaterial),
            compute(ArmorItem.Type.BOOTS, additiveDefense, armorMaterial)
        );
    }

    private static @NotNull Map.Entry<ArmorItem.Type, Integer> compute(
        ArmorItem.Type type,
        Map<ArmorItem.Type, Integer> additiveDefense,
        ArmorMaterial armorMaterial
    ) {
        return Map.entry(type, armorMaterial.getDefense(type) + additiveDefense.getOrDefault(type, 0));
    }

    public static void initialize() {}
}
