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

public class M41APulseRifleData extends WeaponData {

    private static final float DAMAGE = 2F * 2;

    private static final int MAX_AMMUNITION = 99;

    private static final AmmunitionData AUTOMATIC_AMMUNITION_DATA = new AmmunitionData(
        1,
        MAX_AMMUNITION,
        CASELESS_AMMUNITION_SUPPLIERS,
        HasAmmunitionBehavior.LOADED
    );

    private static final AmmunitionData BURST_AMMUNITION_DATA = new AmmunitionData(
        4,
        MAX_AMMUNITION,
        CASELESS_AMMUNITION_SUPPLIERS,
        HasAmmunitionBehavior.LOADED
    );

    private static final ReloadData RELOAD_DATA = new ReloadData(
        20 * 3,
        AVPSoundEvents.INSTANCE.itemWeaponGenericReload,
        Holder.empty(),
        ReloadBehavior.LOAD_INTO_WEAPON
    );

    private static final ShootData SHOOT_DATA = new ShootData(
        0,
        Holder.empty(),
        0,
        AVPSoundEvents.INSTANCE.itemWeaponPulseRifleShoot
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
