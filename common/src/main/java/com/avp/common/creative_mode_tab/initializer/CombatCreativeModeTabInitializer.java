package com.avp.common.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.TempAVPItems;

public class CombatCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        CreativeModeTabUtil.accept(output, TempAVPBlocks.AMMO_CHEST);
        CreativeModeTabUtil.accept(output, TempAVPItems.GRENADE);
        CreativeModeTabUtil.accept(output, TempAVPItems.GRENADE_INCENDIARY);
        CreativeModeTabUtil.accept(output, TempAVPItems.GRENADE_IRRADIATED);
        CreativeModeTabUtil.accept(output, TempAVPItems.CASELESS_BULLET);
        CreativeModeTabUtil.accept(output, TempAVPItems.HEAVY_BULLET);
        CreativeModeTabUtil.accept(output, TempAVPItems.SMALL_BULLET);
        CreativeModeTabUtil.accept(output, TempAVPItems.MEDIUM_BULLET);
        CreativeModeTabUtil.accept(output, TempAVPItems.SHOTGUN_SHELL);
        CreativeModeTabUtil.accept(output, TempAVPItems.ROCKET);
        CreativeModeTabUtil.accept(output, TempAVPItems.FUEL_TANK);

        // FIXME:
        // CreativeModeTabUtil.accept(output, AVPItems.F903WE_RIFLE);
        // CreativeModeTabUtil.accept(output, AVPItems.FLAMETHROWER_SEVASTOPOL);
        // CreativeModeTabUtil.accept(output, AVPItems.M37_12_SHOTGUN);
        // CreativeModeTabUtil.accept(output, AVPItems.M41A_PULSE_RIFLE);
        // CreativeModeTabUtil.accept(output, AVPItems.M42A3_SNIPER_RIFLE);
        // CreativeModeTabUtil.accept(output, AVPItems.M4RA_BATTLE_RIFLE);
        // CreativeModeTabUtil.accept(output, AVPItems.M56_SMARTGUN);
        // CreativeModeTabUtil.accept(output, AVPItems.M6B_ROCKET_LAUNCHER);
        // CreativeModeTabUtil.accept(output, AVPItems.M88MOD4_COMBAT_PISTOL);
        // CreativeModeTabUtil.accept(output, AVPItems.OLD_PAINLESS);
        // CreativeModeTabUtil.accept(output, AVPItems.ZX_76_SHOTGUN);

        // CreativeModeTabUtil.accept(output, AVPItems.SHURIKEN);
        // CreativeModeTabUtil.accept(output, AVPItems.SMART_DISC);

        CreativeModeTabUtil.accept(output, AVPArmorItems.MK50_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.MK50_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.MK50_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.MK50_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.PRESSURE_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PRESSURE_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PRESSURE_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PRESSURE_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.STEEL_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.STEEL_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.STEEL_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.STEEL_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.TACTICAL_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TACTICAL_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TACTICAL_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TACTICAL_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.TACTICAL_CAMO_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TACTICAL_CAMO_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TACTICAL_CAMO_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TACTICAL_CAMO_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.TITANIUM_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TITANIUM_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TITANIUM_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.TITANIUM_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.CHITIN_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.CHITIN_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.CHITIN_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.CHITIN_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_CHITIN_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_CHITIN_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_CHITIN_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_CHITIN_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.ABERRANT_CHITIN_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.ABERRANT_CHITIN_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.ABERRANT_CHITIN_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.ABERRANT_CHITIN_BOOTS);

        // TODO: Re-implement these at some point in the future.
        // CreativeModeTabUtil.accept(output, AVPArmorItems.IRRADIATED_CHITIN_HELMET);
        // CreativeModeTabUtil.accept(output, AVPArmorItems.IRRADIATED_CHITIN_CHESTPLATE);
        // CreativeModeTabUtil.accept(output, AVPArmorItems.IRRADIATED_CHITIN_LEGGINGS);
        // CreativeModeTabUtil.accept(output, AVPArmorItems.IRRADIATED_CHITIN_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_ABERRANT_CHITIN_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_ABERRANT_CHITIN_BOOTS);

        // TODO: Re-implement these at some point in the future.
        // CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_IRRADIATED_CHITIN_HELMET);
        // CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE);
        // CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS);
        // CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.NETHER_CHITIN_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.NETHER_CHITIN_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.NETHER_CHITIN_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.NETHER_CHITIN_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_NETHER_CHITIN_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.PLATED_NETHER_CHITIN_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.JUNGLE_PREDATOR_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.JUNGLE_PREDATOR_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.JUNGLE_PREDATOR_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.JUNGLE_PREDATOR_BOOTS);
    };
}
