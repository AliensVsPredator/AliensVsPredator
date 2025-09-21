package com.avp.common.registry.init.creative_mode_tab.initializer;

import com.alien.common.registry.init.AlienItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.registry.init.item.AVPItems;

public class IngredientsCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        // Raw materials
        CreativeModeTabUtil.accept(output, AVPItems.AUTUNITE_DUST);
        CreativeModeTabUtil.accept(output, AVPItems.CARBON_DUST);
        CreativeModeTabUtil.accept(output, AVPItems.RAW_BAUXITE);
        CreativeModeTabUtil.accept(output, AVPItems.RAW_BRASS);
        CreativeModeTabUtil.accept(output, AVPItems.RAW_CRUDE_IRON);
        CreativeModeTabUtil.accept(output, AVPItems.RAW_FERROBAUXITE);
        CreativeModeTabUtil.accept(output, AVPItems.RAW_GALENA);
        CreativeModeTabUtil.accept(output, AVPItems.RAW_MONAZITE);
        CreativeModeTabUtil.accept(output, AVPItems.SILICON);
        CreativeModeTabUtil.accept(output, AVPItems.RAW_TITANIUM);
        CreativeModeTabUtil.accept(output, AVPItems.RAW_ZINC);
        CreativeModeTabUtil.accept(output, AVPItems.ALUMINUM_NUGGET);
        CreativeModeTabUtil.accept(output, AVPItems.BRASS_NUGGET);
        CreativeModeTabUtil.accept(output, AVPItems.FERROALUMINUM_NUGGET);
        CreativeModeTabUtil.accept(output, AVPItems.LEAD_NUGGET);
        CreativeModeTabUtil.accept(output, AVPItems.STEEL_NUGGET);
        CreativeModeTabUtil.accept(output, AVPItems.TITANIUM_NUGGET);
        CreativeModeTabUtil.accept(output, AVPItems.URANIUM_NUGGET);
        CreativeModeTabUtil.accept(output, AVPItems.ZINC_NUGGET);
        // Refined materials
        CreativeModeTabUtil.accept(output, AVPItems.ALUMINUM_INGOT);
        CreativeModeTabUtil.accept(output, AVPItems.BRASS_INGOT);
        CreativeModeTabUtil.accept(output, AVPItems.FERROALUMINUM_INGOT);
        CreativeModeTabUtil.accept(output, AVPItems.LEAD_INGOT);
        CreativeModeTabUtil.accept(output, AVPItems.LITHIUM_DUST);
        CreativeModeTabUtil.accept(output, AVPItems.NEODYMIUM_MAGNET);
        CreativeModeTabUtil.accept(output, AVPItems.STEEL_INGOT);
        CreativeModeTabUtil.accept(output, AVPItems.TITANIUM_INGOT);
        CreativeModeTabUtil.accept(output, AVPItems.URANIUM_INGOT);
        CreativeModeTabUtil.accept(output, AVPItems.ZINC_INGOT);
        CreativeModeTabUtil.accept(output, AVPItems.POLYMER);
        CreativeModeTabUtil.accept(output, AVPItems.REDSTONE_CRYSTAL);
        // Electronic materials
        CreativeModeTabUtil.accept(output, AVPItems.BATTERY_PACK);
        CreativeModeTabUtil.accept(output, AVPItems.CAPACITOR);
        CreativeModeTabUtil.accept(output, AVPItems.CPU);
        CreativeModeTabUtil.accept(output, AVPItems.DIODE);
        CreativeModeTabUtil.accept(output, AVPItems.INTEGRATED_CIRCUIT);
        CreativeModeTabUtil.accept(output, AVPItems.LED);
        CreativeModeTabUtil.accept(output, AVPItems.LED_DISPLAY);
        CreativeModeTabUtil.accept(output, AVPItems.REGULATOR);
        CreativeModeTabUtil.accept(output, AVPItems.RESISTOR);
        CreativeModeTabUtil.accept(output, AVPItems.TRANSISTOR);
        CreativeModeTabUtil.accept(output, AVPItems.SERVO);
        CreativeModeTabUtil.accept(output, AVPItems.SPEAKER);
        CreativeModeTabUtil.accept(output, AVPItems.NUCLEAR_BATTERY);

        // Blueprint materials
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_F903WE_RIFLE);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_M37_12_SHOTGUN);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_M41A_PULSE_RIFLE);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_M56_SMARTGUN);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_OLD_PAINLESS);
        CreativeModeTabUtil.accept(output, AVPItems.BLUEPRINT_ZX_76_SHOTGUN);

        // Gun part materials
        CreativeModeTabUtil.accept(output, AVPItems.BARREL);
        CreativeModeTabUtil.accept(output, AVPItems.GRIP);
        CreativeModeTabUtil.accept(output, AVPItems.MINIGUN_BARREL);
        CreativeModeTabUtil.accept(output, AVPItems.RECEIVER);
        CreativeModeTabUtil.accept(output, AVPItems.ROCKET_BARREL);
        CreativeModeTabUtil.accept(output, AVPItems.SMART_BARREL);
        CreativeModeTabUtil.accept(output, AVPItems.SMART_RECEIVER);
        CreativeModeTabUtil.accept(output, AVPItems.STOCK);

        // Alien materials
        CreativeModeTabUtil.accept(output, AlienItems.RESIN_BALL);
        CreativeModeTabUtil.accept(output, AlienItems.CHITIN);
        CreativeModeTabUtil.accept(output, AlienItems.PLATED_CHITIN);
        CreativeModeTabUtil.accept(output, AlienItems.NETHER_RESIN_BALL);
        CreativeModeTabUtil.accept(output, AlienItems.NETHER_CHITIN);
        CreativeModeTabUtil.accept(output, AlienItems.PLATED_NETHER_CHITIN);
        CreativeModeTabUtil.accept(output, AlienItems.ABERRANT_RESIN_BALL);
        CreativeModeTabUtil.accept(output, AlienItems.ABERRANT_CHITIN);
        CreativeModeTabUtil.accept(output, AlienItems.PLATED_ABERRANT_CHITIN);
        CreativeModeTabUtil.accept(output, AlienItems.IRRADIATED_RESIN_BALL);
        CreativeModeTabUtil.accept(output, AlienItems.IRRADIATED_CHITIN);
        CreativeModeTabUtil.accept(output, AlienItems.PLATED_IRRADIATED_CHITIN);
        CreativeModeTabUtil.accept(output, AlienItems.RAW_ROYAL_JELLY);
        CreativeModeTabUtil.accept(output, AlienItems.POISON_JELLY);

        // Decorative materials
        CreativeModeTabUtil.accept(output, AlienItems.OVOID_POTTERY_SHERD);
        CreativeModeTabUtil.accept(output, AlienItems.PARASITE_POTTERY_SHERD);
        CreativeModeTabUtil.accept(output, AlienItems.ROYALTY_POTTERY_SHERD);
        CreativeModeTabUtil.accept(output, AlienItems.VECTOR_POTTERY_SHERD);
        CreativeModeTabUtil.accept(output, AlienItems.ALIEN_MUSIC_DISC_1_FRAGMENT);
        CreativeModeTabUtil.accept(output, AVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT);

        CreativeModeTabUtil.accept(output, AVPItems.VERITANIUM_SHARD);
    };
}
