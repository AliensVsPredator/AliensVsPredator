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

public class OldPainlessData extends WeaponData {

    public static final OldPainlessData INSTANCE = new OldPainlessData();

    private OldPainlessData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Automatic",
                1,
                2F * 2,
                0,
                0.02F,
                64,
                0,
                new AmmunitionData(
                    1,
                    Integer.MAX_VALUE,
                    HEAVY_AMMUNITION_SUPPLIERS,
                    HasAmmunitionBehavior.INVENTORY
                ),
                new ReloadData(
                    0,
                    Holder.empty(),
                    Holder.empty(),
                    ReloadBehavior.NO_OP
                ),
                new ShootData(
                    30,
                    AVPSoundEvents.INSTANCE.itemWeaponOldPainlessShootSpinning,
                    10,
                    AVPSoundEvents.INSTANCE.itemWeaponOldPainlessShootLoop
                ),
                new WindData(
                    20,
                    AVPSoundEvents.INSTANCE.itemWeaponOldPainlessShootStart,
                    AVPSoundEvents.INSTANCE.itemWeaponOldPainlessShootStop
                ),
                HitscanWeaponAttack::new
            )
        );
    }

    @Override
    public int getDurability() {
        return 2048 + 512;
    }
}
