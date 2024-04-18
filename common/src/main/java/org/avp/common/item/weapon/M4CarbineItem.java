package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.avp.api.item.weapon.ammo.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.reload.TryReloadBehavior;
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
        64.0D,
        0.0F
    );

    // TODO:
    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(
            32,
            List.of(
                AVPBulletItems.INSTANCE.bulletRifle::get,
                AVPBulletItems.INSTANCE.bulletRifleAcid::get,
                AVPBulletItems.INSTANCE.bulletRifleElectric::get,
                AVPBulletItems.INSTANCE.bulletRifleExplosive::get,
                AVPBulletItems.INSTANCE.bulletRifleIncendiary::get,
                AVPBulletItems.INSTANCE.bulletRiflePenetration::get
            )
        )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE),
        2F * 2,
        0.13F,
        ReloadStrategy.builder(20 * 5)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponGenericReload)
            .setTryReloadBehavior(TryReloadBehavior.STANDARD)
            .build(),
        ShootStrategy.builder().build()
    );

    public M4CarbineItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
