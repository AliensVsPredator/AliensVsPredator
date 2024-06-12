package org.avp.common.data.item.weapon;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.common.game.item.weapon.HitscanWeaponAttack;
import org.avp.api.weapon.data.AmmunitionData;
import org.avp.api.weapon.data.FireModeData;
import org.avp.api.weapon.data.ReloadData;
import org.avp.api.weapon.data.ShootData;
import org.avp.api.weapon.data.WeaponData;
import org.avp.api.weapon.data.WindData;
import org.avp.api.weapon.reload.ReloadBehavior;
import org.avp.common.game.sound.AVPSoundEventRegistry;

import java.util.List;

public class F90RifleData extends WeaponData {

    public static final F90RifleData INSTANCE = new F90RifleData();

    private F90RifleData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Automatic",
                1,
                2F * 2,
                2,
                0.13F,
                64,
                0,
                new AmmunitionData(
                    1,
                    32,
                    AVPAmmunitionSuppliers.RIFLE_AMMUNITION_SUPPLIERS,
                    HasAmmunitionBehavior.LOADED
                ),
                new ReloadData(
                    20 * 5,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponGenericReload,
                    BLHolder.empty(),
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    BLHolder.empty(),
                    0,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponAk47Shoot
                ),
                WindData.EMPTY,
                HitscanWeaponAttack::new
            )
        );
    }

    @Override
    public int getDurability() {
        return 1024 + 512 + 256;
    }
}
