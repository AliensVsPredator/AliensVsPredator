package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.avp.api.item.weapon.ammo.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.shoot.ShootStrategy;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

public class FlamethrowerSevastopolItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Automatic",
        1,
        1,
        AVPSoundEvents.ITEM_WEAPON_FLAMETHROWER_SHOOT,
        16.0D,
        0.0F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(500, List.of(AVPAmmunitionPartItems.AMMO_FLAMETHROWER::get))
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE),
        1F,
        0.13F,
        ReloadStrategy.builder(20 * 5)
            .setReloadFinishSound(AVPSoundEvents.ITEM_WEAPON_FLAMETHROWER_RELOAD_FINISH)
            .setReloadStartSound(AVPSoundEvents.ITEM_WEAPON_FLAMETHROWER_RELOAD_START)
            .build(),
        ShootStrategy.builder().build()
    );

    public FlamethrowerSevastopolItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }
}
