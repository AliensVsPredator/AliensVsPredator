package org.avp.common.data.item.weapon;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.item.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.item.weapon.attack.HitscanWeaponAttack;
import org.avp.api.item.weapon.data.AmmunitionData;
import org.avp.api.item.weapon.data.FireModeData;
import org.avp.api.item.weapon.data.ReloadData;
import org.avp.api.item.weapon.data.ShootData;
import org.avp.api.item.weapon.data.WeaponData;
import org.avp.api.item.weapon.data.WindData;
import org.avp.api.item.weapon.reload.ReloadBehavior;
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
                    HEAVY_AMMUNITION_SUPPLIERS,
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
