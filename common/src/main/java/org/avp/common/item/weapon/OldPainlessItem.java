package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.avp.api.item.weapon.ammo.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.shoot.ShootStrategy;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

public class OldPainlessItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Automatic",
        1,
        0,
        AVPSoundEvents.ITEM_WEAPON_OLD_PAINLESS_SHOOT_LOOP,
        10,
        64.0D,
        0.0F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(
                Integer.MAX_VALUE,
                List.of(
                    AVPBulletItems.BULLET_HEAVY::get,
                    AVPBulletItems.BULLET_HEAVY_ACID::get,
                    AVPBulletItems.BULLET_HEAVY_ELECTRIC::get,
                    AVPBulletItems.BULLET_HEAVY_EXPLOSIVE::get,
                    AVPBulletItems.BULLET_HEAVY_INCENDIARY::get,
                    AVPBulletItems.BULLET_HEAVY_PENETRATION::get
                )
            )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.INVENTORY)
            .build(),
        List.of(FIRE_MODE),
        2F * 2,
        0.02F,
        ReloadStrategy.builder(0).build(),
        ShootStrategy.builder()
            .setBackgroundShootSound(AVPSoundEvents.ITEM_WEAPON_OLD_PAINLESS_SHOOT_SPINNING, 30)
            .setWindUpSound(AVPSoundEvents.ITEM_WEAPON_OLD_PAINLESS_SHOOT_START)
            .setWindDownSound(AVPSoundEvents.ITEM_WEAPON_OLD_PAINLESS_SHOOT_STOP)
            .setWindUpTimeInTicks(20)
            .build()
    );

    public OldPainlessItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
