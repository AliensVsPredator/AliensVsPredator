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
public class AVPWeaponPartItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> WEAPON_PART_BARREL_GENERIC;

    public static final GameObject<Item> WEAPON_PART_BARREL_MINIGUN;

    public static final GameObject<Item> WEAPON_PART_BARREL_SMART;

    public static final GameObject<Item> WEAPON_PART_GRIP_GENERIC;

    public static final GameObject<Item> WEAPON_PART_RECEIVER_GENERIC;

    public static final GameObject<Item> WEAPON_PART_RECEIVER_SMART;

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

    private AVPWeaponPartItems() {}

    static {
        WEAPON_PART_BARREL_GENERIC = register("weapon_part_barrel", () -> new Item(new Item.Properties()));
        WEAPON_PART_BARREL_MINIGUN = register("weapon_part_barrel_minigun", () -> new Item(new Item.Properties()));
        WEAPON_PART_BARREL_SMART = register("weapon_part_barrel_smart", () -> new Item(new Item.Properties()));
        WEAPON_PART_GRIP_GENERIC = register("weapon_part_grip", () -> new Item(new Item.Properties()));
        WEAPON_PART_RECEIVER_GENERIC = register("weapon_part_receiver", () -> new Item(new Item.Properties()));
        WEAPON_PART_RECEIVER_SMART = register("weapon_part_receiver_smart", () -> new Item(new Item.Properties()));
    }
}
