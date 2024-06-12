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
                    AVPAmmunitionSuppliers.HEAVY_AMMUNITION_SUPPLIERS,
                    HasAmmunitionBehavior.INVENTORY
                ),
                new ReloadData(
                    0,
                    BLHolder.empty(),
                    BLHolder.empty(),
                    ReloadBehavior.LOAD_FROM_INVENTORY
                ),
                new ShootData(
                    30,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponOldPainlessShootSpinning,
                    10,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponOldPainlessShootLoop
                ),
                new WindData(
                    20,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponOldPainlessShootStart,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponOldPainlessShootStop
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
