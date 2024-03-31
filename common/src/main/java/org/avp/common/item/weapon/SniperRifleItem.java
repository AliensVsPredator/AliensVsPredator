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

public class SniperRifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "SemiAutomatic",
        1,
        30,
        AVPSoundEvents.ITEM_WEAPON_SNIPER_RIFLE_SHOOT,
        1.5F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(6)
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        WeaponDamageTypes.HEAVY,
        List.of(FIRE_MODE),
        15F * 2,
        0.13F,
        20 * 7 + 10,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        null,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD,
        ReloadStrategies.STANDARD,
        0
    );

    public SniperRifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }
}
