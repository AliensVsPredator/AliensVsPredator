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
public class AVPItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> CARBON;

    public static final GameObject<Item> COBALT;

    public static final GameObject<Item> INGOT_ALUMINUM;

    public static final GameObject<Item> INGOT_LITHIUM;

    public static final GameObject<Item> INGOT_STEEL;

    public static final GameObject<Item> LASER_MINE;

    public static final GameObject<Item> NBT_DRIVE;

    public static final GameObject<Item> NEODYMIUM;

    public static final GameObject<Item> NEODYMIUM_MAGNET;

    public static final GameObject<Item> POLYCARBONATE;

    public static final GameObject<Item> RAW_BAUXITE;

    public static final GameObject<Item> ROYAL_JELLY;

    public static final GameObject<Item> SHURIKEN;

    public static final GameObject<Item> SILICA;

    public static final GameObject<Item> SMART_DISC;

    public static final GameObject<Item> YAUTJA_ARTIFACT;

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

    private AVPItems() {}

    static {
        CARBON = register("carbon", () -> new Item(new Item.Properties()));
        COBALT = register("cobalt", () -> new Item(new Item.Properties()));
        INGOT_ALUMINUM = register("ingot_aluminum", () -> new Item(new Item.Properties()));
        INGOT_LITHIUM = register("ingot_lithium", () -> new Item(new Item.Properties()));
        INGOT_STEEL = register("ingot_steel", () -> new Item(new Item.Properties()));
        LASER_MINE = register("laser_mine", () -> new Item(new Item.Properties()));
        NBT_DRIVE = register("nbt_drive", () -> new Item(new Item.Properties()));
        NEODYMIUM = register("neodymium", () -> new Item(new Item.Properties()));
        NEODYMIUM_MAGNET = register("neodymium_magnet", () -> new Item(new Item.Properties()));
        POLYCARBONATE = register("polycarbonate", () -> new Item(new Item.Properties()));
        RAW_BAUXITE = register("raw_bauxite", () -> new Item(new Item.Properties()));
        ROYAL_JELLY = register("royal_jelly", () -> new Item(new Item.Properties()));
        SHURIKEN = register("shuriken", () -> new Item(new Item.Properties()));
        SILICA = register("silica", () -> new Item(new Item.Properties()));
        SMART_DISC = register("smart_disc", () -> new Item(new Item.Properties()));
        YAUTJA_ARTIFACT = register("yautja_artifact", () -> new Item(new Item.Properties()));
    }
}
