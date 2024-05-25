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

public class SniperRifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Semi-Automatic",
        1,
        30,
        AVPSoundEvents.INSTANCE.itemWeaponSniperRifleShoot,
        128,
        1.5F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1,
        AmmunitionStrategy.builder(
            6,
            List.of(
                AVPBulletItems.INSTANCE.bulletRifle.base()::get,
                AVPBulletItems.INSTANCE.bulletRifle.acid()::get,
                AVPBulletItems.INSTANCE.bulletRifle.electric()::get,
                AVPBulletItems.INSTANCE.bulletRifle.explosive()::get,
                AVPBulletItems.INSTANCE.bulletRifle.incendiary()::get,
                AVPBulletItems.INSTANCE.bulletRifle.penetration()::get
            )
        )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE),
        15F * 2,
        0.13F,
        ReloadStrategy.builder(20 * 7 + 10)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponGenericReload)
            .setTryReloadBehavior(ReloadBehavior.LOAD_INTO_WEAPON)
            .build(),
        ShootStrategy.builder().build()
    );

    public SniperRifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }
}
