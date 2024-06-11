package org.avp.api.item.weapon.data;

import net.minecraft.world.item.Item;
import org.avp.common.registry.item.AVPAmmunitionPartItemRegistry;
import org.avp.common.registry.item.AVPBulletItemRegistry;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class WeaponData {
    private final List<FireModeData> fireModeDataList = createFireModeData();

    protected abstract List<FireModeData> createFireModeData();

    public abstract int getDurability();

    public final List<FireModeData> getFireModeDataList() {
        return fireModeDataList;
    }

    public final FireModeData getFireModeByIdOrFirst(String identifier) {
        return this.getFireModeDataList()
            .stream()
            .filter(fireModeData -> Objects.equals(fireModeData.identifier(), identifier))
            .findFirst()
            .or(() -> this.getFireModeDataList().stream().findFirst())
            .orElseThrow();
    }

    protected static final List<Supplier<Item>> CASELESS_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletCaseless.penetration()::get
    );

    protected static final List<Supplier<Item>> HEAVY_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletHeavy.penetration()::get
    );

    protected static final List<Supplier<Item>> PISTOL_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletPistol.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletPistol.penetration()::get
    );

    protected static final List<Supplier<Item>> RIFLE_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletRifle.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletRifle.penetration()::get
    );

    protected static final List<Supplier<Item>> ROCKET_AMMUNITION_SUPPLIERS = List.of(
        AVPAmmunitionPartItemRegistry.INSTANCE.rocket::get,
        AVPAmmunitionPartItemRegistry.INSTANCE.rocketElectric::get,
        AVPAmmunitionPartItemRegistry.INSTANCE.rocketIncendiary::get,
        AVPAmmunitionPartItemRegistry.INSTANCE.rocketPenetration::get
    );

    protected static final List<Supplier<Item>> SHOTGUN_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.base()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.acid()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.electric()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.explosive()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.incendiary()::get,
        AVPBulletItemRegistry.INSTANCE.bulletShotgun.penetration()::get
    );
}
