package org.avp.common.item;

import net.minecraft.world.item.Item;
import org.avp.api.GameObject;
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

    public final GameObject<Item> BULLET_CASELESS;

    public final GameObject<Item> BULLET_CASELESS_ACID;

    public final GameObject<Item> BULLET_CASELESS_ELECTRIC;

    public final GameObject<Item> BULLET_CASELESS_EXPLOSIVE;

    public final GameObject<Item> BULLET_CASELESS_INCENDIARY;

    public final GameObject<Item> BULLET_CASELESS_PENETRATION;

    public final GameObject<Item> BULLET_HEAVY;

    public final GameObject<Item> BULLET_HEAVY_ACID;

    public final GameObject<Item> BULLET_HEAVY_ELECTRIC;

    public final GameObject<Item> BULLET_HEAVY_EXPLOSIVE;

    public final GameObject<Item> BULLET_HEAVY_INCENDIARY;

    public final GameObject<Item> BULLET_HEAVY_PENETRATION;

    public final GameObject<Item> BULLET_PISTOL;

    public final GameObject<Item> BULLET_PISTOL_ACID;

    public final GameObject<Item> BULLET_PISTOL_ELECTRIC;

    public final GameObject<Item> BULLET_PISTOL_EXPLOSIVE;

    public final GameObject<Item> BULLET_PISTOL_INCENDIARY;

    public final GameObject<Item> BULLET_PISTOL_PENETRATION;

    public final GameObject<Item> BULLET_RIFLE;

    public final GameObject<Item> BULLET_RIFLE_ACID;

    public final GameObject<Item> BULLET_RIFLE_ELECTRIC;

    public final GameObject<Item> BULLET_RIFLE_EXPLOSIVE;

    public final GameObject<Item> BULLET_RIFLE_INCENDIARY;

    public final GameObject<Item> BULLET_RIFLE_PENETRATION;

    public final GameObject<Item> BULLET_SHOTGUN;

    public final GameObject<Item> BULLET_SHOTGUN_ACID;

    public final GameObject<Item> BULLET_SHOTGUN_ELECTRIC;

    public final GameObject<Item> BULLET_SHOTGUN_EXPLOSIVE;

    public final GameObject<Item> BULLET_SHOTGUN_INCENDIARY;

    public final GameObject<Item> BULLET_SHOTGUN_PENETRATION;

    private GameObject<Item> createHolder(String registryName, BulletEffect bulletEffect) {
        return createHolder(registryName, Set.of(bulletEffect));
    }

    private GameObject<Item> createHolder(String registryName, Set<BulletEffect> bulletEffects) {
        return createHolder(registryName, () -> {
            var item = new Item(new Item.Properties());
            var location = AVPResources.location(BULLET_ITEM_PREFIX + registryName);
            bulletEffects.forEach(bulletEffect -> BulletEffectRegistry.registerBulletEffect(location, bulletEffect));
            return item;
        });
    }

    @Override
    protected GameObject<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return super.createHolder(BULLET_ITEM_PREFIX + registryName, supplier);
    }

    private AVPBulletItems() {
        BULLET_CASELESS = createHolder("caseless", BulletEffects.STANDARD);
        BULLET_CASELESS_ACID = createHolder("caseless_acid", BulletEffects.ACID);
        BULLET_CASELESS_ELECTRIC = createHolder("caseless_electric", BulletEffects.ELECTRIC);
        BULLET_CASELESS_EXPLOSIVE = createHolder("caseless_explosive", BulletEffects.EXPLOSIVE);
        BULLET_CASELESS_INCENDIARY = createHolder("caseless_incendiary", BulletEffects.INCENDIARY);
        BULLET_CASELESS_PENETRATION = createHolder("caseless_penetration", BulletEffects.ARMOR_PENETRATION);

        BULLET_HEAVY = createHolder("heavy", BulletEffects.STANDARD);
        BULLET_HEAVY_ACID = createHolder("heavy_acid", BulletEffects.ACID);
        BULLET_HEAVY_ELECTRIC = createHolder("heavy_electric", BulletEffects.ELECTRIC);
        BULLET_HEAVY_EXPLOSIVE = createHolder("heavy_explosive", BulletEffects.EXPLOSIVE);
        BULLET_HEAVY_INCENDIARY = createHolder("heavy_incendiary", BulletEffects.INCENDIARY);
        BULLET_HEAVY_PENETRATION = createHolder("heavy_penetration", BulletEffects.ARMOR_PENETRATION);

        BULLET_PISTOL = createHolder("pistol", BulletEffects.STANDARD);
        BULLET_PISTOL_ACID = createHolder("pistol_acid", BulletEffects.ACID);
        BULLET_PISTOL_ELECTRIC = createHolder("pistol_electric", BulletEffects.ELECTRIC);
        BULLET_PISTOL_EXPLOSIVE = createHolder("pistol_explosive", BulletEffects.EXPLOSIVE);
        BULLET_PISTOL_INCENDIARY = createHolder("pistol_incendiary", BulletEffects.INCENDIARY);
        BULLET_PISTOL_PENETRATION = createHolder("pistol_penetration", BulletEffects.ARMOR_PENETRATION);

        BULLET_RIFLE = createHolder("rifle", BulletEffects.STANDARD);
        BULLET_RIFLE_ACID = createHolder("rifle_acid", BulletEffects.ACID);
        BULLET_RIFLE_ELECTRIC = createHolder("rifle_electric", BulletEffects.ELECTRIC);
        BULLET_RIFLE_EXPLOSIVE = createHolder("rifle_explosive", BulletEffects.EXPLOSIVE);
        BULLET_RIFLE_INCENDIARY = createHolder("rifle_incendiary", BulletEffects.INCENDIARY);
        BULLET_RIFLE_PENETRATION = createHolder("rifle_penetration", BulletEffects.ARMOR_PENETRATION);

        BULLET_SHOTGUN = createHolder("shotgun", BulletEffects.STANDARD);
        BULLET_SHOTGUN_ACID = createHolder("shotgun_acid", BulletEffects.ACID);
        BULLET_SHOTGUN_ELECTRIC = createHolder("shotgun_electric", BulletEffects.ELECTRIC);
        BULLET_SHOTGUN_EXPLOSIVE = createHolder("shotgun_explosive", BulletEffects.EXPLOSIVE);
        BULLET_SHOTGUN_INCENDIARY = createHolder("shotgun_incendiary", BulletEffects.INCENDIARY);
        BULLET_SHOTGUN_PENETRATION = createHolder("shotgun_penetration", BulletEffects.ARMOR_PENETRATION);
    }
}
