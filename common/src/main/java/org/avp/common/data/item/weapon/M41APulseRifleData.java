package org.avp.common.data.item.weapon;

import java.util.List;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.common.weapon.data.AmmunitionData;
import org.avp.api.common.weapon.data.FireModeData;
import org.avp.api.common.weapon.data.ReloadData;
import org.avp.api.common.weapon.data.ShootData;
import org.avp.api.common.weapon.data.WeaponData;
import org.avp.api.common.weapon.data.WindData;
import org.avp.api.common.weapon.reload.ReloadBehavior;
import org.avp.common.game.item.weapon.HitscanWeaponAttack;
import org.avp.common.game.sound.AVPSoundEventRegistry;

public class M41APulseRifleData extends WeaponData {

    private static final float DAMAGE = 2F * 2;

    private static final int MAX_AMMUNITION = 99;

    private static final AmmunitionData AUTOMATIC_AMMUNITION_DATA = new AmmunitionData(
        1,
        MAX_AMMUNITION,
        AVPAmmunitionSuppliers.CASELESS_AMMUNITION_SUPPLIERS,
        HasAmmunitionBehavior.LOADED
    );

    private static final AmmunitionData BURST_AMMUNITION_DATA = new AmmunitionData(
        4,
        MAX_AMMUNITION,
        AVPAmmunitionSuppliers.CASELESS_AMMUNITION_SUPPLIERS,
        HasAmmunitionBehavior.LOADED
    );

    private static final ReloadData RELOAD_DATA = new ReloadData(
        20 * 3,
        AVPSoundEventRegistry.INSTANCE.itemWeaponGenericReload,
        BLHolder.empty(),
        ReloadBehavior.LOAD_INTO_WEAPON
    );

    private static final ShootData SHOOT_DATA = new ShootData(
        0,
        BLHolder.empty(),
        0,
        AVPSoundEventRegistry.INSTANCE.itemWeaponPulseRifleShoot
    );

    public static final M41APulseRifleData INSTANCE = new M41APulseRifleData();

    private M41APulseRifleData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Burst",
                1,
                DAMAGE,
                10,
                0.13F,
                64,
                1,
                BURST_AMMUNITION_DATA,
                RELOAD_DATA,
                SHOOT_DATA,
                WindData.EMPTY,
                HitscanWeaponAttack::new
            ),
            new FireModeData(
                "Automatic",
                1,
                DAMAGE,
                2,
                0.13F,
                64,
                0.25F,
                AUTOMATIC_AMMUNITION_DATA,
                RELOAD_DATA,
                SHOOT_DATA,
                WindData.EMPTY,
                HitscanWeaponAttack::new
            )
        );
    }

    @Override
    public int getDurability() {
        return 2048;
    }
}
