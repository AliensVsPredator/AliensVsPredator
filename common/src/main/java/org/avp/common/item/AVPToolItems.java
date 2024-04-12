package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

public class AVPToolItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> VERITANIUM_AXE;

    public static final GameObject<Item> VERITANIUM_HOE;

    public static final GameObject<Item> VERITANIUM_PICKAXE;

    public static final GameObject<Item> VERITANIUM_SHOVEL;

    public static final GameObject<Item> VERITANIUM_SWORD;

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

    private AVPToolItems() {}

    static {
        VERITANIUM_AXE = register("tool_veritanium_axe", () -> new Item(new Item.Properties()));
        VERITANIUM_HOE = register("tool_veritanium_hoe", () -> new Item(new Item.Properties()));
        VERITANIUM_PICKAXE = register("tool_veritanium_pickaxe", () -> new Item(new Item.Properties()));
        VERITANIUM_SHOVEL = register("tool_veritanium_shovel", () -> new Item(new Item.Properties()));
        VERITANIUM_SWORD = register("tool_veritanium_sword", () -> new Item(new Item.Properties()));
    }
}
