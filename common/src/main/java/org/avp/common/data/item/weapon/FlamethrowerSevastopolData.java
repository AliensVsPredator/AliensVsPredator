package org.avp.common.data.item.weapon;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.common.weapon.data.AmmunitionData;
import org.avp.api.common.weapon.data.FireModeData;
import org.avp.api.common.weapon.data.ReloadData;
import org.avp.api.common.weapon.data.ShootData;
import org.avp.api.common.weapon.data.WeaponData;
import org.avp.api.common.weapon.data.WindData;
import org.avp.api.common.weapon.reload.ReloadBehavior;
import org.avp.common.game.sound.AVPSoundEventRegistry;
import org.avp.common.registry.item.AVPAmmunitionPartItemRegistry;
import org.avp.common.game.item.weapon.FlamethrowerProjectileWeaponAttack;

import java.util.List;

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
                    List.of(AVPAmmunitionPartItemRegistry.INSTANCE.ammoFlamethrower::get),
                    HasAmmunitionBehavior.LOADED
                ),
                new ReloadData(
                    20 * 5,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponFlamethrowerReloadStart,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponFlamethrowerReloadFinish,
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    BLHolder.empty(),
                    0,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponFlamethrowerShoot
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
