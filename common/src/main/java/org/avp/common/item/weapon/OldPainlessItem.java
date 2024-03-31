package org.avp.common.item.weapon;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponDamageTypes;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.avp.api.item.weapon.ammo.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

import java.util.List;

public class OldPainlessItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Automatic",
        1,
        0,
        AVPSoundEvents.ITEM_WEAPON_AK_47_SHOOT,
        0.0F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(Integer.MAX_VALUE)
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.NO_OP)
            .build(),
        WeaponDamageTypes.HEAVY,
        List.of(FIRE_MODE),
        2F * 2,
        0.02F,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        ReloadStrategy.builder(0).build(),
        20
    );

    public OldPainlessItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
