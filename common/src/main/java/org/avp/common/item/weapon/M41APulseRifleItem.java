package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.avp.api.item.weapon.ammo.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.reload.TryReloadBehavior;
import org.avp.api.item.weapon.shoot.ShootStrategy;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

public class M41APulseRifleItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE_BURST = new FireMode(
        "Burst",
        4,
        10,
        AVPSoundEvents.INSTANCE.ITEM_WEAPON_PULSE_RIFLE_SHOOT,
        64.0D,
        1F
    );

    private static final FireMode FIRE_MODE_AUTOMATIC = new FireMode(
        "Automatic",
        1,
        2,
        AVPSoundEvents.INSTANCE.ITEM_WEAPON_PULSE_RIFLE_SHOOT, // TODO:
        64.0D,
        0.25F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(
                99,
                List.of(
                    AVPBulletItems.BULLET_CASELESS::get,
                    AVPBulletItems.BULLET_CASELESS_ACID::get,
                    AVPBulletItems.BULLET_CASELESS_ELECTRIC::get,
                    AVPBulletItems.BULLET_CASELESS_EXPLOSIVE::get,
                    AVPBulletItems.BULLET_CASELESS_INCENDIARY::get,
                    AVPBulletItems.BULLET_CASELESS_PENETRATION::get
                )
            )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE_BURST, FIRE_MODE_AUTOMATIC),
        4F,
        0.13F,
        ReloadStrategy.builder(20 * 3)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.ITEM_WEAPON_GENERIC_RELOAD)
            .setTryReloadBehavior(TryReloadBehavior.STANDARD)
            .build(),
        ShootStrategy.builder().build()
    );

    public M41APulseRifleItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
