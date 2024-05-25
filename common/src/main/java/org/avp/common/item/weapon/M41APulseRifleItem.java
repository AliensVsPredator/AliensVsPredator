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

public class M41APulseRifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE_BURST = new FireMode(
        "Burst",
        4,
        10,
        AVPSoundEvents.INSTANCE.itemWeaponPulseRifleShoot,
        64,
        1
    );

    private static final FireMode FIRE_MODE_AUTOMATIC = new FireMode(
        "Automatic",
        1,
        2,
        AVPSoundEvents.INSTANCE.itemWeaponPulseRifleShoot, // TODO:
        64,
        0.25F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1,
        AmmunitionStrategy.builder(
            99,
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
        List.of(FIRE_MODE_BURST, FIRE_MODE_AUTOMATIC),
        4F,
        0.13F,
        ReloadStrategy.builder(20 * 3)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponGenericReload)
            .setTryReloadBehavior(ReloadBehavior.LOAD_INTO_WEAPON)
            .build(),
        ShootStrategy.builder().build()
    );

    public M41APulseRifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
