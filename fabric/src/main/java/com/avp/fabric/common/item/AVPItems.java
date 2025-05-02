package com.avp.fabric.common.item;

import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;

import java.util.function.Function;

import com.avp.AVPResources;
import com.avp.fabric.common.item.gun.GunData;
import com.avp.fabric.common.item.old_painless.OldPainlessItem;

public class AVPItems {

    // Combat Items

    public static final Item F903WE_RIFLE = register(new GunItem(GunData.F903WE_RIFLE), "f903we_rifle");

    public static final Item FLAMETHROWER_SEVASTOPOL = register(
        new GunItem(GunData.FLAMETHROWER_SEVASTOPOL),
        "flamethrower_sevastopol"
    );

    public static final Item M37_12_SHOTGUN = register(new GunItem(GunData.M37_12_SHOTGUN), "m37_12_shotgun");

    public static final Item M41A_PULSE_RIFLE = register(new GunItem(GunData.M41A_PULSE_RIFLE), "m41a_pulse_rifle");

    public static final Item M42A3_SNIPER_RIFLE = register(new GunItem(GunData.M42A3_SNIPER_RIFLE), "m42a3_sniper_rifle");

    public static final Item M4RA_BATTLE_RIFLE = register(new GunItem(GunData.M4RA_BATTLE_RIFLE), "m4ra_battle_rifle");

    public static final Item M56_SMARTGUN = register(new GunItem(GunData.M56_SMARTGUN), "m56_smartgun");

    public static final Item M6B_ROCKET_LAUNCHER = register(new GunItem(GunData.M6B_ROCKET_LAUNCHER), "m6b_rocket_launcher");

    public static final Item M88MOD4_COMBAT_PISTOL = register(
        new GunItem(GunData.M88_MOD_4_COMBAT_PISTOL),
        "m88mod4_combat_pistol"
    );

    public static final Item OLD_PAINLESS = register(new OldPainlessItem(), "old_painless");

    public static final Item ZX_76_SHOTGUN = register(new GunItem(GunData.ZX_76_SHOTGUN), "zx_76_shotgun");

    public static final Item POISON_JELLY = register(new PoisionJellyItem(), "poison_jelly");

    public static Item register(Function<Item.Properties, Item> itemSupplier, String id) {
        return register(itemSupplier.apply(new Item.Properties()), id);
    }

    public static Item register(Item.Properties itemProperties, String id) {
        return register(new Item(itemProperties), id);
    }

    public static Item register(Item item, String id) {
        var resourceLocation = AVPResources.location(id);
        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
    }

    public static Item register(String id) {
        return register(new Item(new Item.Properties()), id);
    }

    public static void initialize() {
        AzIdentityRegistry.register(
            OLD_PAINLESS,
            F903WE_RIFLE,
            FLAMETHROWER_SEVASTOPOL,
            M4RA_BATTLE_RIFLE,
            M6B_ROCKET_LAUNCHER,
            M37_12_SHOTGUN,
            M41A_PULSE_RIFLE,
            M42A3_SNIPER_RIFLE,
            M56_SMARTGUN,
            M88MOD4_COMBAT_PISTOL,
            ZX_76_SHOTGUN
        );
    }
}
