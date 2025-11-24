package com.avp.common.registry.init.creative_mode_tab.initializer;

import com.human.common.registry.init.item.HumanGunItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPItems;

public class CombatCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        CreativeModeTabUtil.accept(output, AVPBlocks.AMMO_CHEST);
        CreativeModeTabUtil.accept(output, AVPItems.GRENADE);
        CreativeModeTabUtil.accept(output, AVPItems.GRENADE_INCENDIARY);
        CreativeModeTabUtil.accept(output, AVPItems.GRENADE_IRRADIATED);
        CreativeModeTabUtil.accept(output, AVPItems.CASELESS_BULLET);
        CreativeModeTabUtil.accept(output, AVPItems.HEAVY_BULLET);
        CreativeModeTabUtil.accept(output, AVPItems.SMALL_BULLET);
        CreativeModeTabUtil.accept(output, AVPItems.MEDIUM_BULLET);
        CreativeModeTabUtil.accept(output, AVPItems.SHOTGUN_SHELL);
        CreativeModeTabUtil.accept(output, AVPItems.ROCKET);
        CreativeModeTabUtil.accept(output, AVPItems.FUEL_TANK);

        CreativeModeTabUtil.accept(output, HumanGunItems.F903WE_RIFLE);
        CreativeModeTabUtil.accept(output, HumanGunItems.FLAMETHROWER_SEVASTOPOL);
        CreativeModeTabUtil.accept(output, HumanGunItems.M37_12_SHOTGUN);
        CreativeModeTabUtil.accept(output, HumanGunItems.M41A_PULSE_RIFLE);
        CreativeModeTabUtil.accept(output, HumanGunItems.M42A3_SNIPER_RIFLE);
        CreativeModeTabUtil.accept(output, HumanGunItems.M4RA_BATTLE_RIFLE);
        CreativeModeTabUtil.accept(output, HumanGunItems.M56_SMARTGUN);
        CreativeModeTabUtil.accept(output, HumanGunItems.M6B_ROCKET_LAUNCHER);
        CreativeModeTabUtil.accept(output, HumanGunItems.M88MOD4_COMBAT_PISTOL);
        CreativeModeTabUtil.accept(output, HumanGunItems.OLD_PAINLESS);
        CreativeModeTabUtil.accept(output, HumanGunItems.ZX_76_SHOTGUN);

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

        CreativeModeTabUtil.accept(output, AVPArmorItems.WY_COMMANDO_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.WY_COMMANDO_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.WY_COMMANDO_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.WY_COMMANDO_BOOTS);

        CreativeModeTabUtil.accept(output, AVPArmorItems.WY_ELITE_HELMET);
        CreativeModeTabUtil.accept(output, AVPArmorItems.WY_ELITE_CHESTPLATE);
        CreativeModeTabUtil.accept(output, AVPArmorItems.WY_ELITE_LEGGINGS);
        CreativeModeTabUtil.accept(output, AVPArmorItems.WY_ELITE_BOOTS);
    };
}
