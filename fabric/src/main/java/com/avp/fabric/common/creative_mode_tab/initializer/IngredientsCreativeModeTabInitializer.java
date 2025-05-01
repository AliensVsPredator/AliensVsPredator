package com.avp.fabric.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.creative_mode_tab.CreativeModeTabs;
import com.avp.fabric.common.item.AVPItems;

public class IngredientsCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS_KEY).register(entries -> {
            // Raw materials
            CreativeModeTabUtil.accept(entries, TempAVPItems.AUTUNITE_DUST);
            CreativeModeTabUtil.accept(entries, TempAVPItems.CARBON_DUST);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_BAUXITE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_BRASS);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_CRUDE_IRON);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_FERROBAUXITE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_GALENA);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_MONAZITE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_SILICA.get());
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_TITANIUM);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_ZINC);
            CreativeModeTabUtil.accept(entries, TempAVPItems.ALUMINUM_NUGGET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BRASS_NUGGET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.FERROALUMINUM_NUGGET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.LEAD_NUGGET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.STEEL_NUGGET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.TITANIUM_NUGGET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.URANIUM_NUGGET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.ZINC_NUGGET);
            // Refined materials
            CreativeModeTabUtil.accept(entries, TempAVPItems.ALUMINUM_INGOT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BRASS_INGOT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.FERROALUMINUM_INGOT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.LEAD_INGOT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.LITHIUM_DUST);
            CreativeModeTabUtil.accept(entries, TempAVPItems.NEODYMIUM_MAGNET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.STEEL_INGOT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.TITANIUM_INGOT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.URANIUM_INGOT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.ZINC_INGOT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.POLYMER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.REDSTONE_CRYSTAL);
            // Electronic materials
            CreativeModeTabUtil.accept(entries, TempAVPItems.BATTERY_PACK);
            CreativeModeTabUtil.accept(entries, TempAVPItems.CAPACITOR);
            CreativeModeTabUtil.accept(entries, TempAVPItems.CPU);
            CreativeModeTabUtil.accept(entries, TempAVPItems.DIODE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.INTEGRATED_CIRCUIT);
            CreativeModeTabUtil.accept(entries, TempAVPItems.LED);
            CreativeModeTabUtil.accept(entries, TempAVPItems.LED_DISPLAY);
            CreativeModeTabUtil.accept(entries, TempAVPItems.REGULATOR);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RESISTOR);
            CreativeModeTabUtil.accept(entries, TempAVPItems.TRANSISTOR);
            CreativeModeTabUtil.accept(entries, TempAVPItems.SERVO);
            CreativeModeTabUtil.accept(entries, TempAVPItems.SPEAKER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.NUCLEAR_BATTERY);

            // Blueprint materials
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_F903WE_RIFLE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_M37_12_SHOTGUN);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_M41A_PULSE_RIFLE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_M56_SMARTGUN);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_OLD_PAINLESS);
            CreativeModeTabUtil.accept(entries, TempAVPItems.BLUEPRINT_ZX_76_SHOTGUN);

            // Gun part materials
            CreativeModeTabUtil.accept(entries, TempAVPItems.BARREL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.GRIP);
            CreativeModeTabUtil.accept(entries, TempAVPItems.MINIGUN_BARREL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RECEIVER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.ROCKET_BARREL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.SMART_BARREL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.SMART_RECEIVER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.STOCK);

            // Ammo materials
            CreativeModeTabUtil.accept(entries, TempAVPItems.BULLET_TIP);
            CreativeModeTabUtil.accept(entries, TempAVPItems.SMALL_CASING);
            CreativeModeTabUtil.accept(entries, TempAVPItems.MEDIUM_CASING);
            CreativeModeTabUtil.accept(entries, TempAVPItems.HEAVY_CASING);
            CreativeModeTabUtil.accept(entries, TempAVPItems.SHOTGUN_CASING);
            CreativeModeTabUtil.accept(entries, TempAVPItems.CASELESS_CARTRIDGE);

            // Alien materials
            CreativeModeTabUtil.accept(entries, TempAVPItems.RESIN_BALL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.CHITIN);
            CreativeModeTabUtil.accept(entries, TempAVPItems.PLATED_CHITIN);
            CreativeModeTabUtil.accept(entries, AVPItems.NETHER_RESIN_BALL);
            CreativeModeTabUtil.accept(entries, AVPItems.NETHER_CHITIN);
            CreativeModeTabUtil.accept(entries, TempAVPItems.PLATED_NETHER_CHITIN);
            CreativeModeTabUtil.accept(entries, AVPItems.ABERRANT_RESIN_BALL);
            CreativeModeTabUtil.accept(entries, AVPItems.ABERRANT_CHITIN);
            CreativeModeTabUtil.accept(entries, AVPItems.PLATED_ABERRANT_CHITIN);
            CreativeModeTabUtil.accept(entries, TempAVPItems.IRRADIATED_RESIN_BALL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.IRRADIATED_CHITIN);
            CreativeModeTabUtil.accept(entries, TempAVPItems.PLATED_IRRADIATED_CHITIN);
            CreativeModeTabUtil.accept(entries, TempAVPItems.RAW_ROYAL_JELLY);
            CreativeModeTabUtil.accept(entries, AVPItems.POISON_JELLY);

            // Decorative materials
            CreativeModeTabUtil.accept(entries, AVPItems.OVOID_POTTERY_SHERD);
            CreativeModeTabUtil.accept(entries, AVPItems.PARASITE_POTTERY_SHERD);
            CreativeModeTabUtil.accept(entries, AVPItems.ROYALTY_POTTERY_SHERD);
            CreativeModeTabUtil.accept(entries, AVPItems.VECTOR_POTTERY_SHERD);
            CreativeModeTabUtil.accept(entries, AVPItems.ALIEN_MUSIC_DISC_1_FRAGMENT);
            CreativeModeTabUtil.accept(entries, AVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT);

            CreativeModeTabUtil.accept(entries, AVPItems.VERITANIUM_SHARD);
        });
    }
}
