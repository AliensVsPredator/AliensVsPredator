package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammunition.AmmunitionStrategy;
import org.avp.api.item.weapon.ammunition.HasAmmunitionBehavior;
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
        AVPSoundEvents.INSTANCE.itemWeaponOldPainlessShootLoop,
        10,
        64,
        0
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1,
        AmmunitionStrategy.builder(
            Integer.MAX_VALUE,
            List.of(
                AVPBulletItems.INSTANCE.bulletHeavy.base()::get,
                AVPBulletItems.INSTANCE.bulletHeavy.acid()::get,
                AVPBulletItems.INSTANCE.bulletHeavy.electric()::get,
                AVPBulletItems.INSTANCE.bulletHeavy.explosive()::get,
                AVPBulletItems.INSTANCE.bulletHeavy.incendiary()::get,
                AVPBulletItems.INSTANCE.bulletHeavy.penetration()::get
            )
        )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.INVENTORY)
            .build(),
        List.of(FIRE_MODE),
        2F * 2,
        0.02F,
        ReloadStrategy.builder(0).build(),
        ShootStrategy.builder()
            .setBackgroundShootSound(AVPSoundEvents.INSTANCE.itemWeaponOldPainlessShootSpinning, 30)
            .setWindingLogic(
                20,
                AVPSoundEvents.INSTANCE.itemWeaponOldPainlessShootStart,
                AVPSoundEvents.INSTANCE.itemWeaponOldPainlessShootStop
            )
            .build()
    );

    public OldPainlessItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
