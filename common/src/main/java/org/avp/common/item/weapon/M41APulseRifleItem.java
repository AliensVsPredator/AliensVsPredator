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

public class M41APulseRifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE_BURST = new FireMode(
        "Burst",
        4,
        10,
        AVPSoundEvents.ITEM_WEAPON_PULSE_RIFLE_SHOOT,
        1F
    );

    private static final FireMode FIRE_MODE_AUTOMATIC = new FireMode(
        "Automatic",
        1,
        1,
        AVPSoundEvents.ITEM_WEAPON_PULSE_RIFLE_SHOOT, // TODO:
        0.25F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(99)
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        WeaponDamageTypes.MEDIUM,
        List.of(FIRE_MODE_BURST, FIRE_MODE_AUTOMATIC),
        4F,
        0.13F,
        20 * 3,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_SHOOT_FAIL,
        null,
        AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD,
        ReloadStrategies.STANDARD,
        0
    );

    public M41APulseRifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
