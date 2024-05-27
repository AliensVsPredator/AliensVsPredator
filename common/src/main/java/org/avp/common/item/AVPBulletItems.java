package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.Set;

import org.avp.api.Holder;
import org.avp.api.item.weapon.bullet.effect.BulletEffect;
import org.avp.api.item.weapon.bullet.effect.BulletEffectRegistry;
import org.avp.api.item.weapon.bullet.effect.BulletEffects;
import org.avp.common.AVPResources;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPBulletItems extends AVPDeferredItemRegistry {

    public static final AVPBulletItems INSTANCE = new AVPBulletItems();

    private static final String BULLET_ITEM_PREFIX = "bullet_";

    public final ItemHolderBulletSet bulletCaseless;

    public final ItemHolderBulletSet bulletHeavy;

    public final ItemHolderBulletSet bulletPistol;

    public final ItemHolderBulletSet bulletRifle;

    public final ItemHolderBulletSet bulletShotgun;

    @Override
    protected Holder<Item> createHolder(String registryName) {
        return super.createHolder(BULLET_ITEM_PREFIX + registryName);
    }

    private Holder<Item> createHolderWithBulletEffects(String registryName, Set<BulletEffect> bulletEffects) {
        var location = AVPResources.location(BULLET_ITEM_PREFIX + registryName);
        bulletEffects.forEach(bulletEffect -> BulletEffectRegistry.registerBulletEffect(location, bulletEffect));
        return createHolder(registryName);
    }

    private Holder<Item> createHolderWithBulletEffect(String registryName, BulletEffect bulletEffect) {
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

    private AVPBulletItems() {
        bulletCaseless = createBulletHolderSet("caseless");
        bulletHeavy = createBulletHolderSet("heavy");
        bulletPistol = createBulletHolderSet("pistol");
        bulletRifle = createBulletHolderSet("rifle");
        bulletShotgun = createBulletHolderSet("shotgun");
    }

    public record ItemHolderBulletSet(
        Holder<Item> base,
        Holder<Item> acid,
        Holder<Item> electric,
        Holder<Item> explosive,
        Holder<Item> incendiary,
        Holder<Item> penetration
    ) {}
}
