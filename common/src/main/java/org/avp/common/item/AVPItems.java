package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPItems extends AVPDeferredItemRegistry {

    public static final AVPItems INSTANCE = new AVPItems();

    public final Holder<Item> bottleTinted;

    public final Holder<Item> bottleTintedAcid;

    public final Holder<Item> carbon;

    public final Holder<Item> cobalt;

    public final Holder<Item> dustLithium;

    public final Holder<Item> ingotAluminum;

    public final Holder<Item> ingotOrionite;

    public final Holder<Item> ingotSteel;

    public final Holder<Item> ingotTitanium;

    public final Holder<Item> laserMine;

    public final Holder<Item> nbtDrive;

    public final Holder<Item> neodymium;

    public final Holder<Item> neodymiumMagnet;

    public final Holder<Item> polycarbonate;

    public final Holder<Item> polymer;

    public final Holder<Item> rawBauxite;

    public final Holder<Item> rawTitanium;

    public final Holder<Item> royalJelly;

    public final Holder<Item> shuriken;

    public final Holder<Item> silica;

    public final Holder<Item> smartDisc;

    public final Holder<Item> veritaniumShard;

    public final Holder<Item> xenomorphChitin;

    public final Holder<Item> yautjaArtifact;

    private AVPItems() {
        bottleTinted = createHolder("bottle_tinted");
        bottleTintedAcid = createHolder("bottle_tinted_acid");
        carbon = createHolder("carbon");
        cobalt = createHolder("cobalt");
        dustLithium = createHolder("dust_lithium");
        ingotAluminum = createHolder("ingot_aluminum");
        ingotOrionite = createHolder("ingot_orionite");
        ingotSteel = createHolder("ingot_steel");
        ingotTitanium = createHolder("ingot_titanium");
        laserMine = createHolder("laser_mine");
        nbtDrive = createHolder("nbt_drive");
        neodymium = createHolder("neodymium");
        neodymiumMagnet = createHolder("neodymium_magnet");
        polycarbonate = createHolder("polycarbonate");
        polymer = createHolder("polymer");
        rawBauxite = createHolder("raw_bauxite");
        rawTitanium = createHolder("raw_titanium");
        royalJelly = createHolder("royal_jelly");
        shuriken = createHolder("shuriken");
        silica = createHolder("silica");
        smartDisc = createHolder("smart_disc");
        veritaniumShard = createHolder("veritanium_shard");
        xenomorphChitin = createHolder("xenomorph_chitin");
        yautjaArtifact = createHolder("yautja_artifact");
    }
}
