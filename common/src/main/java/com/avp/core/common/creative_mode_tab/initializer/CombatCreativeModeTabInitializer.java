package com.avp.core.common.creative_mode_tab.initializer;

import com.avp.core.common.creative_mode_tab.AVPCreativeModeTabOutputAdapter;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class CombatCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> CONSUMER = output -> {
        var adaptedOutput = new AVPCreativeModeTabOutputAdapter(output);

        adaptedOutput.accept(AVPItems.CASELESS_BULLET);
        adaptedOutput.accept(AVPItems.HEAVY_BULLET);
        adaptedOutput.accept(AVPItems.SMALL_BULLET);
        adaptedOutput.accept(AVPItems.MEDIUM_BULLET);
        adaptedOutput.accept(AVPItems.SHOTGUN_BULLET);
        adaptedOutput.accept(AVPItems.ROCKET);
        adaptedOutput.accept(AVPItems.FUEL_TANK);

        adaptedOutput.accept(AVPItems.F903WE_RIFLE);
        adaptedOutput.accept(AVPItems.FLAMETHROWER_SEVASTOPOL);
        adaptedOutput.accept(AVPItems.M37_12_SHOTGUN);
        adaptedOutput.accept(AVPItems.M41A_PULSE_RIFLE);
        adaptedOutput.accept(AVPItems.M42A3_SNIPER_RIFLE);
        adaptedOutput.accept(AVPItems.M4RA_BATTLE_RIFLE);
        adaptedOutput.accept(AVPItems.M56_SMARTGUN);
        adaptedOutput.accept(AVPItems.M6B_ROCKET_LAUNCHER);
        adaptedOutput.accept(AVPItems.M88_MOD_4_COMBAT_PISTOL);
        adaptedOutput.accept(AVPItems.OLD_PAINLESS);
        adaptedOutput.accept(AVPItems.ZX_76_SHOTGUN);

        adaptedOutput.accept(ArmorItems.MK50_HELMET);
        adaptedOutput.accept(ArmorItems.MK50_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.MK50_LEGGINGS);
        adaptedOutput.accept(ArmorItems.MK50_BOOTS);

        adaptedOutput.accept(ArmorItems.PRESSURE_HELMET);
        adaptedOutput.accept(ArmorItems.PRESSURE_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.PRESSURE_LEGGINGS);
        adaptedOutput.accept(ArmorItems.PRESSURE_BOOTS);

        adaptedOutput.accept(ArmorItems.STEEL_HELMET);
        adaptedOutput.accept(ArmorItems.STEEL_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.STEEL_LEGGINGS);
        adaptedOutput.accept(ArmorItems.STEEL_BOOTS);

        adaptedOutput.accept(ArmorItems.TACTICAL_HELMET);
        adaptedOutput.accept(ArmorItems.TACTICAL_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.TACTICAL_LEGGINGS);
        adaptedOutput.accept(ArmorItems.TACTICAL_BOOTS);

        adaptedOutput.accept(ArmorItems.TITANIUM_HELMET);
        adaptedOutput.accept(ArmorItems.TITANIUM_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.TITANIUM_LEGGINGS);
        adaptedOutput.accept(ArmorItems.TITANIUM_BOOTS);

        adaptedOutput.accept(ArmorItems.CHITIN_HELMET);
        adaptedOutput.accept(ArmorItems.CHITIN_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.CHITIN_LEGGINGS);
        adaptedOutput.accept(ArmorItems.CHITIN_BOOTS);

        adaptedOutput.accept(ArmorItems.PLATED_CHITIN_HELMET);
        adaptedOutput.accept(ArmorItems.PLATED_CHITIN_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.PLATED_CHITIN_LEGGINGS);
        adaptedOutput.accept(ArmorItems.PLATED_CHITIN_BOOTS);

        adaptedOutput.accept(ArmorItems.NETHER_CHITIN_HELMET);
        adaptedOutput.accept(ArmorItems.NETHER_CHITIN_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.NETHER_CHITIN_LEGGINGS);
        adaptedOutput.accept(ArmorItems.NETHER_CHITIN_BOOTS);

        adaptedOutput.accept(ArmorItems.PLATED_NETHER_CHITIN_HELMET);
        adaptedOutput.accept(ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        adaptedOutput.accept(ArmorItems.PLATED_NETHER_CHITIN_BOOTS);

        adaptedOutput.accept(ArmorItems.JUNGLE_PREDATOR_HELMET);
        adaptedOutput.accept(ArmorItems.JUNGLE_PREDATOR_CHESTPLATE);
        adaptedOutput.accept(ArmorItems.JUNGLE_PREDATOR_LEGGINGS);
        adaptedOutput.accept(ArmorItems.JUNGLE_PREDATOR_BOOTS);
    };
}
