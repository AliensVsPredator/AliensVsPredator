package org.avp.common.item.weapon;

import java.util.List;

import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.ammunition.AmmunitionStrategy;
import org.avp.api.item.weapon.ammunition.HasAmmunitionBehavior;
import org.avp.api.item.weapon.reload.ReloadBehavior;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.shoot.ShootStrategy;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;

public class M3712ShotgunItem extends AbstractAVPWeaponItem {

    private static final FireMode FIRE_MODE = new FireMode(
        "Semi-Automatic",
        1,
        20,
        AVPSoundEvents.INSTANCE.itemWeaponShotgunShoot,
        6,
        1.5F
    );

    private static final WeaponItemData WEAPON_ITEM_DATA = new WeaponItemData(
        1,
        AmmunitionStrategy.builder(
            6,
            List.of(
                AVPBulletItems.INSTANCE.bulletShotgun::get,
                AVPBulletItems.INSTANCE.bulletShotgunAcid::get,
                AVPBulletItems.INSTANCE.bulletShotgunElectric::get,
                AVPBulletItems.INSTANCE.bulletShotgunExplosive::get,
                AVPBulletItems.INSTANCE.bulletShotgunIncendiary::get,
                AVPBulletItems.INSTANCE.bulletShotgunPenetration::get
            )
        )
            .setHasAmmunitionBehavior(HasAmmunitionBehavior.LOADED)
            .build(),
        List.of(FIRE_MODE),
        8F * 2,
        0.5F,
        ReloadStrategy.builder(20 * 4)
            .setReloadStartSound(AVPSoundEvents.INSTANCE.itemWeaponGenericReload)
            .setTryReloadBehavior(ReloadBehavior.LOAD_INTO_WEAPON)
            .build(),
        ShootStrategy.builder().build()
    );

    public M3712ShotgunItem(Properties properties) {
        super(properties, WEAPON_ITEM_DATA);
    }
}
