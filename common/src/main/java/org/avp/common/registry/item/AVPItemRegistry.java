package org.avp.common.registry.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.Set;

import org.avp.api.registry.holder.BLHolder;
import org.avp.common.game.item.TintedBottleItem;
import org.avp.api.registry.AVPDeferredItemRegistry;
import org.avp.common.data.tag.AVPItemTags;

public class AVPItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPItemRegistry INSTANCE = new AVPItemRegistry();

    public final BLHolder<Item> bottleTinted;

    public final BLHolder<Item> bottleTintedAcid;

    public final BLHolder<Item> carbon;

    public final BLHolder<Item> cobalt;

    public final BLHolder<Item> dustLithium;

    public final BLHolder<Item> ingotAluminum;

    public final BLHolder<Item> ingotOrionite;

    public final BLHolder<Item> ingotSteel;

    public final BLHolder<Item> ingotTitanium;

    public final BLHolder<Item> laserMine;

    public final BLHolder<Item> nbtDrive;

    public final BLHolder<Item> neodymium;

    public final BLHolder<Item> neodymiumMagnet;

    public final BLHolder<Item> polycarbonate;

    public final BLHolder<Item> polymer;

    public final BLHolder<Item> rawBauxite;

    public final BLHolder<Item> rawTitanium;

    public final BLHolder<Item> resinBall;

    public final BLHolder<Item> royalJelly;

    public final BLHolder<Item> sheetOrionite;

    public final BLHolder<Item> shuriken;

    public final BLHolder<Item> silica;

    public final BLHolder<Item> smartDisc;

    public final BLHolder<Item> veritaniumShard;

    public final BLHolder<Item> xenomorphChitin;

    public final BLHolder<Item> yautjaArtifact;

    private AVPItemRegistry() {
        bottleTinted = createHolder("bottle_tinted", TintedBottleItem::new);
        bottleTintedAcid = createHolder("bottle_tinted_acid", new Item.Properties().craftRemainder(bottleTinted.get()));
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
        resinBall = createHolder("resin_ball", Set.of(AVPItemTags.ACID_IMMUNE));
        royalJelly = createHolder("royal_jelly", new Item.Properties().rarity(Rarity.UNCOMMON), Set.of(AVPItemTags.ACID_IMMUNE));
        sheetOrionite = createHolder("sheet_orionite");
        shuriken = createHolder("shuriken");
        silica = createHolder("silica");
        smartDisc = createHolder("smart_disc");
        veritaniumShard = createHolder("veritanium_shard");
        xenomorphChitin = createHolder("xenomorph_chitin", Set.of(AVPItemTags.ACID_IMMUNE));
        yautjaArtifact = createHolder("yautja_artifact");
    }
}
