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

public class M56SmartgunData extends WeaponData {

    public static final M56SmartgunData INSTANCE = new M56SmartgunData();

    private M56SmartgunData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Automatic",
                1,
                2F * 2,
                1,
                0.13F,
                64,
                0,
                new AmmunitionData(
                    1,
                    500,
                    CASELESS_AMMUNITION_SUPPLIERS,
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
        return 2048 + 512;
    }
}
