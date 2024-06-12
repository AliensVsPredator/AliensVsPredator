package org.avp.common.data.item.weapon;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.weapon.data.AmmunitionData;
import org.avp.api.weapon.data.FireModeData;
import org.avp.api.weapon.data.ReloadData;
import org.avp.api.weapon.data.ShootData;
import org.avp.api.weapon.data.WeaponData;
import org.avp.api.weapon.data.WindData;
import org.avp.api.weapon.reload.ReloadBehavior;
import org.avp.common.game.item.weapon.M83A2SADARProjectileWeaponAttack;
import org.avp.common.game.sound.AVPSoundEventRegistry;

import java.util.List;

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
                    AVPAmmunitionSuppliers.ROCKET_AMMUNITION_SUPPLIERS,
                    HasAmmunitionBehavior.LOADED
                ),
                new ReloadData(
                    20 * 5,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponRocketLauncherReloadStart,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponRocketLauncherReloadFinish,
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    BLHolder.empty(),
                    0,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponRocketLauncherShoot
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
