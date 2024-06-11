package org.avp.common.data.item.weapon;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.item.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.item.weapon.attack.HitscanWeaponAttack;
import org.avp.api.item.weapon.data.AmmunitionData;
import org.avp.api.item.weapon.data.FireModeData;
import org.avp.api.item.weapon.data.ReloadData;
import org.avp.api.item.weapon.data.ShootData;
import org.avp.api.item.weapon.data.WeaponData;
import org.avp.api.item.weapon.data.WindData;
import org.avp.api.item.weapon.reload.ReloadBehavior;
import org.avp.common.game.sound.AVPSoundEventRegistry;

import java.util.List;

public class SniperRifleData extends WeaponData {

    public static final SniperRifleData INSTANCE = new SniperRifleData();

    private SniperRifleData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Semi-Automatic",
                1,
                15F * 2,
                30,
                0.13F,
                128,
                1.5F,
                new AmmunitionData(
                    1,
                    6,
                    RIFLE_AMMUNITION_SUPPLIERS,
                    HasAmmunitionBehavior.LOADED
                ),
                new ReloadData(
                    20 * 7 + 10,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponGenericReload,
                    BLHolder.empty(),
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    BLHolder.empty(),
                    0,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponSniperRifleShoot
                ),
                WindData.EMPTY,
                HitscanWeaponAttack::new
            )
        );
    }

    @Override
    public int getDurability() {
        return 512;
    }
}
