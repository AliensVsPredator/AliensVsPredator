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

public class F90RifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Automatic",
        1,
        2,
        AVPSoundEvents.INSTANCE.ITEM_WEAPON_AK_47_SHOOT,
        64.0D,
        0.0F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(
                32,
                List.of(
                    AVPBulletItems.INSTANCE.BULLET_RIFLE::get,
                    AVPBulletItems.INSTANCE.BULLET_RIFLE_ACID::get,
                    AVPBulletItems.INSTANCE.BULLET_RIFLE_ELECTRIC::get,
                    AVPBulletItems.INSTANCE.BULLET_RIFLE_EXPLOSIVE::get,
                    AVPBulletItems.INSTANCE.BULLET_RIFLE_INCENDIARY::get,
                    AVPBulletItems.INSTANCE.BULLET_RIFLE_PENETRATION::get
                )
            )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE),
        2F * 2,
        0.13F,
        ReloadStrategy.builder(20 * 5)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.ITEM_WEAPON_GENERIC_RELOAD)
            .setTryReloadBehavior(TryReloadBehavior.STANDARD)
            .build(),
        ShootStrategy.builder().build()
    );

    public F90RifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
