package com.avp.common.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.item.TempAVPItems;

public class IngredientsCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        // Raw materials
        CreativeModeTabUtil.accept(output, TempAVPItems.AUTUNITE_DUST);
        CreativeModeTabUtil.accept(output, TempAVPItems.CARBON_DUST);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_BAUXITE);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_BRASS);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_CRUDE_IRON);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_FERROBAUXITE);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_GALENA);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_MONAZITE);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_SILICA.get());
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_TITANIUM);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_ZINC);
        CreativeModeTabUtil.accept(output, TempAVPItems.ALUMINUM_NUGGET);
        CreativeModeTabUtil.accept(output, TempAVPItems.BRASS_NUGGET);
        CreativeModeTabUtil.accept(output, TempAVPItems.FERROALUMINUM_NUGGET);
        CreativeModeTabUtil.accept(output, TempAVPItems.LEAD_NUGGET);
        CreativeModeTabUtil.accept(output, TempAVPItems.STEEL_NUGGET);
        CreativeModeTabUtil.accept(output, TempAVPItems.TITANIUM_NUGGET);
        CreativeModeTabUtil.accept(output, TempAVPItems.URANIUM_NUGGET);
        CreativeModeTabUtil.accept(output, TempAVPItems.ZINC_NUGGET);
        // Refined materials
        CreativeModeTabUtil.accept(output, TempAVPItems.ALUMINUM_INGOT);
        CreativeModeTabUtil.accept(output, TempAVPItems.BRASS_INGOT);
        CreativeModeTabUtil.accept(output, TempAVPItems.FERROALUMINUM_INGOT);
        CreativeModeTabUtil.accept(output, TempAVPItems.LEAD_INGOT);
        CreativeModeTabUtil.accept(output, TempAVPItems.LITHIUM_DUST);
        CreativeModeTabUtil.accept(output, TempAVPItems.NEODYMIUM_MAGNET);
        CreativeModeTabUtil.accept(output, TempAVPItems.STEEL_INGOT);
        CreativeModeTabUtil.accept(output, TempAVPItems.TITANIUM_INGOT);
        CreativeModeTabUtil.accept(output, TempAVPItems.URANIUM_INGOT);
        CreativeModeTabUtil.accept(output, TempAVPItems.ZINC_INGOT);
        CreativeModeTabUtil.accept(output, TempAVPItems.POLYMER);
        CreativeModeTabUtil.accept(output, TempAVPItems.REDSTONE_CRYSTAL);
        // Electronic materials
        CreativeModeTabUtil.accept(output, TempAVPItems.BATTERY_PACK);
        CreativeModeTabUtil.accept(output, TempAVPItems.CAPACITOR);
        CreativeModeTabUtil.accept(output, TempAVPItems.CPU);
        CreativeModeTabUtil.accept(output, TempAVPItems.DIODE);
        CreativeModeTabUtil.accept(output, TempAVPItems.INTEGRATED_CIRCUIT);
        CreativeModeTabUtil.accept(output, TempAVPItems.LED);
        CreativeModeTabUtil.accept(output, TempAVPItems.LED_DISPLAY);
        CreativeModeTabUtil.accept(output, TempAVPItems.REGULATOR);
        CreativeModeTabUtil.accept(output, TempAVPItems.RESISTOR);
        CreativeModeTabUtil.accept(output, TempAVPItems.TRANSISTOR);
        CreativeModeTabUtil.accept(output, TempAVPItems.SERVO);
        CreativeModeTabUtil.accept(output, TempAVPItems.SPEAKER);
        CreativeModeTabUtil.accept(output, TempAVPItems.NUCLEAR_BATTERY);

        // Blueprint materials
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_F903WE_RIFLE);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_M37_12_SHOTGUN);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_M41A_PULSE_RIFLE);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_M56_SMARTGUN);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_OLD_PAINLESS);
        CreativeModeTabUtil.accept(output, TempAVPItems.BLUEPRINT_ZX_76_SHOTGUN);

        // Gun part materials
        CreativeModeTabUtil.accept(output, TempAVPItems.BARREL);
        CreativeModeTabUtil.accept(output, TempAVPItems.GRIP);
        CreativeModeTabUtil.accept(output, TempAVPItems.MINIGUN_BARREL);
        CreativeModeTabUtil.accept(output, TempAVPItems.RECEIVER);
        CreativeModeTabUtil.accept(output, TempAVPItems.ROCKET_BARREL);
        CreativeModeTabUtil.accept(output, TempAVPItems.SMART_BARREL);
        CreativeModeTabUtil.accept(output, TempAVPItems.SMART_RECEIVER);
        CreativeModeTabUtil.accept(output, TempAVPItems.STOCK);

        // Ammo materials
        CreativeModeTabUtil.accept(output, TempAVPItems.BULLET_TIP);
        CreativeModeTabUtil.accept(output, TempAVPItems.SMALL_CASING);
        CreativeModeTabUtil.accept(output, TempAVPItems.MEDIUM_CASING);
        CreativeModeTabUtil.accept(output, TempAVPItems.HEAVY_CASING);
        CreativeModeTabUtil.accept(output, TempAVPItems.SHOTGUN_CASING);
        CreativeModeTabUtil.accept(output, TempAVPItems.CASELESS_CARTRIDGE);

        // Alien materials
        CreativeModeTabUtil.accept(output, TempAVPItems.RESIN_BALL);
        CreativeModeTabUtil.accept(output, TempAVPItems.CHITIN);
        CreativeModeTabUtil.accept(output, TempAVPItems.PLATED_CHITIN);
        // FIXME:
        // CreativeModeTabUtil.accept(output, AVPItems.NETHER_RESIN_BALL);
        // CreativeModeTabUtil.accept(output, AVPItems.NETHER_CHITIN);
        CreativeModeTabUtil.accept(output, TempAVPItems.PLATED_NETHER_CHITIN);
        // FIXME:
        // CreativeModeTabUtil.accept(output, AVPItems.ABERRANT_RESIN_BALL);
        // CreativeModeTabUtil.accept(output, AVPItems.ABERRANT_CHITIN);
        // CreativeModeTabUtil.accept(output, AVPItems.PLATED_ABERRANT_CHITIN);
        CreativeModeTabUtil.accept(output, TempAVPItems.IRRADIATED_RESIN_BALL);
        CreativeModeTabUtil.accept(output, TempAVPItems.IRRADIATED_CHITIN);
        CreativeModeTabUtil.accept(output, TempAVPItems.PLATED_IRRADIATED_CHITIN);
        CreativeModeTabUtil.accept(output, TempAVPItems.RAW_ROYAL_JELLY);
        // FIXME:
        // CreativeModeTabUtil.accept(output, AVPItems.POISON_JELLY);

        // Decorative materials
        CreativeModeTabUtil.accept(output, TempAVPItems.OVOID_POTTERY_SHERD);
        CreativeModeTabUtil.accept(output, TempAVPItems.PARASITE_POTTERY_SHERD);
        CreativeModeTabUtil.accept(output, TempAVPItems.ROYALTY_POTTERY_SHERD);
        CreativeModeTabUtil.accept(output, TempAVPItems.VECTOR_POTTERY_SHERD);
        // FIXME:
        // CreativeModeTabUtil.accept(output, AVPItems.ALIEN_MUSIC_DISC_1_FRAGMENT);
        // CreativeModeTabUtil.accept(output, AVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT);

        // FIXME:
        // CreativeModeTabUtil.accept(output, AVPItems.VERITANIUM_SHARD);
    };
}
