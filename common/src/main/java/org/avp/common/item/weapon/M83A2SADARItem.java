package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponDamageTypes;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.avp.api.item.weapon.ammo.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.reload.TryReloadBehavior;
import org.avp.api.item.weapon.shoot.ShootStrategy;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

public class M83A2SADARItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "SemiAutomatic",
        1,
        60,
        AVPSoundEvents.ITEM_WEAPON_ROCKET_LAUNCHER_SHOOT,
        100.0D,
        1.5F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(4)
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        WeaponDamageTypes.MEDIUM,
        List.of(FIRE_MODE),
        8F * 2,
        0.5F,
        ReloadStrategy.builder(20 * 5)
            .setReloadStartSound(AVPSoundEvents.ITEM_WEAPON_ROCKET_LAUNCHER_RELOAD_START)
            .setReloadFinishSound(AVPSoundEvents.ITEM_WEAPON_ROCKET_LAUNCHER_RELOAD_FINISH)
            .setTryReloadBehavior(TryReloadBehavior.STANDARD)
            .build(),
        ShootStrategy.builder().build()
    );

    public M83A2SADARItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
