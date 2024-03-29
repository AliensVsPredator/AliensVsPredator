package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

/**
 * @author Boston Vanseghi
 */
public class AVPToolItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> CELTIC_AXE;

    public static final GameObject<Item> CELTIC_HOE;

    public static final GameObject<Item> CELTIC_PICKAXE;

    public static final GameObject<Item> CELTIC_SHOVEL;

    public static final GameObject<Item> CELTIC_SWORD;

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
        CELTIC_AXE = register("tool_celtic_axe", () -> new Item(new Item.Properties()));
        CELTIC_HOE = register("tool_celtic_hoe", () -> new Item(new Item.Properties()));
        CELTIC_PICKAXE = register("tool_celtic_pickaxe", () -> new Item(new Item.Properties()));
        CELTIC_SHOVEL = register("tool_celtic_shovel", () -> new Item(new Item.Properties()));
        CELTIC_SWORD = register("tool_celtic_sword", () -> new Item(new Item.Properties()));
    }
}
