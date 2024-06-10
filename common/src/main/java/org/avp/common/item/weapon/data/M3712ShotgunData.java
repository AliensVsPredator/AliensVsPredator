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

public class M3712ShotgunData extends WeaponData {

    public static final M3712ShotgunData INSTANCE = new M3712ShotgunData();

    private M3712ShotgunData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Semi-Automatic",
                1,
                8F * 2,
                20,
                0.5F,
                6,
                1.5F,
                new AmmunitionData(
                    1,
                    6,
                    SHOTGUN_AMMUNITION_SUPPLIERS,
                    HasAmmunitionBehavior.LOADED
                ),
                new ReloadData(
                    20 * 4,
                    AVPSoundEvents.INSTANCE.itemWeaponGenericReload,
                    Holder.empty(),
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    Holder.empty(),
                    0,
                    AVPSoundEvents.INSTANCE.itemWeaponShotgunShoot
                ),
                WindData.EMPTY,
                HitscanWeaponAttack::new
            )
        );
    }

    @Override
    public int getDurability() {
        return 512 + 256;
    }
}
