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

public class M88Mod4CombatPistolData extends WeaponData {

    public static final M88Mod4CombatPistolData INSTANCE = new M88Mod4CombatPistolData();

    private M88Mod4CombatPistolData() {}

    @Override
    protected List<FireModeData> createFireModeData() {
        return List.of(
            new FireModeData(
                "Semi-Automatic",
                1,
                2F,
                7,
                0.06F,
                32,
                0,
                new AmmunitionData(
                    1,
                    18,
                    PISTOL_AMMUNITION_SUPPLIERS,
                    HasAmmunitionBehavior.LOADED
                ),
                new ReloadData(
                    20 * 2,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponCombatPistolReload,
                    BLHolder.empty(),
                    ReloadBehavior.LOAD_INTO_WEAPON
                ),
                new ShootData(
                    0,
                    BLHolder.empty(),
                    0,
                    AVPSoundEventRegistry.INSTANCE.itemWeaponCombatPistolShoot
                ),
                WindData.EMPTY,
                HitscanWeaponAttack::new
            )
        );
    }

    @Override
    public int getDurability() {
        return 512;
    }
}
