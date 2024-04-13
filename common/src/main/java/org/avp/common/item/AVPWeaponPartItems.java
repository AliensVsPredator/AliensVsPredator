package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

public class AVPWeaponPartItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> WEAPON_PART_BARREL_GENERIC;

    public static final GameObject<Item> WEAPON_PART_BARREL_MINIGUN;

    public static final GameObject<Item> WEAPON_PART_BARREL_ROCKET;

    public static final GameObject<Item> WEAPON_PART_BARREL_SMART;

    public static final GameObject<Item> WEAPON_PART_GRIP_GENERIC;

    public static final GameObject<Item> WEAPON_PART_RECEIVER_GENERIC;

    public static final GameObject<Item> WEAPON_PART_RECEIVER_SMART;

    public static final GameObject<Item> WEAPON_PART_STOCK_GENERIC;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName) {
        return register(registryName, () -> new Item(new Item.Properties()));
    }

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register("weapon_part_" + registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPWeaponPartItems() {}

    static {
        WEAPON_PART_BARREL_GENERIC = register("barrel");
        WEAPON_PART_BARREL_MINIGUN = register("barrel_minigun");
        WEAPON_PART_BARREL_ROCKET = register("barrel_rocket");
        WEAPON_PART_BARREL_SMART = register("barrel_smart");
        WEAPON_PART_GRIP_GENERIC = register("grip");
        WEAPON_PART_RECEIVER_GENERIC = register("receiver");
        WEAPON_PART_RECEIVER_SMART = register("receiver_smart");
        WEAPON_PART_STOCK_GENERIC = register("stock");
    }
}
