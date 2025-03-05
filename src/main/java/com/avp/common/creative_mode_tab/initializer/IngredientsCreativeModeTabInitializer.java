package com.avp.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.creative_mode_tab.CreativeModeTabs;
import com.avp.common.item.AVPItems;

public class IngredientsCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS_KEY).register(entries -> {
            // Raw materials
            entries.accept(AVPItems.AUTUNITE_DUST);
            entries.accept(AVPItems.CARBON_DUST);
            entries.accept(AVPItems.RAW_BAUXITE);
            entries.accept(AVPItems.RAW_BRASS);
            entries.accept(AVPItems.RAW_CRUDE_IRON);
            entries.accept(AVPItems.RAW_FERROBAUXITE);
            entries.accept(AVPItems.RAW_GALENA);
            entries.accept(AVPItems.RAW_MONAZITE);
            entries.accept(AVPItems.RAW_SILICA);
            entries.accept(AVPItems.RAW_TITANIUM);
            entries.accept(AVPItems.RAW_ZINC);
            // Refined materials
            entries.accept(AVPItems.ALUMINUM_INGOT);
            entries.accept(AVPItems.BRASS_INGOT);
            entries.accept(AVPItems.FERROALUMINUM_INGOT);
            entries.accept(AVPItems.LEAD_INGOT);
            entries.accept(AVPItems.LITHIUM_DUST);
            entries.accept(AVPItems.NEODYMIUM_MAGNET);
            entries.accept(AVPItems.STEEL_INGOT);
            entries.accept(AVPItems.TITANIUM_INGOT);
            entries.accept(AVPItems.URANIUM_INGOT);
            entries.accept(AVPItems.ZINC_INGOT);
            entries.accept(AVPItems.POLYMER);
            // Electronic materials
            entries.accept(AVPItems.BATTERY_PACK);
            entries.accept(AVPItems.CAPACITOR);
            entries.accept(AVPItems.CPU);
            entries.accept(AVPItems.DIODE);
            entries.accept(AVPItems.INTEGRATED_CIRCUIT);
            entries.accept(AVPItems.LED);
            entries.accept(AVPItems.LED_DISPLAY);
            entries.accept(AVPItems.REGULATOR);
            entries.accept(AVPItems.RESISTOR);
            entries.accept(AVPItems.TRANSISTOR);

            // Blueprint materials
            entries.accept(AVPItems.BLUEPRINT_F903WE_RIFLE);
            entries.accept(AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL);
            entries.accept(AVPItems.BLUEPRINT_M37_12_SHOTGUN);
            entries.accept(AVPItems.BLUEPRINT_M41A_PULSE_RIFLE);
            entries.accept(AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE);
            entries.accept(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE);
            entries.accept(AVPItems.BLUEPRINT_M56_SMARTGUN);
            entries.accept(AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER);
            entries.accept(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL);
            entries.accept(AVPItems.BLUEPRINT_OLD_PAINLESS);
            entries.accept(AVPItems.BLUEPRINT_ZX_76_SHOTGUN);

            // Gun part materials
            entries.accept(AVPItems.BARREL);
            entries.accept(AVPItems.GRIP);
            entries.accept(AVPItems.MINIGUN_BARREL);
            entries.accept(AVPItems.RECEIVER);
            entries.accept(AVPItems.ROCKET_BARREL);
            entries.accept(AVPItems.SMART_BARREL);
            entries.accept(AVPItems.SMART_RECEIVER);
            entries.accept(AVPItems.STOCK);

            // Ammo materials
            entries.accept(AVPItems.BULLET_TIP);
            entries.accept(AVPItems.SMALL_CASING);
            entries.accept(AVPItems.MEDIUM_CASING);
            entries.accept(AVPItems.HEAVY_CASING);
            entries.accept(AVPItems.SHOTGUN_CASING);
            entries.accept(AVPItems.CASELESS_CASING);

            // Alien materials
            entries.accept(AVPItems.RESIN_BALL);
            entries.accept(AVPItems.CHITIN);
            entries.accept(AVPItems.PLATED_CHITIN);
            entries.accept(AVPItems.NETHER_RESIN_BALL);
            entries.accept(AVPItems.NETHER_CHITIN);
            entries.accept(AVPItems.PLATED_NETHER_CHITIN);
            entries.accept(AVPItems.RAW_ROYAL_JELLY);

            // Decorative materials
            entries.accept(AVPItems.OVOID_POTTERY_SHERD);
            entries.accept(AVPItems.PARASITE_POTTERY_SHERD);
            entries.accept(AVPItems.ROYALTY_POTTERY_SHERD);
            entries.accept(AVPItems.VECTOR_POTTERY_SHERD);

            entries.accept(AVPItems.VERITANIUM_SHARD);
        });
    }
}
