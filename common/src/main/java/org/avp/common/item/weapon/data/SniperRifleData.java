package org.avp.common.item.weapon.data;

import org.avp.api.Holder;
import org.avp.api.item.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.item.weapon.attack.HitscanWeaponAttack;
import org.avp.api.item.weapon.data.AmmunitionData;
import org.avp.api.item.weapon.data.FireModeData;
import org.avp.api.item.weapon.data.ReloadData;
import org.avp.api.item.weapon.data.ShootData;
import org.avp.api.item.weapon.data.WeaponData;
import org.avp.api.item.weapon.data.WindData;
import org.avp.api.item.weapon.reload.ReloadBehavior;
import org.avp.common.sound.AVPSoundEvents;

import java.util.List;
import java.util.Set;

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
                    AVPSoundEvents.INSTANCE.itemWeaponGenericReload,
                    Holder.empty(),
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    Holder.empty(),
                    0,
                    AVPSoundEvents.INSTANCE.itemWeaponSniperRifleShoot
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
