package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.registry.AVPDeferredItemRegistry;
import org.avp.common.service.Services;

public class AVPItems extends AVPDeferredItemRegistry {

    public static final AVPItems INSTANCE = new AVPItems();

    public final GameObject<Item> BOTTLE_TINTED;

    public final GameObject<Item> BOTTLE_TINTED_ACID;

    public final GameObject<Item> CARBON;

    public final GameObject<Item> COBALT;

    public final GameObject<Item> DUST_LITHIUM;

    public final GameObject<Item> INGOT_ALUMINUM;

    public final GameObject<Item> INGOT_ORIONITE;

    public final GameObject<Item> INGOT_STEEL;

    public final GameObject<Item> INGOT_TITANIUM;

    public final GameObject<Item> LASER_MINE;

    public final GameObject<Item> NBT_DRIVE;

    public final GameObject<Item> NEODYMIUM;

    public final GameObject<Item> NEODYMIUM_MAGNET;

    public final GameObject<Item> POLYCARBONATE;

    public final GameObject<Item> POLYMER;

    public final GameObject<Item> RAW_BAUXITE;

    public final GameObject<Item> RAW_TITANIUM;

    public final GameObject<Item> ROYAL_JELLY;

    public final GameObject<Item> SHURIKEN;

    public final GameObject<Item> SILICA;

    public final GameObject<Item> SMART_DISC;

    public final GameObject<Item> VERITANIUM_SHARD;

    public final GameObject<Item> XENOMORPH_CHITIN;

    public final GameObject<Item> YAUTJA_ARTIFACT;

    private GameObject<Item> createHolder(String registryName) {
        return createHolder(registryName, () -> new Item(new Item.Properties()));
    }

    private AVPItems() {
        BOTTLE_TINTED = createHolder("bottle_tinted");
        BOTTLE_TINTED_ACID = createHolder("bottle_tinted_acid");
        CARBON = createHolder("carbon");
        COBALT = createHolder("cobalt");
        DUST_LITHIUM = createHolder("dust_lithium");
        INGOT_ALUMINUM = createHolder("ingot_aluminum");
        INGOT_ORIONITE = createHolder("ingot_orionite");
        INGOT_STEEL = createHolder("ingot_steel");
        INGOT_TITANIUM = createHolder("ingot_titanium");
        LASER_MINE = createHolder("laser_mine");
        NBT_DRIVE = createHolder("nbt_drive");
        NEODYMIUM = createHolder("neodymium");
        NEODYMIUM_MAGNET = createHolder("neodymium_magnet");
        POLYCARBONATE = createHolder("polycarbonate");
        POLYMER = createHolder("polymer");
        RAW_BAUXITE = createHolder("raw_bauxite");
        RAW_TITANIUM = createHolder("raw_titanium");
        ROYAL_JELLY = createHolder("royal_jelly");
        SHURIKEN = createHolder("shuriken");
        SILICA = createHolder("silica");
        SMART_DISC = createHolder("smart_disc");
        VERITANIUM_SHARD = createHolder("veritanium_shard");
        XENOMORPH_CHITIN = createHolder("xenomorph_chitin");
        YAUTJA_ARTIFACT = createHolder("yautja_artifact");
    }
}
