package com.avp.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.creative_mode_tab.CreativeModeTabs;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;

public class CombatCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT_KEY).register(entries -> {
            entries.accept(AVPItems.CASELESS_BULLET);
            entries.accept(AVPItems.HEAVY_BULLET);
            entries.accept(AVPItems.SMALL_BULLET);
            entries.accept(AVPItems.MEDIUM_BULLET);
            entries.accept(AVPItems.SHOTGUN_BULLET);
            entries.accept(AVPItems.ROCKET);
            entries.accept(AVPItems.FUEL_TANK);

            entries.accept(AVPItems.F903WE_RIFLE);
            entries.accept(AVPItems.FLAMETHROWER_SEVASTOPOL);
            entries.accept(AVPItems.M37_12_SHOTGUN);
            entries.accept(AVPItems.M41A_PULSE_RIFLE);
            entries.accept(AVPItems.M42A3_SNIPER_RIFLE);
            entries.accept(AVPItems.M4RA_BATTLE_RIFLE);
            entries.accept(AVPItems.M56_SMARTGUN);
            entries.accept(AVPItems.M6B_ROCKET_LAUNCHER);
            entries.accept(AVPItems.M88_MOD_4_COMBAT_PISTOL);
            entries.accept(AVPItems.OLD_PAINLESS);
            entries.accept(AVPItems.ZX_76_SHOTGUN);

            entries.accept(ArmorItems.MK50_HELMET);
            entries.accept(ArmorItems.MK50_CHESTPLATE);
            entries.accept(ArmorItems.MK50_LEGGINGS);
            entries.accept(ArmorItems.MK50_BOOTS);

            entries.accept(ArmorItems.PRESSURE_HELMET);
            entries.accept(ArmorItems.PRESSURE_CHESTPLATE);
            entries.accept(ArmorItems.PRESSURE_LEGGINGS);
            entries.accept(ArmorItems.PRESSURE_BOOTS);

            entries.accept(ArmorItems.STEEL_HELMET);
            entries.accept(ArmorItems.STEEL_CHESTPLATE);
            entries.accept(ArmorItems.STEEL_LEGGINGS);
            entries.accept(ArmorItems.STEEL_BOOTS);

            entries.accept(ArmorItems.TACTICAL_HELMET);
            entries.accept(ArmorItems.TACTICAL_CHESTPLATE);
            entries.accept(ArmorItems.TACTICAL_LEGGINGS);
            entries.accept(ArmorItems.TACTICAL_BOOTS);

            entries.accept(ArmorItems.TITANIUM_HELMET);
            entries.accept(ArmorItems.TITANIUM_CHESTPLATE);
            entries.accept(ArmorItems.TITANIUM_LEGGINGS);
            entries.accept(ArmorItems.TITANIUM_BOOTS);

            entries.accept(ArmorItems.CHITIN_HELMET);
            entries.accept(ArmorItems.CHITIN_CHESTPLATE);
            entries.accept(ArmorItems.CHITIN_LEGGINGS);
            entries.accept(ArmorItems.CHITIN_BOOTS);

            entries.accept(ArmorItems.PLATED_CHITIN_HELMET);
            entries.accept(ArmorItems.PLATED_CHITIN_CHESTPLATE);
            entries.accept(ArmorItems.PLATED_CHITIN_LEGGINGS);
            entries.accept(ArmorItems.PLATED_CHITIN_BOOTS);

            entries.accept(ArmorItems.NETHER_CHITIN_HELMET);
            entries.accept(ArmorItems.NETHER_CHITIN_CHESTPLATE);
            entries.accept(ArmorItems.NETHER_CHITIN_LEGGINGS);
            entries.accept(ArmorItems.NETHER_CHITIN_BOOTS);

            entries.accept(ArmorItems.PLATED_NETHER_CHITIN_HELMET);
            entries.accept(ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
            entries.accept(ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
            entries.accept(ArmorItems.PLATED_NETHER_CHITIN_BOOTS);

            entries.accept(ArmorItems.JUNGLE_PREDATOR_HELMET);
            entries.accept(ArmorItems.JUNGLE_PREDATOR_CHESTPLATE);
            entries.accept(ArmorItems.JUNGLE_PREDATOR_LEGGINGS);
            entries.accept(ArmorItems.JUNGLE_PREDATOR_BOOTS);
        });
    }
}
