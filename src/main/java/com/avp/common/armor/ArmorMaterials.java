package com.avp.common.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.common.item.AVPItems;
import com.avp.common.sound.AVPSoundEvents;

public class ArmorMaterials {

    // Should be slightly stronger than iron.
    public static final Holder<ArmorMaterial> CHITIN = register(
        "chitin",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        7,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN),
        () -> Ingredient.of(AVPItems.CHITIN),
        0,
        0,
        false
    );

    public static final Holder<ArmorMaterial> MK50 = register(
        "mk50",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, -2),
                Map.entry(ArmorItem.Type.LEGGINGS, -1)
            )
        ),
        6,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_MK50),
        () -> Ingredient.of(AVPItems.LEAD_INGOT),
        0,
        0,
        false
    );

    public static final Holder<ArmorMaterial> NETHER_CHITIN = register(
        "nether_chitin",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        7,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN),
        () -> Ingredient.of(AVPItems.NETHER_CHITIN),
        0,
        0,
        false
    );

    public static final Holder<ArmorMaterial> PLATED_CHITIN = register(
        "plated_chitin",
        relativeDefense(net.minecraft.world.item.ArmorMaterials.DIAMOND, Map.of()),
        7,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN),
        () -> Ingredient.of(AVPItems.PLATED_CHITIN),
        1,
        0,
        false
    );

    public static final Holder<ArmorMaterial> PLATED_NETHER_CHITIN = register(
        "plated_nether_chitin",
        relativeDefense(net.minecraft.world.item.ArmorMaterials.DIAMOND, Map.of()),
        7,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN),
        () -> Ingredient.of(AVPItems.PLATED_NETHER_CHITIN),
        1,
        0,
        false
    );

    public static final Holder<ArmorMaterial> PRESSURE = register(
        "pressure",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, -2),
                Map.entry(ArmorItem.Type.LEGGINGS, -1)
            )
        ),
        6,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_PRESSURE),
        () -> Ingredient.of(AVPItems.ALUMINUM_INGOT),
        0,
        0,
        false
    );

    public static final Holder<ArmorMaterial> STEEL = register(
        "steel",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 1),
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.LEGGINGS, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        5, // TODO:
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_STEEL),
        () -> Ingredient.of(AVPItems.STEEL_INGOT),
        0,
        0,
        false
    );

    public static final Holder<ArmorMaterial> TACTICAL = register(
        "tactical",
        Map.ofEntries(
            Map.entry(ArmorItem.Type.HELMET, 2),
            Map.entry(ArmorItem.Type.CHESTPLATE, 6),
            Map.entry(ArmorItem.Type.LEGGINGS, 3),
            Map.entry(ArmorItem.Type.BOOTS, 2)
        ),
        5,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL),
        () -> Ingredient.of(AVPItems.STEEL_INGOT),
        0,
        0,
        false
    );

    public static final Holder<ArmorMaterial> TITANIUM = register(
        "titanium",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 1),
                Map.entry(ArmorItem.Type.CHESTPLATE, 2),
                Map.entry(ArmorItem.Type.LEGGINGS, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        5,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_TITANIUM),
        () -> Ingredient.of(AVPItems.TITANIUM_INGOT),
        1,
        0,
        false
    );

    public static final Holder<ArmorMaterial> VERITANIUM = register(
        "veritanium",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.NETHERITE,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.HELMET, 1),
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.LEGGINGS, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        6,
        Holder.direct(AVPSoundEvents.ITEM_ARMOR_EQUIP_VERITANIUM),
        () -> Ingredient.of(AVPItems.VERITANIUM_SHARD),
        4,
        0.15F,
        false
    );

    private static Holder<ArmorMaterial> register(
        String id,
        Map<ArmorItem.Type, Integer> defensePoints,
        int enchantability,
        Holder<SoundEvent> equipSound,
        Supplier<Ingredient> repairIngredientSupplier,
        float toughness,
        float knockbackResistance,
        boolean dyeable
    ) {
        var resourceLocation = AVPResources.location(id);

        List<ArmorMaterial.Layer> layers = List.of(
            new ArmorMaterial.Layer(resourceLocation, "", dyeable)
        );

        var material = new ArmorMaterial(
            defensePoints,
            enchantability,
            equipSound,
            repairIngredientSupplier,
            layers,
            toughness,
            knockbackResistance
        );
        // Register the material within the ArmorMaterials registry.
        material = Registry.register(BuiltInRegistries.ARMOR_MATERIAL, resourceLocation, material);

        return Holder.direct(material);
    }

    private static Map<ArmorItem.Type, Integer> relativeDefense(
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

    private static Map.@NotNull Entry<ArmorItem.Type, Integer> compute(
        ArmorItem.Type type,
        Map<ArmorItem.Type, Integer> additiveDefense,
        ArmorMaterial armorMaterial
    ) {
        return Map.entry(type, armorMaterial.getDefense(type) + additiveDefense.getOrDefault(type, 0));
    }
}
