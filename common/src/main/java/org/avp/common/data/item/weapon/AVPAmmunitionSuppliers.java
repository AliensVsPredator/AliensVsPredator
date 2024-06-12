package org.avp.common.data.item.weapon;

import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Supplier;

import org.avp.common.registry.item.AVPAmmunitionPartItemRegistry;
import org.avp.common.registry.item.AVPBulletItemRegistry;

public class AVPAmmunitionSuppliers {

    public static final List<Supplier<Item>> CASELESS_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.penetration()::get
    );

    public static final List<Supplier<Item>> HEAVY_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.penetration()::get
    );

    public static final List<Supplier<Item>> PISTOL_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletPistol.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.penetration()::get
    );

    public static final List<Supplier<Item>> RIFLE_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletRifle.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.penetration()::get
    );

    public static final List<Supplier<Item>> ROCKET_AMMUNITION_SUPPLIERS = List.of(
        AVPAmmunitionPartItemRegistry.INSTANCE.rocket::get,
        AVPAmmunitionPartItemRegistry.INSTANCE.rocketElectric::get,
        AVPAmmunitionPartItemRegistry.INSTANCE.rocketIncendiary::get,
        AVPAmmunitionPartItemRegistry.INSTANCE.rocketPenetration::get
    );

    public static final List<Supplier<Item>> SHOTGUN_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.penetration()::get
    );

    private AVPAmmunitionSuppliers() {
        throw new UnsupportedOperationException();
    }
}
