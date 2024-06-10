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
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.weapon.attack.FlamethrowerProjectileWeaponAttack;
import org.avp.common.sound.AVPSoundEvents;

import java.util.List;
import java.util.Set;

public class FlamethrowerSevastopolData extends WeaponData {

    public static final FlamethrowerSevastopolData INSTANCE = new FlamethrowerSevastopolData();

    private FlamethrowerSevastopolData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Automatic",
                1,
                1F,
                1,
                0.0F,
                16,
                0,
                new AmmunitionData(
                    1,
                    500,
                    List.of(AVPAmmunitionPartItems.INSTANCE.ammoFlamethrower::get),
                    HasAmmunitionBehavior.LOADED
                ),
                new ReloadData(
                    20 * 5,
                    AVPSoundEvents.INSTANCE.itemWeaponFlamethrowerReloadStart,
                    AVPSoundEvents.INSTANCE.itemWeaponFlamethrowerReloadFinish,
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    Holder.empty(),
                    0,
                    AVPSoundEvents.INSTANCE.itemWeaponFlamethrowerShoot
                ),
                WindData.EMPTY,
                FlamethrowerProjectileWeaponAttack::new
            )
        );
    }

    @Override
    public int getDurability() {
        return 2048;
    }
}
