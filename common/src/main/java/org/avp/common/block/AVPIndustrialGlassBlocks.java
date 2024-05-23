package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;
import org.avp.common.tag.AVPBlockTags;
import org.avp.common.tag.AVPItemTags;

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
        Function<DyeColor, BlockData.Builder> blockDataBuilder = dyeColor -> BlockDataUtils.transparent(dyeColor, industrialGlassProperties)
            .blockTags(blockTags)
            .itemTags(itemTags);

        glass = super.createHolder("industrial_glass", blockDataBuilder.apply(DyeColor.WHITE));

        glassBlack = createColoredIndustrialGlassHolder(DyeColor.BLACK, blockDataBuilder);
        glassBlue = createColoredIndustrialGlassHolder(DyeColor.BLUE, blockDataBuilder);
        glassBrown = createColoredIndustrialGlassHolder(DyeColor.BROWN, blockDataBuilder);
        glassCyan = createColoredIndustrialGlassHolder(DyeColor.CYAN, blockDataBuilder);
        glassGray = createColoredIndustrialGlassHolder(DyeColor.GRAY, blockDataBuilder);
        glassGreen = createColoredIndustrialGlassHolder(DyeColor.GREEN, blockDataBuilder);
        glassLightBlue = createColoredIndustrialGlassHolder(DyeColor.LIGHT_BLUE, blockDataBuilder);
        glassLightGray = createColoredIndustrialGlassHolder(DyeColor.LIGHT_GRAY, blockDataBuilder);
        glassLime = createColoredIndustrialGlassHolder(DyeColor.LIME, blockDataBuilder);
        glassMagenta = createColoredIndustrialGlassHolder(DyeColor.MAGENTA, blockDataBuilder);
        glassOrange = createColoredIndustrialGlassHolder(DyeColor.ORANGE, blockDataBuilder);
        glassPink = createColoredIndustrialGlassHolder(DyeColor.PINK, blockDataBuilder);
        glassPurple = createColoredIndustrialGlassHolder(DyeColor.PURPLE, blockDataBuilder);
        glassRed = createColoredIndustrialGlassHolder(DyeColor.RED, blockDataBuilder);
        glassWhite = createColoredIndustrialGlassHolder(DyeColor.WHITE, blockDataBuilder);
        glassYellow = createColoredIndustrialGlassHolder(DyeColor.YELLOW, blockDataBuilder);
    }

    private ColoredIndustrialGlassHolder createColoredIndustrialGlassHolder(
        DyeColor dyeColor,
        Function<DyeColor, BlockData.Builder> blockDataBuilder
    ) {
        var coloredIndustrialGlassHolder = new ColoredIndustrialGlassHolder(
            dyeColor,
            createHolder(dyeColor.getName(), blockDataBuilder.apply(dyeColor))
        );
        COLORED_INDUSTRIAL_GLASS_ENTRIES.add(coloredIndustrialGlassHolder);
        return coloredIndustrialGlassHolder;
    }

    public record ColoredIndustrialGlassHolder(
        DyeColor dyeColor,
        Holder<Block> holder
    ) {}
}
