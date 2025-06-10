package com.human.common.registry.init.item;

import com.human.common.gameplay.item.gun.GunData;
import net.minecraft.world.item.Item;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.service.Services;

public class HumanGunItems {

    public static final AVPDeferredHolder<Item> F903WE_RIFLE = AVPItems.register(
        "f903we_rifle",
        Services.BRIDGE.createGunSupplier(GunData.F903WE_RIFLE)
    );

    public static final AVPDeferredHolder<Item> FLAMETHROWER_SEVASTOPOL = AVPItems.register(
        "flamethrower_sevastopol",
        Services.BRIDGE.createGunSupplier(GunData.FLAMETHROWER_SEVASTOPOL)
    );

    public static final AVPDeferredHolder<Item> M37_12_SHOTGUN = AVPItems.register(
        "m37_12_shotgun",
        Services.BRIDGE.createGunSupplier(GunData.M37_12_SHOTGUN)
    );

    public static final AVPDeferredHolder<Item> M41A_PULSE_RIFLE = AVPItems.register(
        "m41a_pulse_rifle",
        Services.BRIDGE.createGunSupplier(GunData.M41A_PULSE_RIFLE)
    );

    public static final AVPDeferredHolder<Item> M42A3_SNIPER_RIFLE = AVPItems.register(
        "m42a3_sniper_rifle",
        Services.BRIDGE.createGunSupplier(GunData.M42A3_SNIPER_RIFLE)
    );

    public static final AVPDeferredHolder<Item> M4RA_BATTLE_RIFLE = AVPItems.register(
        "m4ra_battle_rifle",
        Services.BRIDGE.createGunSupplier(GunData.M4RA_BATTLE_RIFLE)
    );

    public static final AVPDeferredHolder<Item> M56_SMARTGUN = AVPItems.register(
        "m56_smartgun",
        Services.BRIDGE.createGunSupplier(GunData.M56_SMARTGUN)
    );

    public static final AVPDeferredHolder<Item> M6B_ROCKET_LAUNCHER = AVPItems.register(
        "m6b_rocket_launcher",
        Services.BRIDGE.createGunSupplier(GunData.M6B_ROCKET_LAUNCHER)
    );

    public static final AVPDeferredHolder<Item> M88MOD4_COMBAT_PISTOL = AVPItems.register(
        "m88mod4_combat_pistol",
        Services.BRIDGE.createGunSupplier(GunData.M88_MOD_4_COMBAT_PISTOL)
    );

    public static final AVPDeferredHolder<Item> OLD_PAINLESS = AVPItems.register(
        "old_painless",
        Services.BRIDGE.createOldPainlessSupplier()
    );

    public static final AVPDeferredHolder<Item> ZX_76_SHOTGUN = AVPItems.register(
        "zx_76_shotgun",
        Services.BRIDGE.createGunSupplier(GunData.ZX_76_SHOTGUN)
    );

    public static void initialize() {}
}
