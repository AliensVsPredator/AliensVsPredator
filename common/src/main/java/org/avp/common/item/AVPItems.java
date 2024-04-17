package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPItems extends AVPDeferredItemRegistry {

    public static final AVPItems INSTANCE = new AVPItems();

    public final Holder<Item> BOTTLE_TINTED;

    public final Holder<Item> BOTTLE_TINTED_ACID;

    public final Holder<Item> CARBON;

    public final Holder<Item> COBALT;

    public final Holder<Item> DUST_LITHIUM;

    public final Holder<Item> INGOT_ALUMINUM;

    public final Holder<Item> INGOT_ORIONITE;

    public final Holder<Item> INGOT_STEEL;

    public final Holder<Item> INGOT_TITANIUM;

    public final Holder<Item> LASER_MINE;

    public final Holder<Item> NBT_DRIVE;

    public final Holder<Item> NEODYMIUM;

    public final Holder<Item> NEODYMIUM_MAGNET;

    public final Holder<Item> POLYCARBONATE;

    public final Holder<Item> POLYMER;

    public final Holder<Item> RAW_BAUXITE;

    public final Holder<Item> RAW_TITANIUM;

    public final Holder<Item> ROYAL_JELLY;

    public final Holder<Item> SHURIKEN;

    public final Holder<Item> SILICA;

    public final Holder<Item> SMART_DISC;

    public final Holder<Item> VERITANIUM_SHARD;

    public final Holder<Item> XENOMORPH_CHITIN;

    public final Holder<Item> YAUTJA_ARTIFACT;

    private Holder<Item> createHolder(String registryName) {
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
