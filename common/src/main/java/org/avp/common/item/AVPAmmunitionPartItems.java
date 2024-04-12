package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

public class AVPAmmunitionPartItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> AMMO_CHARGE_PACK;

    public static final GameObject<Item> AMMO_FLAMETHROWER;

    public static final GameObject<Item> BULLET_TIP;

    public static final GameObject<Item> CASING_CASELESS;

    public static final GameObject<Item> CASING_HEAVY;

    public static final GameObject<Item> CASING_PISTOL;

    public static final GameObject<Item> CASING_RIFLE;

    public static final GameObject<Item> CASING_SHOTGUN;

    public static final GameObject<Item> ROCKET;

    public static final GameObject<Item> ROCKET_ELECTRIC;

    public static final GameObject<Item> ROCKET_INCENDIARY;

    public static final GameObject<Item> ROCKET_PENETRATION;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register(registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPAmmunitionPartItems() {}

    static {
        AMMO_CHARGE_PACK = register("ammo_charge_pack", () -> new Item(new Item.Properties()));
        AMMO_FLAMETHROWER = register("ammo_flamethrower", () -> new Item(new Item.Properties()));

        BULLET_TIP = register("bullet_tip", () -> new Item(new Item.Properties()));

        CASING_CASELESS = register("casing_caseless", () -> new Item(new Item.Properties()));
        CASING_HEAVY = register("casing_heavy", () -> new Item(new Item.Properties()));
        CASING_PISTOL = register("casing_pistol", () -> new Item(new Item.Properties()));
        CASING_RIFLE = register("casing_rifle", () -> new Item(new Item.Properties()));
        CASING_SHOTGUN = register("casing_shotgun", () -> new Item(new Item.Properties()));

        ROCKET = register("rocket", () -> new Item(new Item.Properties()));
        ROCKET_ELECTRIC = register("rocket_electric", () -> new Item(new Item.Properties()));
        ROCKET_INCENDIARY = register("rocket_incendiary", () -> new Item(new Item.Properties()));
        ROCKET_PENETRATION = register("rocket_penetration", () -> new Item(new Item.Properties()));
    }
}
