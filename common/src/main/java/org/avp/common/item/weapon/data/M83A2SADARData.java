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
import org.avp.common.item.weapon.attack.M83A2SADARProjectileWeaponAttack;
import org.avp.common.sound.AVPSoundEvents;

import java.util.List;
import java.util.Set;

public class M83A2SADARData extends WeaponData {

    public static final M83A2SADARData INSTANCE = new M83A2SADARData();

    private M83A2SADARData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Semi-Automatic",
                1,
                8F * 2,
                60,
                0.5F,
                100,
                1.5F,
                new AmmunitionData(
                    1,
                    4,
                    ROCKET_AMMUNITION_SUPPLIERS,
                    HasAmmunitionBehavior.LOADED
                ),
                new ReloadData(
                    20 * 5,
                    AVPSoundEvents.INSTANCE.itemWeaponRocketLauncherReloadStart,
                    AVPSoundEvents.INSTANCE.itemWeaponRocketLauncherReloadFinish,
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    Holder.empty(),
                    0,
                    AVPSoundEvents.INSTANCE.itemWeaponRocketLauncherShoot
                ),
                WindData.EMPTY,
                M83A2SADARProjectileWeaponAttack::new
            )
        );
    }

    @Override
    public int getDurability() {
        return 256;
    }
}
