package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammunition.AmmunitionStrategy;
import org.avp.api.item.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.shoot.ShootStrategy;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

public class M88Mod4CombatPistolItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Semi-Automatic",
        1,
        7,
        AVPSoundEvents.INSTANCE.itemWeaponCombatPistolShoot,
        32,
        0
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1,
        AmmunitionStrategy.builder(
            18,
            List.of(
                AVPBulletItems.INSTANCE.bulletPistol.base()::get,
                AVPBulletItems.INSTANCE.bulletPistol.acid()::get,
                AVPBulletItems.INSTANCE.bulletPistol.electric()::get,
                AVPBulletItems.INSTANCE.bulletPistol.explosive()::get,
                AVPBulletItems.INSTANCE.bulletPistol.incendiary()::get,
                AVPBulletItems.INSTANCE.bulletPistol.penetration()::get
            )
        )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        512,
        List.of(FIRE_MODE),
        2F,
        0.06F,
        ReloadStrategy.builder(20 * 2)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponCombatPistolReload)
            .setTryReloadBehavior(ReloadBehavior.LOAD_INTO_WEAPON)
            .build(),
        ShootStrategy.builder().build()
    );

    public M88Mod4CombatPistolItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
