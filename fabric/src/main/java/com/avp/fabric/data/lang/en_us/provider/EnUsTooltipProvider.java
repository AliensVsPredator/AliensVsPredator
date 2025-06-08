package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

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

        builder.add("tooltip.avp.mk50_suit.full_set_bonus", "Full Set Bonus:");
        builder.add("tooltip.avp.mk50_suit.radiation_resistance", "+ Radiation Resistance");
        builder.add("tooltip.avp.mk50_suit.water_breathing", "+ Water Breathing");
        builder.add("tooltip.avp.mk50_suit.slowness", "- Slowness");

        builder.add("tooltip.avp.pressure_suit.full_set_bonus", "Full Set Bonus:");
        builder.add("tooltip.avp.pressure_suit.water_breathing", "+ Water Breathing");

        builder.add("tooltip.avp.ammo_chest.in_inventory", "When In Inventory:");
        builder.add("tooltip.avp.ammo_chest.reload_from_chest", "+ Guns Auto-Reload Ammo from Chest");
        builder.add("tooltip.avp.ammo_chest.placed", "When Placed:");
        builder.add("tooltip.avp.ammo_chest.turret_load_from_chest", "+ Nearby Turrets use Ammo from Chest");

        builder.add("tooltip.avp.lead_chest.in_inventory", "When In Inventory:");
        builder.add("tooltip.avp.lead_chest.auto_store_irradiated_items", "+ Irradiated Items Auto-Stored in Chest");

        builder.add("tooltip.avp.sentry_turret.requires", "Requires:");
        builder.add("tooltip.avp.sentry_turret.redstone_power_requirement", "- Redstone Power");
        builder.add(
            "tooltip.avp.sentry_turret.nearby_ammo_chest_with_ammo_requirement",
            "- Nearby Ammo Chest with Medium Bullets"
        );
    };
}
