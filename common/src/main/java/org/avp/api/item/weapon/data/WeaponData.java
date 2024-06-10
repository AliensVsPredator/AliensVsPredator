package org.avp.api.item.weapon.data;

import net.minecraft.world.item.Item;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPBulletItems;

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
        AVPBulletItems.INSTANCE.bulletCaseless.base()::get,
        AVPBulletItems.INSTANCE.bulletCaseless.acid()::get,
        AVPBulletItems.INSTANCE.bulletCaseless.electric()::get,
        AVPBulletItems.INSTANCE.bulletCaseless.explosive()::get,
        AVPBulletItems.INSTANCE.bulletCaseless.incendiary()::get,
        AVPBulletItems.INSTANCE.bulletCaseless.penetration()::get
    );

    protected static final List<Supplier<Item>> HEAVY_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItems.INSTANCE.bulletHeavy.base()::get,
        AVPBulletItems.INSTANCE.bulletHeavy.acid()::get,
        AVPBulletItems.INSTANCE.bulletHeavy.electric()::get,
        AVPBulletItems.INSTANCE.bulletHeavy.explosive()::get,
        AVPBulletItems.INSTANCE.bulletHeavy.incendiary()::get,
        AVPBulletItems.INSTANCE.bulletHeavy.penetration()::get
    );

    protected static final List<Supplier<Item>> PISTOL_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItems.INSTANCE.bulletPistol.base()::get,
        AVPBulletItems.INSTANCE.bulletPistol.acid()::get,
        AVPBulletItems.INSTANCE.bulletPistol.electric()::get,
        AVPBulletItems.INSTANCE.bulletPistol.explosive()::get,
        AVPBulletItems.INSTANCE.bulletPistol.incendiary()::get,
        AVPBulletItems.INSTANCE.bulletPistol.penetration()::get
    );

    protected static final List<Supplier<Item>> RIFLE_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItems.INSTANCE.bulletRifle.base()::get,
        AVPBulletItems.INSTANCE.bulletRifle.acid()::get,
        AVPBulletItems.INSTANCE.bulletRifle.electric()::get,
        AVPBulletItems.INSTANCE.bulletRifle.explosive()::get,
        AVPBulletItems.INSTANCE.bulletRifle.incendiary()::get,
        AVPBulletItems.INSTANCE.bulletRifle.penetration()::get
    );

    protected static final List<Supplier<Item>> ROCKET_AMMUNITION_SUPPLIERS = List.of(
        AVPAmmunitionPartItems.INSTANCE.rocket::get,
        AVPAmmunitionPartItems.INSTANCE.rocketElectric::get,
        AVPAmmunitionPartItems.INSTANCE.rocketIncendiary::get,
        AVPAmmunitionPartItems.INSTANCE.rocketPenetration::get
    );

    protected static final List<Supplier<Item>> SHOTGUN_AMMUNITION_SUPPLIERS = List.of(
        AVPBulletItems.INSTANCE.bulletShotgun.base()::get,
        AVPBulletItems.INSTANCE.bulletShotgun.acid()::get,
        AVPBulletItems.INSTANCE.bulletShotgun.electric()::get,
        AVPBulletItems.INSTANCE.bulletShotgun.explosive()::get,
        AVPBulletItems.INSTANCE.bulletShotgun.incendiary()::get,
        AVPBulletItems.INSTANCE.bulletShotgun.penetration()::get
    );
}
