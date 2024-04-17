package org.avp.common.item;

import net.minecraft.world.item.Item;
import org.avp.api.Holder;
import org.avp.api.item.weapon.bullet.effect.BulletEffect;
import org.avp.api.item.weapon.bullet.effect.BulletEffectRegistry;
import org.avp.api.item.weapon.bullet.effect.BulletEffects;
import org.avp.common.AVPResources;
import org.avp.common.registry.AVPDeferredItemRegistry;

import java.util.Set;
import java.util.function.Supplier;

public class AVPBulletItems extends AVPDeferredItemRegistry {

    public static final AVPBulletItems INSTANCE = new AVPBulletItems();

    private static final String BULLET_ITEM_PREFIX = "bullet_";

    public final Holder<Item> bulletCaseless;

    public final Holder<Item> bulletCaselessAcid;

    public final Holder<Item> bulletCaselessElectric;

    public final Holder<Item> bulletCaselessExplosive;

    public final Holder<Item> bulletCaselessIncendiary;

    public final Holder<Item> bulletCaselessPenetration;

    public final Holder<Item> bulletHeavy;

    public final Holder<Item> bulletHeavyAcid;

    public final Holder<Item> bulletHeavyElectric;

    public final Holder<Item> bulletHeavyExplosive;

    public final Holder<Item> bulletHeavyIncendiary;

    public final Holder<Item> bulletHeavyPenetration;

    public final Holder<Item> bulletPistol;

    public final Holder<Item> bulletPistolAcid;

    public final Holder<Item> bulletPistolElectric;

    public final Holder<Item> bulletPistolExplosive;

    public final Holder<Item> bulletPistolIncendiary;

    public final Holder<Item> bulletPistolPenetration;

    public final Holder<Item> bulletRifle;

    public final Holder<Item> bulletRifleAcid;

    public final Holder<Item> bulletRifleElectric;

    public final Holder<Item> bulletRifleExplosive;

    public final Holder<Item> bulletRifleIncendiary;

    public final Holder<Item> bulletRiflePenetration;

    public final Holder<Item> bulletShotgun;

    public final Holder<Item> bulletShotgunAcid;

    public final Holder<Item> bulletShotgunElectric;

    public final Holder<Item> bulletShotgunExplosive;

    public final Holder<Item> bulletShotgunIncendiary;

    public final Holder<Item> bulletShotgunPenetration;

    private Holder<Item> createHolder(String registryName, BulletEffect bulletEffect) {
        return createHolder(registryName, Set.of(bulletEffect));
    }

    private Holder<Item> createHolder(String registryName, Set<BulletEffect> bulletEffects) {
        return createHolder(registryName, () -> {
            var item = new Item(new Item.Properties());
            var location = AVPResources.location(BULLET_ITEM_PREFIX + registryName);
            bulletEffects.forEach(bulletEffect -> BulletEffectRegistry.registerBulletEffect(location, bulletEffect));
            return item;
        });
    }

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return super.createHolder(BULLET_ITEM_PREFIX + registryName, supplier);
    }

    private AVPBulletItems() {
        bulletCaseless = createHolder("caseless", BulletEffects.STANDARD);
        bulletCaselessAcid = createHolder("caseless_acid", BulletEffects.ACID);
        bulletCaselessElectric = createHolder("caseless_electric", BulletEffects.ELECTRIC);
        bulletCaselessExplosive = createHolder("caseless_explosive", BulletEffects.EXPLOSIVE);
        bulletCaselessIncendiary = createHolder("caseless_incendiary", BulletEffects.INCENDIARY);
        bulletCaselessPenetration = createHolder("caseless_penetration", BulletEffects.ARMOR_PENETRATION);

        bulletHeavy = createHolder("heavy", BulletEffects.STANDARD);
        bulletHeavyAcid = createHolder("heavy_acid", BulletEffects.ACID);
        bulletHeavyElectric = createHolder("heavy_electric", BulletEffects.ELECTRIC);
        bulletHeavyExplosive = createHolder("heavy_explosive", BulletEffects.EXPLOSIVE);
        bulletHeavyIncendiary = createHolder("heavy_incendiary", BulletEffects.INCENDIARY);
        bulletHeavyPenetration = createHolder("heavy_penetration", BulletEffects.ARMOR_PENETRATION);

        bulletPistol = createHolder("pistol", BulletEffects.STANDARD);
        bulletPistolAcid = createHolder("pistol_acid", BulletEffects.ACID);
        bulletPistolElectric = createHolder("pistol_electric", BulletEffects.ELECTRIC);
        bulletPistolExplosive = createHolder("pistol_explosive", BulletEffects.EXPLOSIVE);
        bulletPistolIncendiary = createHolder("pistol_incendiary", BulletEffects.INCENDIARY);
        bulletPistolPenetration = createHolder("pistol_penetration", BulletEffects.ARMOR_PENETRATION);

        bulletRifle = createHolder("rifle", BulletEffects.STANDARD);
        bulletRifleAcid = createHolder("rifle_acid", BulletEffects.ACID);
        bulletRifleElectric = createHolder("rifle_electric", BulletEffects.ELECTRIC);
        bulletRifleExplosive = createHolder("rifle_explosive", BulletEffects.EXPLOSIVE);
        bulletRifleIncendiary = createHolder("rifle_incendiary", BulletEffects.INCENDIARY);
        bulletRiflePenetration = createHolder("rifle_penetration", BulletEffects.ARMOR_PENETRATION);

        bulletShotgun = createHolder("shotgun", BulletEffects.STANDARD);
        bulletShotgunAcid = createHolder("shotgun_acid", BulletEffects.ACID);
        bulletShotgunElectric = createHolder("shotgun_electric", BulletEffects.ELECTRIC);
        bulletShotgunExplosive = createHolder("shotgun_explosive", BulletEffects.EXPLOSIVE);
        bulletShotgunIncendiary = createHolder("shotgun_incendiary", BulletEffects.INCENDIARY);
        bulletShotgunPenetration = createHolder("shotgun_penetration", BulletEffects.ARMOR_PENETRATION);
    }
}
