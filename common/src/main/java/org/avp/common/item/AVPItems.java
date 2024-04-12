package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

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

    public static final GameObject<Item> POLYMER;

    public static final GameObject<Item> RAW_BAUXITE;

    public static final GameObject<Item> ROYAL_JELLY;

    public static final GameObject<Item> SHURIKEN;

    public static final GameObject<Item> SILICA;

    public static final GameObject<Item> SMART_DISC;

    public static final GameObject<Item> VERITANIUM_SHARD;

    public static final GameObject<Item> XENOMORPH_CHITIN;

    public static final GameObject<Item> YAUTJA_ARTIFACT;

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
        var gameObject = Services.ITEM_REGISTRY.register(registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPItems() {}

    static {
        CARBON = register("carbon");
        COBALT = register("cobalt");
        INGOT_ALUMINUM = register("ingot_aluminum");
        INGOT_LITHIUM = register("ingot_lithium");
        INGOT_STEEL = register("ingot_steel");
        LASER_MINE = register("laser_mine");
        NBT_DRIVE = register("nbt_drive");
        NEODYMIUM = register("neodymium");
        NEODYMIUM_MAGNET = register("neodymium_magnet");
        POLYCARBONATE = register("polycarbonate");
        POLYMER = register("polymer");
        RAW_BAUXITE = register("raw_bauxite");
        ROYAL_JELLY = register("royal_jelly");
        SHURIKEN = register("shuriken");
        SILICA = register("silica");
        SMART_DISC = register("smart_disc");
        VERITANIUM_SHARD = register("veritanium_shard");
        XENOMORPH_CHITIN = register("xenomorph_chitin");
        YAUTJA_ARTIFACT = register("yautja_artifact");
    }
}
