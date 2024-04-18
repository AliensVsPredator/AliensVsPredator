package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
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
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPWeaponItems extends AVPDeferredItemRegistry {

    public static final AVPWeaponItems INSTANCE = new AVPWeaponItems();

    public final Holder<Item> grenadeIncendiary;

    public final Holder<Item> grenadeM40;

    public final Holder<Item> weaponM88Mod4CombatPistol;

    public final Holder<Item> weapon3712Shotgun;

    public final Holder<Item> weaponAk47;

    public final Holder<Item> weaponF90Rifle;

    public final Holder<Item> weaponFlamethrowerSevastopol;

    public final Holder<Item> weaponM4Carbine;

    public final Holder<Item> weaponM41APulseRifle;

    public final Holder<Item> weaponM56Smartgun;

    public final Holder<Item> weaponM83A2Sadar;

    public final Holder<Item> weaponOldPainless;

    public final Holder<Item> weaponSniperRifle;

    private AVPWeaponItems() {
        grenadeIncendiary = createHolder("grenade_incendiary", () -> new Item(new Item.Properties()));
        grenadeM40 = createHolder("grenade_m40", () -> new Item(new Item.Properties()));

        weapon3712Shotgun = createHolder("weapon_37_12_shotgun", () -> new M3712ShotgunItem(new Item.Properties()));
        weaponM88Mod4CombatPistol = createHolder("weapon_m88mod4_combat_pistol", () -> new M88Mod4CombatPistolItem(new Item.Properties()));
        weaponAk47 = createHolder("weapon_ak_47", () -> new AK47Item(new Item.Properties()));
        weaponF90Rifle = createHolder("weapon_f90_rifle", () -> new F90RifleItem(new Item.Properties()));
        weaponFlamethrowerSevastopol = createHolder(
            "weapon_flamethrower_sevastopol",
            () -> new FlamethrowerSevastopolItem(new Item.Properties())
        );
        weaponM4Carbine = createHolder("weapon_m4_carbine", () -> new M4CarbineItem(new Item.Properties()));
        weaponM41APulseRifle = createHolder("weapon_m41a_pulse_rifle", () -> new M41APulseRifleItem(new Item.Properties()));
        weaponM56Smartgun = createHolder("weapon_m56_smartgun", () -> new M56SmartgunItem(new Item.Properties()));
        weaponM83A2Sadar = createHolder("weapon_m83a2_sadar", () -> new M83A2SADARItem(new Item.Properties()));
        weaponOldPainless = createHolder("weapon_old_painless", () -> new OldPainlessItem(new Item.Properties()));
        weaponSniperRifle = createHolder("weapon_sniper_rifle", () -> new SniperRifleItem(new Item.Properties()));
    }
}
