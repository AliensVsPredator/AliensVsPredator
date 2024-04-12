package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

public class AVPToolItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> TITANIUM_AXE;

    public static final GameObject<Item> TITANIUM_HOE;

    public static final GameObject<Item> TITANIUM_PICKAXE;

    public static final GameObject<Item> TITANIUM_SHOVEL;

    public static final GameObject<Item> TITANIUM_SWORD;

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

    private static GameObject<Item> register(String registryName) {
        return register(registryName, () -> new Item(new Item.Properties()));
    }

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register("tool_" + registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPToolItems() {}

    static {
        TITANIUM_AXE = register("titanium_axe");
        TITANIUM_HOE = register("titanium_hoe");
        TITANIUM_PICKAXE = register("titanium_pickaxe");
        TITANIUM_SHOVEL = register("titanium_shovel");
        TITANIUM_SWORD = register("titanium_sword");
        VERITANIUM_AXE = register("veritanium_axe");
        VERITANIUM_HOE = register("veritanium_hoe");
        VERITANIUM_PICKAXE = register("veritanium_pickaxe");
        VERITANIUM_SHOVEL = register("veritanium_shovel");
        VERITANIUM_SWORD = register("veritanium_sword");
    }
}
