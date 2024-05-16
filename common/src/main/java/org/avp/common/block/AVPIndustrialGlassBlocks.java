package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;
import org.avp.common.tag.AVPBlockTags;
import org.avp.common.tag.AVPItemTags;

import java.util.ArrayList;
import java.util.List;

public class AVPIndustrialGlassBlocks extends AVPDeferredBlockRegistry {

    private static final List<ColoredIndustrialGlassHolder> COLORED_INDUSTRIAL_GLASS_ENTRIES = new ArrayList<>();

    public static final AVPIndustrialGlassBlocks INSTANCE = new AVPIndustrialGlassBlocks();

    public final Holder<Block> glass;

    public final ColoredIndustrialGlassHolder glassBlack;

    public final ColoredIndustrialGlassHolder glassBlue;

    public final ColoredIndustrialGlassHolder glassBrown;

    public final ColoredIndustrialGlassHolder glassCyan;

    public final ColoredIndustrialGlassHolder glassGray;

    public final ColoredIndustrialGlassHolder glassGreen;

    public final ColoredIndustrialGlassHolder glassLightBlue;

    public final ColoredIndustrialGlassHolder glassLightGray;

    public final ColoredIndustrialGlassHolder glassLime;

    public final ColoredIndustrialGlassHolder glassMagenta;

    public final ColoredIndustrialGlassHolder glassOrange;

    public final ColoredIndustrialGlassHolder glassPink;

    public final ColoredIndustrialGlassHolder glassPurple;

    public final ColoredIndustrialGlassHolder glassRed;

    public final ColoredIndustrialGlassHolder glassWhite;

    public final ColoredIndustrialGlassHolder glassYellow;

    public static List<ColoredIndustrialGlassHolder> getColoredIndustrialGlassEntries() {
        return COLORED_INDUSTRIAL_GLASS_ENTRIES;
    }

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("industrial_glass_" + registryName, blockDataBuilder);
    }

    private AVPIndustrialGlassBlocks() {
        var industrialGlassProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS);
        var blockTags = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, AVPBlockTags.INDUSTRIAL_GLASS);
        var itemTags = List.of(AVPItemTags.INDUSTRIAL_GLASS);
        var blockDataBuilder = BlockDataUtils.transparent(industrialGlassProperties).blockTags(blockTags).itemTags(itemTags);

        glass = super.createHolder("industrial_glass", blockDataBuilder);

        glassBlack = createColoredIndustrialGlassHolder(Items.BLACK_DYE, "black", blockDataBuilder);
        glassBlue = createColoredIndustrialGlassHolder(Items.BLUE_DYE, "blue", blockDataBuilder);
        glassBrown = createColoredIndustrialGlassHolder(Items.BROWN_DYE, "brown", blockDataBuilder);
        glassCyan = createColoredIndustrialGlassHolder(Items.CYAN_DYE, "cyan", blockDataBuilder);
        glassGray = createColoredIndustrialGlassHolder(Items.GRAY_DYE, "gray", blockDataBuilder);
        glassGreen = createColoredIndustrialGlassHolder(Items.GREEN_DYE, "green", blockDataBuilder);
        glassLightBlue = createColoredIndustrialGlassHolder(Items.LIGHT_BLUE_DYE, "light_blue", blockDataBuilder);
        glassLightGray = createColoredIndustrialGlassHolder(Items.LIGHT_GRAY_DYE, "light_gray", blockDataBuilder);
        glassLime = createColoredIndustrialGlassHolder(Items.LIME_DYE, "lime", blockDataBuilder);
        glassMagenta = createColoredIndustrialGlassHolder(Items.MAGENTA_DYE, "magenta", blockDataBuilder);
        glassOrange = createColoredIndustrialGlassHolder(Items.ORANGE_DYE, "orange", blockDataBuilder);
        glassPink = createColoredIndustrialGlassHolder(Items.PINK_DYE, "pink", blockDataBuilder);
        glassPurple = createColoredIndustrialGlassHolder(Items.PURPLE_DYE, "purple", blockDataBuilder);
        glassRed = createColoredIndustrialGlassHolder(Items.RED_DYE, "red", blockDataBuilder);
        glassWhite = createColoredIndustrialGlassHolder(Items.WHITE_DYE, "white", blockDataBuilder);
        glassYellow = createColoredIndustrialGlassHolder(Items.YELLOW_DYE, "yellow", blockDataBuilder);
    }

    private ColoredIndustrialGlassHolder createColoredIndustrialGlassHolder(Item dyeItem, String name, BlockData.Builder blockDataBuilder) {
        var coloredIndustrialGlassHolder = new ColoredIndustrialGlassHolder(dyeItem, createHolder(name, blockDataBuilder));
        COLORED_INDUSTRIAL_GLASS_ENTRIES.add(coloredIndustrialGlassHolder);
        return coloredIndustrialGlassHolder;
    }

    public record ColoredIndustrialGlassHolder(Item dyeItem, Holder<Block> holder) {}
}
