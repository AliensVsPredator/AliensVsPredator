package com.avp.core.common.armor;

import com.avp.core.platform.service.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.avp.core.AVPResources;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.sound.AVPSoundEvents;

public class ArmorMaterials {

    // Should be slightly stronger than iron.
    public static final Supplier<ArmorMaterial> CHITIN = register(
        "chitin",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        7,
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.CHITIN.get()),
        0,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> MK50 = register(
        "mk50",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, -2),
                Map.entry(ArmorItem.Type.LEGGINGS, -1)
            )
        ),
        6,
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.LEAD_INGOT.get()),
        0,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> NETHER_CHITIN = register(
        "nether_chitin",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, 1),
                Map.entry(ArmorItem.Type.BOOTS, 1)
            )
        ),
        7,
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.NETHER_CHITIN.get()),
        0,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> PLATED_CHITIN = register(
        "plated_chitin",
        relativeDefense(net.minecraft.world.item.ArmorMaterials.DIAMOND, Map.of()),
        7,
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.PLATED_CHITIN.get()),
        1,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> PLATED_NETHER_CHITIN = register(
        "plated_nether_chitin",
        relativeDefense(net.minecraft.world.item.ArmorMaterials.DIAMOND, Map.of()),
        7,
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.PLATED_NETHER_CHITIN.get()),
        1,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> PRESSURE = register(
        "pressure",
        relativeDefense(
            net.minecraft.world.item.ArmorMaterials.IRON,
            Map.ofEntries(
                Map.entry(ArmorItem.Type.CHESTPLATE, -2),
                Map.entry(ArmorItem.Type.LEGGINGS, -1)
            )
        ),
        6,
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.ALUMINUM_INGOT.get()),
        0,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> STEEL = register(
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
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.STEEL_INGOT.get()),
        0,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> TACTICAL = register(
        "tactical",
        Map.ofEntries(
            Map.entry(ArmorItem.Type.HELMET, 2),
            Map.entry(ArmorItem.Type.CHESTPLATE, 6),
            Map.entry(ArmorItem.Type.LEGGINGS, 3),
            Map.entry(ArmorItem.Type.BOOTS, 2)
        ),
        5,
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.STEEL_INGOT.get()),
        0,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> TITANIUM = register(
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
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.TITANIUM_INGOT.get()),
        1,
        0,
        false
    );

    public static final Supplier<ArmorMaterial> VERITANIUM = register(
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
        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
        () -> Ingredient.of(AVPItems.VERITANIUM_SHARD.get()),
        4,
        0.15F,
        false
    );

    private static Supplier<ArmorMaterial> register(
        String id,
        Map<ArmorItem.Type, Integer> defensePoints,
        int enchantability,
        SoundEvent equipSound,
        Supplier<Ingredient> repairIngredientSupplier,
        float toughness,
        float knockbackResistance,
        boolean dyeable
    ) {
        var resourceLocation = AVPResources.location(id);

        List<ArmorMaterial.Layer> layers = List.of(
            new ArmorMaterial.Layer(resourceLocation, "", dyeable)
        );

        // Register the material within the ArmorMaterials registry.
        return Services.REGISTRY.registerArmorMaterial(id, () -> new ArmorMaterial(
            defensePoints,
            enchantability,
            Holder.direct(equipSound),
            repairIngredientSupplier,
            layers,
            toughness,
            knockbackResistance
        ));
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
