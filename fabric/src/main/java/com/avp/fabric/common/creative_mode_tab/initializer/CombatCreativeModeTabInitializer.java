package com.avp.fabric.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.creative_mode_tab.CreativeModeTabs;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.item.ArmorItems;

public class CombatCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT_KEY).register(entries -> {
            CreativeModeTabUtil.accept(entries, AVPBlocks.AMMO_CHEST);
            CreativeModeTabUtil.accept(entries, AVPItems.GRENADE);
            CreativeModeTabUtil.accept(entries, AVPItems.GRENADE_INCENDIARY);
            CreativeModeTabUtil.accept(entries, AVPItems.GRENADE_IRRADIATED);
            CreativeModeTabUtil.accept(entries, AVPItems.CASELESS_BULLET);
            CreativeModeTabUtil.accept(entries, AVPItems.HEAVY_BULLET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.SMALL_BULLET);
            CreativeModeTabUtil.accept(entries, AVPItems.MEDIUM_BULLET);
            CreativeModeTabUtil.accept(entries, TempAVPItems.SHOTGUN_SHELL);
            CreativeModeTabUtil.accept(entries, TempAVPItems.ROCKET);
            CreativeModeTabUtil.accept(entries, AVPItems.FUEL_TANK);

            CreativeModeTabUtil.accept(entries, AVPItems.F903WE_RIFLE);
            CreativeModeTabUtil.accept(entries, AVPItems.FLAMETHROWER_SEVASTOPOL);
            CreativeModeTabUtil.accept(entries, AVPItems.M37_12_SHOTGUN);
            CreativeModeTabUtil.accept(entries, AVPItems.M41A_PULSE_RIFLE);
            CreativeModeTabUtil.accept(entries, AVPItems.M42A3_SNIPER_RIFLE);
            CreativeModeTabUtil.accept(entries, AVPItems.M4RA_BATTLE_RIFLE);
            CreativeModeTabUtil.accept(entries, AVPItems.M56_SMARTGUN);
            CreativeModeTabUtil.accept(entries, AVPItems.M6B_ROCKET_LAUNCHER);
            CreativeModeTabUtil.accept(entries, AVPItems.M88MOD4_COMBAT_PISTOL);
            CreativeModeTabUtil.accept(entries, AVPItems.OLD_PAINLESS);
            CreativeModeTabUtil.accept(entries, AVPItems.ZX_76_SHOTGUN);

            CreativeModeTabUtil.accept(entries, AVPItems.SHURIKEN);
            CreativeModeTabUtil.accept(entries, AVPItems.SMART_DISC);

            CreativeModeTabUtil.accept(entries, ArmorItems.MK50_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.MK50_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.MK50_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.MK50_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.PRESSURE_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.PRESSURE_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.PRESSURE_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.PRESSURE_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.STEEL_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.STEEL_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.STEEL_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.STEEL_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.TACTICAL_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.TACTICAL_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.TACTICAL_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.TACTICAL_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.TACTICAL_CAMO_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.TACTICAL_CAMO_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.TACTICAL_CAMO_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.TACTICAL_CAMO_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.TITANIUM_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.TITANIUM_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.TITANIUM_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.TITANIUM_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.CHITIN_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.CHITIN_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.CHITIN_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.CHITIN_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_CHITIN_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_CHITIN_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_CHITIN_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_CHITIN_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.ABERRANT_CHITIN_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.ABERRANT_CHITIN_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.ABERRANT_CHITIN_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.ABERRANT_CHITIN_BOOTS);

            // TODO: Re-implement these at some point in the future.
            // CreativeModeTabUtil.accept(entries, ArmorItems.IRRADIATED_CHITIN_HELMET);
            // CreativeModeTabUtil.accept(entries, ArmorItems.IRRADIATED_CHITIN_CHESTPLATE);
            // CreativeModeTabUtil.accept(entries, ArmorItems.IRRADIATED_CHITIN_LEGGINGS);
            // CreativeModeTabUtil.accept(entries, ArmorItems.IRRADIATED_CHITIN_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_ABERRANT_CHITIN_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_ABERRANT_CHITIN_BOOTS);

            // TODO: Re-implement these at some point in the future.
            // CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_IRRADIATED_CHITIN_HELMET);
            // CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE);
            // CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS);
            // CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.NETHER_CHITIN_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.NETHER_CHITIN_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.NETHER_CHITIN_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.NETHER_CHITIN_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_NETHER_CHITIN_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.PLATED_NETHER_CHITIN_BOOTS);

            CreativeModeTabUtil.accept(entries, ArmorItems.JUNGLE_PREDATOR_HELMET);
            CreativeModeTabUtil.accept(entries, ArmorItems.JUNGLE_PREDATOR_CHESTPLATE);
            CreativeModeTabUtil.accept(entries, ArmorItems.JUNGLE_PREDATOR_LEGGINGS);
            CreativeModeTabUtil.accept(entries, ArmorItems.JUNGLE_PREDATOR_BOOTS);
        });
    }
}
