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

public class M88Mod4CombatPistolItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Semi-Automatic",
        1,
        7,
        AVPSoundEvents.INSTANCE.itemWeaponCombatPistolShoot,
        32.0D,
        0.0F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1.0F,
        AmmunitionStrategy.builder(
            18,
            List.of(
                AVPBulletItems.INSTANCE.bulletPistol::get,
                AVPBulletItems.INSTANCE.bulletPistolAcid::get,
                AVPBulletItems.INSTANCE.bulletPistolElectric::get,
                AVPBulletItems.INSTANCE.bulletPistolExplosive::get,
                AVPBulletItems.INSTANCE.bulletPistolIncendiary::get,
                AVPBulletItems.INSTANCE.bulletPistolPenetration::get
            )
        )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE),
        2F,
        0.06F,
        ReloadStrategy.builder(20 * 2)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponCombatPistolReload)
            .setTryReloadBehavior(TryReloadBehavior.STANDARD)
            .build(),
        ShootStrategy.builder().build()
    );

    public M88Mod4CombatPistolItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }

}
