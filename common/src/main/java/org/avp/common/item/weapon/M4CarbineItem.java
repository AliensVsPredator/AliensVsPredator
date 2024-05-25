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

public class M4CarbineItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Automatic",
        1,
        2,
        AVPSoundEvents.INSTANCE.itemWeaponAk47Shoot,
        64,
        0
    );

    // TODO:
    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1,
        AmmunitionStrategy.builder(
            32,
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
        2F * 2,
        0.13F,
        ReloadStrategy.builder(20 * 5)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponGenericReload)
            .setTryReloadBehavior(ReloadBehavior.LOAD_INTO_WEAPON)
            .build(),
        ShootStrategy.builder().build()
    );

    public M4CarbineItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
