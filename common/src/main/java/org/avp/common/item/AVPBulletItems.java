package org.avp.common.item;

import net.minecraft.world.item.Item;
import org.avp.api.GameObject;
import org.avp.common.service.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Boston Vanseghi
 */
public class AVPBulletItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

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

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register("bullet_" + registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPBulletItems() {}

    static {
        BULLET_HEAVY = register("heavy", () -> new Item(new Item.Properties()));
        BULLET_HEAVY_ACID = register("heavy_acid", () -> new Item(new Item.Properties()));
        BULLET_HEAVY_ELECTRIC = register("heavy_electric", () -> new Item(new Item.Properties()));
        BULLET_HEAVY_EXPLOSIVE = register("heavy_explosive", () -> new Item(new Item.Properties()));
        BULLET_HEAVY_INCENDIARY = register("heavy_incendiary", () -> new Item(new Item.Properties()));
        BULLET_HEAVY_PENETRATION = register("heavy_penetration", () -> new Item(new Item.Properties()));

        BULLET_PISTOL = register("pistol", () -> new Item(new Item.Properties()));
        BULLET_PISTOL_ACID = register("pistol_acid", () -> new Item(new Item.Properties()));
        BULLET_PISTOL_ELECTRIC = register("pistol_electric", () -> new Item(new Item.Properties()));
        BULLET_PISTOL_EXPLOSIVE = register("pistol_explosive", () -> new Item(new Item.Properties()));
        BULLET_PISTOL_INCENDIARY = register("pistol_incendiary", () -> new Item(new Item.Properties()));
        BULLET_PISTOL_PENETRATION = register("pistol_penetration", () -> new Item(new Item.Properties()));

        BULLET_RIFLE = register("rifle", () -> new Item(new Item.Properties()));
        BULLET_RIFLE_ACID = register("rifle_acid", () -> new Item(new Item.Properties()));
        BULLET_RIFLE_ELECTRIC = register("rifle_electric", () -> new Item(new Item.Properties()));
        BULLET_RIFLE_EXPLOSIVE = register("rifle_explosive", () -> new Item(new Item.Properties()));
        BULLET_RIFLE_INCENDIARY = register("rifle_incendiary", () -> new Item(new Item.Properties()));
        BULLET_RIFLE_PENETRATION = register("rifle_penetration", () -> new Item(new Item.Properties()));

        BULLET_SHOTGUN = register("shotgun", () -> new Item(new Item.Properties()));
        BULLET_SHOTGUN_ACID = register("shotgun_acid", () -> new Item(new Item.Properties()));
        BULLET_SHOTGUN_ELECTRIC = register("shotgun_electric", () -> new Item(new Item.Properties()));
        BULLET_SHOTGUN_EXPLOSIVE = register("shotgun_explosive", () -> new Item(new Item.Properties()));
        BULLET_SHOTGUN_INCENDIARY = register("shotgun_incendiary", () -> new Item(new Item.Properties()));
        BULLET_SHOTGUN_PENETRATION = register("shotgun_penetration", () -> new Item(new Item.Properties()));
    }
}
