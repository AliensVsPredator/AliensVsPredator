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
        64.0D,
        0.0F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(
            Integer.MAX_VALUE,
            List.of(
                AVPBulletItems.INSTANCE.bulletHeavy::get,
                AVPBulletItems.INSTANCE.bulletHeavyAcid::get,
                AVPBulletItems.INSTANCE.bulletHeavyElectric::get,
                AVPBulletItems.INSTANCE.bulletHeavyExplosive::get,
                AVPBulletItems.INSTANCE.bulletHeavyIncendiary::get,
                AVPBulletItems.INSTANCE.bulletHeavyPenetration::get
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
