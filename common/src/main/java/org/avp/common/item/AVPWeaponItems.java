package org.avp.common.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.api.item.ItemData;
import org.avp.api.item.model.ItemModelData;
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
import org.avp.common.tag.AVPItemTags;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

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

    private Holder<Item> createWeaponHolder(String registryName, Function<Item.Properties, Item> itemSupplier, Set<TagKey<Item>> tags) {
        var itemTags = new HashSet<>(tags);
        itemTags.add(AVPItemTags.GUNS);
        return createHolder(
            new ItemData(
                "weapon_" + registryName,
                ItemModelData.none(() -> itemSupplier.apply(new Item.Properties())),
                itemTags
            )
        );
    }

    private AVPWeaponItems() {
        grenadeIncendiary = createHolder("grenade_incendiary");
        grenadeM40 = createHolder("grenade_m40");

        // Small
        weaponM88Mod4CombatPistol = createWeaponHolder("m88mod4_combat_pistol", M88Mod4CombatPistolItem::new, Set.of(AVPItemTags.SMALL_GUNS));

        // Medium
        weapon3712Shotgun = createWeaponHolder("37_12_shotgun", M3712ShotgunItem::new, Set.of(AVPItemTags.MEDIUM_GUNS));
        weaponAk47 = createWeaponHolder("ak_47", AK47Item::new, Set.of(AVPItemTags.MEDIUM_GUNS));
        weaponF90Rifle = createWeaponHolder("f90_rifle", F90RifleItem::new, Set.of(AVPItemTags.MEDIUM_GUNS));
        weaponFlamethrowerSevastopol = createWeaponHolder("flamethrower_sevastopol", FlamethrowerSevastopolItem::new, Set.of(AVPItemTags.MEDIUM_GUNS));
        weaponM4Carbine = createWeaponHolder("m4_carbine", M4CarbineItem::new, Set.of(AVPItemTags.MEDIUM_GUNS));

        // Heavy
        weaponM41APulseRifle = createWeaponHolder("m41a_pulse_rifle", M41APulseRifleItem::new, Set.of(AVPItemTags.HEAVY_GUNS));

        // Uber
        weaponM56Smartgun = createWeaponHolder("m56_smartgun", M56SmartgunItem::new, Set.of(AVPItemTags.UBER_GUNS));
        weaponM83A2Sadar = createWeaponHolder("m83a2_sadar", M83A2SADARItem::new, Set.of(AVPItemTags.UBER_GUNS));
        weaponOldPainless = createWeaponHolder("old_painless", OldPainlessItem::new, Set.of(AVPItemTags.UBER_GUNS));
        weaponSniperRifle = createWeaponHolder("sniper_rifle", SniperRifleItem::new, Set.of(AVPItemTags.UBER_GUNS));
    }
}
