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

public class M56SmartgunItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Automatic",
        1,
        1,
        AVPSoundEvents.INSTANCE.itemWeaponAk47Shoot,
        64,
        0
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1,
        AmmunitionStrategy.builder(
            500,
            List.of(
                AVPBulletItems.INSTANCE.bulletCaseless.base()::get,
                AVPBulletItems.INSTANCE.bulletCaseless.acid()::get,
                AVPBulletItems.INSTANCE.bulletCaseless.electric()::get,
                AVPBulletItems.INSTANCE.bulletCaseless.explosive()::get,
                AVPBulletItems.INSTANCE.bulletCaseless.incendiary()::get,
                AVPBulletItems.INSTANCE.bulletCaseless.penetration()::get
            )
        )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        2560,
        List.of(FIRE_MODE),
        2F * 2,
        0.13F,
        ReloadStrategy.builder(20 * 5)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponGenericReload)
            .setTryReloadBehavior(ReloadBehavior.LOAD_INTO_WEAPON)
            .build(),
        ShootStrategy.builder().build()
    );

    public M56SmartgunItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
