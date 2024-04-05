package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.item.weapon.AK47Item;
import org.avp.common.item.weapon.F90RifleItem;
import org.avp.common.item.weapon.FlamethrowerSevastopolItem;
import org.avp.common.item.weapon.M3712ShotgunItem;
import org.avp.common.item.weapon.M41APulseRifleItem;
import org.avp.common.item.weapon.M4CarbineItem;
import org.avp.common.item.weapon.M56SmartgunItem;
import org.avp.common.item.weapon.M83A2SADARItem;
import org.avp.common.item.weapon.M88Mod4CombatPistolItem;
import org.avp.common.item.weapon.OldPainlessItem;
import org.avp.common.item.weapon.SniperRifleItem;
import org.avp.common.service.Services;

/**
 * @author Boston Vanseghi
 */
public class AVPWeaponItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> GRENADE_INCENDIARY;

    public static final GameObject<Item> GRENADE_M40;

    public static final GameObject<Item> WEAPON_M88MOD4_COMBAT_PISTOL;

    public static final GameObject<Item> WEAPON_37_12_SHOTGUN;

    public static final GameObject<Item> WEAPON_AK_47;

    public static final GameObject<Item> WEAPON_F90_RIFLE;

    public static final GameObject<Item> WEAPON_FLAMETHROWER_SEVASTOPOL;

    public static final GameObject<Item> WEAPON_M4_CARBINE;

    public static final GameObject<Item> WEAPON_M41A_PULSE_RIFLE;

    public static final GameObject<Item> WEAPON_M56_SMARTGUN;

    public static final GameObject<Item> WEAPON_M83A2_SADAR;

    public static final GameObject<Item> WEAPON_OLD_PAINLESS;

    public static final GameObject<Item> WEAPON_SNIPER_RIFLE;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register(registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPWeaponItems() {}

    static {
        GRENADE_INCENDIARY = register("grenade_incendiary", () -> new Item(new Item.Properties()));
        GRENADE_M40 = register("grenade_m40", () -> new Item(new Item.Properties()));

        WEAPON_37_12_SHOTGUN = register("weapon_37_12_shotgun", () -> new M3712ShotgunItem(new Item.Properties()));
        WEAPON_M88MOD4_COMBAT_PISTOL = register("weapon_m88mod4_combat_pistol", () -> new M88Mod4CombatPistolItem(new Item.Properties()));
        WEAPON_AK_47 = register("weapon_ak_47", () -> new AK47Item(new Item.Properties()));
        WEAPON_F90_RIFLE = register("weapon_f90_rifle", () -> new F90RifleItem(new Item.Properties()));
        WEAPON_FLAMETHROWER_SEVASTOPOL = register(
            "weapon_flamethrower_sevastopol",
            () -> new FlamethrowerSevastopolItem(new Item.Properties())
        );
        WEAPON_M4_CARBINE = register("weapon_m4_carbine", () -> new M4CarbineItem(new Item.Properties()));
        WEAPON_M41A_PULSE_RIFLE = register("weapon_m41a_pulse_rifle", () -> new M41APulseRifleItem(new Item.Properties()));
        WEAPON_M56_SMARTGUN = register("weapon_m56_smartgun", () -> new M56SmartgunItem(new Item.Properties()));
        WEAPON_M83A2_SADAR = register("weapon_m83a2_sadar", () -> new M83A2SADARItem(new Item.Properties()));
        WEAPON_OLD_PAINLESS = register("weapon_old_painless", () -> new OldPainlessItem(new Item.Properties()));
        WEAPON_SNIPER_RIFLE = register("weapon_sniper_rifle", () -> new SniperRifleItem(new Item.Properties()));
    }
}
