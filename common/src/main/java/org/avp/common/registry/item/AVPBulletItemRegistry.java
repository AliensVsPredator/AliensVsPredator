package org.avp.common.registry.item;

import net.minecraft.world.item.Item;

import java.util.Set;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.weapon.bullet_effect.BulletEffect;
import org.avp.api.weapon.bullet_effect.BulletEffectRegistry;
import org.avp.api.weapon.bullet_effect.BulletEffects;
import org.avp.common.AVPResources;
import org.avp.api.registry.AVPDeferredItemRegistry;

public class AVPBulletItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPBulletItemRegistry INSTANCE = new AVPBulletItemRegistry();

    private static final String BULLET_ITEM_PREFIX = "bullet_";

    public final ItemHolderBulletSet bulletCaseless;

    public final ItemHolderBulletSet bulletHeavy;

    public final ItemHolderBulletSet bulletPistol;

    public final ItemHolderBulletSet bulletRifle;

    public final ItemHolderBulletSet bulletShotgun;

    @Override
    protected BLHolder<Item> createHolder(String registryName) {
        return super.createHolder(BULLET_ITEM_PREFIX + registryName);
    }

    private BLHolder<Item> createHolderWithBulletEffects(String registryName, Set<BulletEffect> bulletEffects) {
        var location = AVPResources.location(BULLET_ITEM_PREFIX + registryName);
        bulletEffects.forEach(bulletEffect -> BulletEffectRegistry.registerBulletEffect(location, bulletEffect));
        return createHolder(registryName);
    }

    private BLHolder<Item> createHolderWithBulletEffect(String registryName, BulletEffect bulletEffect) {
        return createHolderWithBulletEffects(registryName, Set.of(bulletEffect));
    }

    private ItemHolderBulletSet createBulletHolderSet(String registryName) {
        return new ItemHolderBulletSet(
            createHolderWithBulletEffect(registryName, BulletEffects.STANDARD),
            createHolderWithBulletEffect(registryName + "_acid", BulletEffects.ACID),
            createHolderWithBulletEffect(registryName + "_electric", BulletEffects.ELECTRIC),
            createHolderWithBulletEffect(registryName + "_explosive", BulletEffects.EXPLOSIVE),
            createHolderWithBulletEffect(registryName + "_incendiary", BulletEffects.INCENDIARY),
            createHolderWithBulletEffect(registryName + "_penetration", BulletEffects.ARMOR_PENETRATION)
        );
    }

    private AVPBulletItemRegistry() {
        bulletCaseless = createBulletHolderSet("caseless");
        bulletHeavy = createBulletHolderSet("heavy");
        bulletPistol = createBulletHolderSet("pistol");
        bulletRifle = createBulletHolderSet("rifle");
        bulletShotgun = createBulletHolderSet("shotgun");
    }

    public record ItemHolderBulletSet(
        BLHolder<Item> base,
        BLHolder<Item> acid,
        BLHolder<Item> electric,
        BLHolder<Item> explosive,
        BLHolder<Item> incendiary,
        BLHolder<Item> penetration
    ) {}
}
