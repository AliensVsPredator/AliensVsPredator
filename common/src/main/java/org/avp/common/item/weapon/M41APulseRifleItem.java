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

public class M41APulseRifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE_BURST = new FireMode(
        "Burst",
        4,
        10,
        AVPSoundEvents.ITEM_WEAPON_PULSE_RIFLE_SHOOT,
        64.0D,
        1F
    );

    private static final FireMode FIRE_MODE_AUTOMATIC = new FireMode(
        "Automatic",
        1,
        2,
        AVPSoundEvents.ITEM_WEAPON_PULSE_RIFLE_SHOOT, // TODO:
        64.0D,
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
        ReloadStrategy.builder(20 * 3)
            .setReloadStartSound(AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD)
            .setTryReloadBehavior(TryReloadBehavior.STANDARD)
            .build(),
        ShootStrategy.builder().build()
    );

    public M41APulseRifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
