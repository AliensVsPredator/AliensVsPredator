package org.avp.common.item.weapon;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponDamageTypes;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.avp.api.item.weapon.ammo.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategies;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

import java.util.List;

public class F90RifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Automatic",
        1,
        2,
        AVPSoundEvents.ITEM_WEAPON_AK_47_SHOOT,
        0.0F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(32)
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        WeaponDamageTypes.MEDIUM,
        List.of(FIRE_MODE),
        2F * 2,
        0.13F,
        20 * 5,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        null,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD,
        ReloadStrategies.STANDARD,
        0
    );

    public F90RifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
