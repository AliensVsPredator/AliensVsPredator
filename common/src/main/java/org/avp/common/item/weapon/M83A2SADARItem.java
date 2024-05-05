package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammunition.AmmunitionStrategy;
import org.avp.api.item.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.reload.ReloadBehavior;
import org.avp.api.item.weapon.shoot.ShootStrategy;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

public class M83A2SADARItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Semi-Automatic",
        1,
        60,
        AVPSoundEvents.INSTANCE.itemWeaponRocketLauncherShoot,
        100.0D,
        1.5F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(
            4,
            List.of(
                AVPAmmunitionPartItems.INSTANCE.rocket::get,
                AVPAmmunitionPartItems.INSTANCE.rocketElectric::get,
                AVPAmmunitionPartItems.INSTANCE.rocketIncendiary::get,
                AVPAmmunitionPartItems.INSTANCE.rocketPenetration::get
            )
        )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE),
        8F * 2,
        0.5F,
        ReloadStrategy.builder(20 * 5)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponRocketLauncherReloadStart)
            .setReloadFinishSound(AVPSoundEvents.INSTANCE.itemWeaponRocketLauncherReloadFinish)
            .setTryReloadBehavior(ReloadBehavior.LOAD_INTO_WEAPON)
            .build(),
        ShootStrategy.builder().build()
    );

    public M83A2SADARItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
