package com.avp.fabric.data.lang.en_us.provider;

import com.lib.common.model.TooltipCategoryType;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.data.TooltipTranslationKeys;

public class EnUsTooltipProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add("tooltip.avp.accuracy", "Accuracy: ");
        builder.add("tooltip.avp.ammunition", "Ammo: ");
        builder.add("tooltip.avp.ammunition_type", "Fires: ");
        builder.add("tooltip.avp.damage", "Damage: ");
        builder.add("tooltip.avp.fire_mode", "Fire Mode: ");
        builder.add("tooltip.avp.fire_rate", "Fire Rate: ");
        builder.add("tooltip.avp.knockback", "Knockback: ");
        builder.add("tooltip.avp.recoil", "Recoil: ");
        builder.add("tooltip.avp.capacity", "Capacity: ");

        builder.add(TooltipCategoryType.REQUIREMENTS.getTranslationKey(), "Requires");
        builder.add(TooltipCategoryType.WHEN_FULL_ARMOR_SET_EQUIPPED.getTranslationKey(), "Full Set Bonus");
        builder.add(TooltipCategoryType.WHEN_HELMET_EQUIPPED.getTranslationKey(), "Helmet Bonus");
        builder.add(TooltipCategoryType.WHEN_IN_INVENTORY.getTranslationKey(), "When In Inventory");
        builder.add(TooltipCategoryType.WHEN_PLACED_IN_WORLD.getTranslationKey(), "When Placed");
        builder.add(TooltipCategoryType.WHEN_USED.getTranslationKey(), "When Used");
        builder.add(TooltipCategoryType.WHEN_USED_ON_ARMOR_STAND.getTranslationKey(), "When Used On Armor Stand");

        builder.add(TooltipTranslationKeys.EFFECT_AUTO_EQUIP_ARMOR_SET, "Auto-Equips Armor Sets");
        builder.add(TooltipTranslationKeys.EFFECT_AUTO_EQUIP_ARMOR_STAND_ARMOR_SET, "Auto-Equips Armor Stand Armor Sets");
        builder.add(TooltipTranslationKeys.EFFECT_AUTO_STORE_IRRADIATED_ITEMS, "Irradiated Items Auto-Stored in Chest");
        builder.add(TooltipTranslationKeys.EFFECT_FIRE_RESISTANCE, "Fire Resistance");
        builder.add(TooltipTranslationKeys.EFFECT_GUNS_AUTO_RELOAD_FROM_CHEST, "Guns Auto-Reload Ammo from Chest");
        builder.add(TooltipTranslationKeys.EFFECT_JUMP_BOOST, "Jump Boost");
        builder.add(TooltipTranslationKeys.EFFECT_NEARBY_TURRETS_USE_AMMO_FROM_CHEST, "Nearby Turrets use Ammo from Chest");
        builder.add(TooltipTranslationKeys.EFFECT_PREVENTS_FACEHUGGING, "Prevents Facehugging");
        builder.add(TooltipTranslationKeys.EFFECT_RADIATION_RESISTANCE, "Radiation Resistance");
        builder.add(TooltipTranslationKeys.EFFECT_WATER_BREATHING, "Water Breathing");
        builder.add(TooltipTranslationKeys.EFFECT_SLOWNESS, "Slowness");
        builder.add(TooltipTranslationKeys.EFFECT_STRENGTH, "Strength");

        builder.add(TooltipTranslationKeys.REQUIRES_REDSTONE_POWER, "Redstone Power");
        builder.add(TooltipTranslationKeys.REQUIRES_NEARBY_AMMO_CHEST_WITH_AMMO, "Nearby Ammo Chest with Medium Bullets");
    };
}
