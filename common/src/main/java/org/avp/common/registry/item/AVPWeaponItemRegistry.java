package org.avp.common.registry.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.avp.api.data.item.ItemData;
import org.avp.api.data.item.ItemModelData;
import org.avp.api.weapon.data.WeaponData;
import org.avp.api.registry.AVPDeferredItemRegistry;
import org.avp.api.registry.holder.BLHolder;
import org.avp.common.data.item.weapon.AK47Data;
import org.avp.common.data.item.weapon.F90RifleData;
import org.avp.common.data.item.weapon.FlamethrowerSevastopolData;
import org.avp.common.data.item.weapon.M3712ShotgunData;
import org.avp.common.data.item.weapon.M41APulseRifleData;
import org.avp.common.data.item.weapon.M4CarbineData;
import org.avp.common.data.item.weapon.M56SmartgunData;
import org.avp.common.data.item.weapon.M83A2SADARData;
import org.avp.common.data.item.weapon.M88Mod4CombatPistolData;
import org.avp.common.data.item.weapon.OldPainlessData;
import org.avp.common.data.item.weapon.SniperRifleData;
import org.avp.common.data.tag.AVPItemTags;
import org.avp.common.game.item.AbstractAVPWeaponItem;
import org.avp.common.game.item.GrenadeItem;

import java.util.HashSet;
import java.util.Set;

public class AVPWeaponItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPWeaponItemRegistry INSTANCE = new AVPWeaponItemRegistry();

    public final BLHolder<Item> grenadeIncendiary;

    public final BLHolder<Item> grenadeM40;

    public final BLHolder<Item> weaponM88Mod4CombatPistol;

    public final BLHolder<Item> weapon3712Shotgun;

    public final BLHolder<Item> weaponAk47;

    public final BLHolder<Item> weaponF90Rifle;

    public final BLHolder<Item> weaponFlamethrowerSevastopol;

    public final BLHolder<Item> weaponM4Carbine;

    public final BLHolder<Item> weaponM41APulseRifle;

    public final BLHolder<Item> weaponM56Smartgun;

    public final BLHolder<Item> weaponM83A2Sadar;

    public final BLHolder<Item> weaponOldPainless;

    public final BLHolder<Item> weaponSniperRifle;

    private BLHolder<Item> createWeaponHolder(String registryName, WeaponData weaponData, Set<TagKey<Item>> tags) {
        var itemTags = new HashSet<>(tags);
        itemTags.add(AVPItemTags.GUNS);
        return createHolder(
            new ItemData(
                "weapon_" + registryName,
                ItemModelData.none(() -> new AbstractAVPWeaponItem(new Item.Properties(), weaponData) {}),
                itemTags
            )
        );
    }

    private AVPWeaponItemRegistry() {
        grenadeIncendiary = createHolder("grenade_incendiary", properties -> new GrenadeItem(properties, true));
        grenadeM40 = createHolder("grenade_m40", GrenadeItem::new);

        // N/A
        weaponFlamethrowerSevastopol = createWeaponHolder("flamethrower_sevastopol", FlamethrowerSevastopolData.INSTANCE, Set.of());

        // Small
        weaponM88Mod4CombatPistol = createWeaponHolder(
            "m88mod4_combat_pistol",
            M88Mod4CombatPistolData.INSTANCE,
            Set.of(AVPItemTags.SMALL_GUNS)
        );

        // Medium
        weapon3712Shotgun = createWeaponHolder("37_12_shotgun", M3712ShotgunData.INSTANCE, Set.of(AVPItemTags.MEDIUM_GUNS));
        weaponAk47 = createWeaponHolder("ak_47", AK47Data.INSTANCE, Set.of(AVPItemTags.MEDIUM_GUNS));
        weaponF90Rifle = createWeaponHolder("f90_rifle", F90RifleData.INSTANCE, Set.of(AVPItemTags.MEDIUM_GUNS));
        weaponM4Carbine = createWeaponHolder("m4_carbine", M4CarbineData.INSTANCE, Set.of(AVPItemTags.MEDIUM_GUNS));

        // Heavy
        weaponM41APulseRifle = createWeaponHolder("m41a_pulse_rifle", M41APulseRifleData.INSTANCE, Set.of(AVPItemTags.HEAVY_GUNS));
        weaponSniperRifle = createWeaponHolder("sniper_rifle", SniperRifleData.INSTANCE, Set.of(AVPItemTags.HEAVY_GUNS));

        // Uber
        weaponM56Smartgun = createWeaponHolder("m56_smartgun", M56SmartgunData.INSTANCE, Set.of(AVPItemTags.UBER_GUNS));
        weaponM83A2Sadar = createWeaponHolder("m83a2_sadar", M83A2SADARData.INSTANCE, Set.of(AVPItemTags.UBER_GUNS));
        weaponOldPainless = createWeaponHolder("old_painless", OldPainlessData.INSTANCE, Set.of(AVPItemTags.UBER_GUNS));
    }
}
