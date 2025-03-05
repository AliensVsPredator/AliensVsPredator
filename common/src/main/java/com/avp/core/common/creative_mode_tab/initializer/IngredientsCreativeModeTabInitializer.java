package com.avp.core.common.creative_mode_tab.initializer;

import com.avp.core.common.creative_mode_tab.AVPCreativeModeTabOutputAdapter;
import com.avp.core.common.item.AVPItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class IngredientsCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> CONSUMER = output -> {
        var adaptedOutput = new AVPCreativeModeTabOutputAdapter(output);

        // Raw materials
        adaptedOutput.accept(AVPItems.AUTUNITE_DUST);
        adaptedOutput.accept(AVPItems.CARBON_DUST);
        adaptedOutput.accept(AVPItems.RAW_BAUXITE);
        adaptedOutput.accept(AVPItems.RAW_BRASS);
        adaptedOutput.accept(AVPItems.RAW_CRUDE_IRON);
        adaptedOutput.accept(AVPItems.RAW_FERROBAUXITE);
        adaptedOutput.accept(AVPItems.RAW_GALENA);
        adaptedOutput.accept(AVPItems.RAW_MONAZITE);
        adaptedOutput.accept(AVPItems.RAW_SILICA);
        adaptedOutput.accept(AVPItems.RAW_TITANIUM);
        adaptedOutput.accept(AVPItems.RAW_ZINC);
        // Refined materials
        adaptedOutput.accept(AVPItems.ALUMINUM_INGOT);
        adaptedOutput.accept(AVPItems.BRASS_INGOT);
        adaptedOutput.accept(AVPItems.FERROALUMINUM_INGOT);
        adaptedOutput.accept(AVPItems.LEAD_INGOT);
        adaptedOutput.accept(AVPItems.LITHIUM_DUST);
        adaptedOutput.accept(AVPItems.NEODYMIUM_MAGNET);
        adaptedOutput.accept(AVPItems.STEEL_INGOT);
        adaptedOutput.accept(AVPItems.TITANIUM_INGOT);
        adaptedOutput.accept(AVPItems.URANIUM_INGOT);
        adaptedOutput.accept(AVPItems.ZINC_INGOT);
        adaptedOutput.accept(AVPItems.POLYMER);
        // Electronic materials
        adaptedOutput.accept(AVPItems.BATTERY_PACK);
        adaptedOutput.accept(AVPItems.CAPACITOR);
        adaptedOutput.accept(AVPItems.CPU);
        adaptedOutput.accept(AVPItems.DIODE);
        adaptedOutput.accept(AVPItems.INTEGRATED_CIRCUIT);
        adaptedOutput.accept(AVPItems.LED);
        adaptedOutput.accept(AVPItems.LED_DISPLAY);
        adaptedOutput.accept(AVPItems.REGULATOR);
        adaptedOutput.accept(AVPItems.RESISTOR);
        adaptedOutput.accept(AVPItems.TRANSISTOR);

        // Blueprint materials
        adaptedOutput.accept(AVPItems.BLUEPRINT_F903WE_RIFLE);
        adaptedOutput.accept(AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL);
        adaptedOutput.accept(AVPItems.BLUEPRINT_M37_12_SHOTGUN);
        adaptedOutput.accept(AVPItems.BLUEPRINT_M41A_PULSE_RIFLE);
        adaptedOutput.accept(AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE);
        adaptedOutput.accept(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE);
        adaptedOutput.accept(AVPItems.BLUEPRINT_M56_SMARTGUN);
        adaptedOutput.accept(AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER);
        adaptedOutput.accept(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL);
        adaptedOutput.accept(AVPItems.BLUEPRINT_OLD_PAINLESS);
        adaptedOutput.accept(AVPItems.BLUEPRINT_ZX_76_SHOTGUN);

        // Gun part materials
        adaptedOutput.accept(AVPItems.BARREL);
        adaptedOutput.accept(AVPItems.GRIP);
        adaptedOutput.accept(AVPItems.MINIGUN_BARREL);
        adaptedOutput.accept(AVPItems.RECEIVER);
        adaptedOutput.accept(AVPItems.ROCKET_BARREL);
        adaptedOutput.accept(AVPItems.SMART_BARREL);
        adaptedOutput.accept(AVPItems.SMART_RECEIVER);
        adaptedOutput.accept(AVPItems.STOCK);

        // Ammo materials
        adaptedOutput.accept(AVPItems.BULLET_TIP);
        adaptedOutput.accept(AVPItems.SMALL_CASING);
        adaptedOutput.accept(AVPItems.MEDIUM_CASING);
        adaptedOutput.accept(AVPItems.HEAVY_CASING);
        adaptedOutput.accept(AVPItems.SHOTGUN_CASING);
        adaptedOutput.accept(AVPItems.CASELESS_CASING);

        // Alien materials
        adaptedOutput.accept(AVPItems.RESIN_BALL);
        adaptedOutput.accept(AVPItems.CHITIN);
        adaptedOutput.accept(AVPItems.PLATED_CHITIN);
        adaptedOutput.accept(AVPItems.NETHER_RESIN_BALL);
        adaptedOutput.accept(AVPItems.NETHER_CHITIN);
        adaptedOutput.accept(AVPItems.PLATED_NETHER_CHITIN);
        adaptedOutput.accept(AVPItems.RAW_ROYAL_JELLY);

        // Decorative materials
        adaptedOutput.accept(AVPItems.OVOID_POTTERY_SHERD);
        adaptedOutput.accept(AVPItems.PARASITE_POTTERY_SHERD);
        adaptedOutput.accept(AVPItems.ROYALTY_POTTERY_SHERD);
        adaptedOutput.accept(AVPItems.VECTOR_POTTERY_SHERD);

        adaptedOutput.accept(AVPItems.VERITANIUM_SHARD);
    };
}
