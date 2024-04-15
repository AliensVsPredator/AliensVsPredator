package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.avp.api.item.weapon.ammo.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.reload.TryReloadBehavior;
import org.avp.api.item.weapon.shoot.ShootStrategy;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

public class SniperRifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Semi-Automatic",
        1,
        30,
        AVPSoundEvents.ITEM_WEAPON_SNIPER_RIFLE_SHOOT,
        128.0D,
        1.5F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(6)
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE),
        15F * 2,
        0.13F,
        ReloadStrategy.builder(20 * 7 + 10)
            .setReloadStartSound(AVPSoundEvents.ITEM_WEAPON_GENERIC_RELOAD)
            .setTryReloadBehavior(TryReloadBehavior.STANDARD)
            .build(),
        ShootStrategy.builder().build()
    );

    public SniperRifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }
}
