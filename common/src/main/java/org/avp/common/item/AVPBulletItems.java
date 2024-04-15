package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.api.item.weapon.bullet.effect.BulletEffect;
import org.avp.api.item.weapon.bullet.effect.BulletEffectRegistry;
import org.avp.api.item.weapon.bullet.effect.BulletEffects;
import org.avp.common.AVPResources;
import org.avp.common.service.Services;

public class AVPBulletItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    private static final String BULLET_ITEM_PREFIX = "bullet_";

    public static final GameObject<Item> BULLET_CASELESS;

    public static final GameObject<Item> BULLET_CASELESS_ACID;

    public static final GameObject<Item> BULLET_CASELESS_ELECTRIC;

    public static final GameObject<Item> BULLET_CASELESS_EXPLOSIVE;

    public static final GameObject<Item> BULLET_CASELESS_INCENDIARY;

    public static final GameObject<Item> BULLET_CASELESS_PENETRATION;

    public static final GameObject<Item> BULLET_HEAVY;

    public static final GameObject<Item> BULLET_HEAVY_ACID;

    public static final GameObject<Item> BULLET_HEAVY_ELECTRIC;

    public static final GameObject<Item> BULLET_HEAVY_EXPLOSIVE;

    public static final GameObject<Item> BULLET_HEAVY_INCENDIARY;

    public static final GameObject<Item> BULLET_HEAVY_PENETRATION;

    public static final GameObject<Item> BULLET_PISTOL;

    public static final GameObject<Item> BULLET_PISTOL_ACID;

    public static final GameObject<Item> BULLET_PISTOL_ELECTRIC;

    public static final GameObject<Item> BULLET_PISTOL_EXPLOSIVE;

    public static final GameObject<Item> BULLET_PISTOL_INCENDIARY;

    public static final GameObject<Item> BULLET_PISTOL_PENETRATION;

    public static final GameObject<Item> BULLET_RIFLE;

    public static final GameObject<Item> BULLET_RIFLE_ACID;

    public static final GameObject<Item> BULLET_RIFLE_ELECTRIC;

    public static final GameObject<Item> BULLET_RIFLE_EXPLOSIVE;

    public static final GameObject<Item> BULLET_RIFLE_INCENDIARY;

    public static final GameObject<Item> BULLET_RIFLE_PENETRATION;

    public static final GameObject<Item> BULLET_SHOTGUN;

    public static final GameObject<Item> BULLET_SHOTGUN_ACID;

    public static final GameObject<Item> BULLET_SHOTGUN_ELECTRIC;

    public static final GameObject<Item> BULLET_SHOTGUN_EXPLOSIVE;

    public static final GameObject<Item> BULLET_SHOTGUN_INCENDIARY;

    public static final GameObject<Item> BULLET_SHOTGUN_PENETRATION;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName, BulletEffect bulletEffect) {
        return register(registryName, Set.of(bulletEffect));
    }

    private static GameObject<Item> register(String registryName, Set<BulletEffect> bulletEffects) {
        return register(registryName, () -> {
            var item = new Item(new Item.Properties());
            var location = AVPResources.location(BULLET_ITEM_PREFIX + registryName);
            bulletEffects.forEach(bulletEffect -> BulletEffectRegistry.registerBulletEffect(location, bulletEffect));
            return item;
        });
    }

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register(BULLET_ITEM_PREFIX + registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPBulletItems() {}

    static {
        BULLET_CASELESS = register("caseless", BulletEffects.STANDARD);
        BULLET_CASELESS_ACID = register("caseless_acid", BulletEffects.ACID);
        BULLET_CASELESS_ELECTRIC = register("caseless_electric", BulletEffects.ELECTRIC);
        BULLET_CASELESS_EXPLOSIVE = register("caseless_explosive", BulletEffects.EXPLOSIVE);
        BULLET_CASELESS_INCENDIARY = register("caseless_incendiary", BulletEffects.INCENDIARY);
        BULLET_CASELESS_PENETRATION = register("caseless_penetration", BulletEffects.ARMOR_PENETRATION);

        BULLET_HEAVY = register("heavy", BulletEffects.STANDARD);
        BULLET_HEAVY_ACID = register("heavy_acid", BulletEffects.ACID);
        BULLET_HEAVY_ELECTRIC = register("heavy_electric", BulletEffects.ELECTRIC);
        BULLET_HEAVY_EXPLOSIVE = register("heavy_explosive", BulletEffects.EXPLOSIVE);
        BULLET_HEAVY_INCENDIARY = register("heavy_incendiary", BulletEffects.INCENDIARY);
        BULLET_HEAVY_PENETRATION = register("heavy_penetration", BulletEffects.ARMOR_PENETRATION);

        BULLET_PISTOL = register("pistol", BulletEffects.STANDARD);
        BULLET_PISTOL_ACID = register("pistol_acid", BulletEffects.ACID);
        BULLET_PISTOL_ELECTRIC = register("pistol_electric", BulletEffects.ELECTRIC);
        BULLET_PISTOL_EXPLOSIVE = register("pistol_explosive", BulletEffects.EXPLOSIVE);
        BULLET_PISTOL_INCENDIARY = register("pistol_incendiary", BulletEffects.INCENDIARY);
        BULLET_PISTOL_PENETRATION = register("pistol_penetration", BulletEffects.ARMOR_PENETRATION);

        BULLET_RIFLE = register("rifle", BulletEffects.STANDARD);
        BULLET_RIFLE_ACID = register("rifle_acid", BulletEffects.ACID);
        BULLET_RIFLE_ELECTRIC = register("rifle_electric", BulletEffects.ELECTRIC);
        BULLET_RIFLE_EXPLOSIVE = register("rifle_explosive", BulletEffects.EXPLOSIVE);
        BULLET_RIFLE_INCENDIARY = register("rifle_incendiary", BulletEffects.INCENDIARY);
        BULLET_RIFLE_PENETRATION = register("rifle_penetration", BulletEffects.ARMOR_PENETRATION);

        BULLET_SHOTGUN = register("shotgun", BulletEffects.STANDARD);
        BULLET_SHOTGUN_ACID = register("shotgun_acid", BulletEffects.ACID);
        BULLET_SHOTGUN_ELECTRIC = register("shotgun_electric", BulletEffects.ELECTRIC);
        BULLET_SHOTGUN_EXPLOSIVE = register("shotgun_explosive", BulletEffects.EXPLOSIVE);
        BULLET_SHOTGUN_INCENDIARY = register("shotgun_incendiary", BulletEffects.INCENDIARY);
        BULLET_SHOTGUN_PENETRATION = register("shotgun_penetration", BulletEffects.ARMOR_PENETRATION);
    }
}
