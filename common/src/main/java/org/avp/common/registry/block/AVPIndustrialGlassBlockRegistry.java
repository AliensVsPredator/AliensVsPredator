package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.data.block.BlockData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.game.block.ColoredTransparentBlock;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockModelRenderType;
import org.avp.api.common.data.block.BlockModelDataType;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.common.data.tag.AVPBlockTags;
import org.avp.common.data.tag.AVPItemTags;

public class AVPIndustrialGlassBlockRegistry extends AVPDeferredBlockRegistry {

    private static final List<ColoredIndustrialGlassHolder> COLORED_INDUSTRIAL_GLASS_ENTRIES = new ArrayList<>();

    public static final AVPIndustrialGlassBlockRegistry INSTANCE = new AVPIndustrialGlassBlockRegistry();

    public final BLHolder<Block> glass;

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
    protected BLHolder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("industrial_glass_"));
    }

    private AVPIndustrialGlassBlockRegistry() {
        var industrialGlassProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS);
        var blockTagData = new BlockTagData(
            Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, AVPBlockTags.INDUSTRIAL_GLASS),
            Set.of(AVPItemTags.INDUSTRIAL_GLASS)
        );

        glass = super.createHolder(
            new BlockData(
                "industrial_glass",
                new BlockModelData(
                    () -> new ColoredTransparentBlock(DyeColor.WHITE, industrialGlassProperties),
                    BlockModelDataType.Cube::new,
                    BlockModelRenderType.TRANSLUCENT
                ),
                blockTagData
            )
        );

        Function<DyeColor, BlockData> blockDataFactory = dyeColor -> new BlockData(
            dyeColor.getName(),
            new BlockModelData(
                () -> new ColoredTransparentBlock(dyeColor, industrialGlassProperties),
                BlockModelDataType.Cube::new,
                BlockModelRenderType.TRANSLUCENT
            ),
            blockTagData
        );

        glassBlack = createColoredIndustrialGlassHolder(DyeColor.BLACK, blockDataFactory);
        glassBlue = createColoredIndustrialGlassHolder(DyeColor.BLUE, blockDataFactory);
        glassBrown = createColoredIndustrialGlassHolder(DyeColor.BROWN, blockDataFactory);
        glassCyan = createColoredIndustrialGlassHolder(DyeColor.CYAN, blockDataFactory);
        glassGray = createColoredIndustrialGlassHolder(DyeColor.GRAY, blockDataFactory);
        glassGreen = createColoredIndustrialGlassHolder(DyeColor.GREEN, blockDataFactory);
        glassLightBlue = createColoredIndustrialGlassHolder(DyeColor.LIGHT_BLUE, blockDataFactory);
        glassLightGray = createColoredIndustrialGlassHolder(DyeColor.LIGHT_GRAY, blockDataFactory);
        glassLime = createColoredIndustrialGlassHolder(DyeColor.LIME, blockDataFactory);
        glassMagenta = createColoredIndustrialGlassHolder(DyeColor.MAGENTA, blockDataFactory);
        glassOrange = createColoredIndustrialGlassHolder(DyeColor.ORANGE, blockDataFactory);
        glassPink = createColoredIndustrialGlassHolder(DyeColor.PINK, blockDataFactory);
        glassPurple = createColoredIndustrialGlassHolder(DyeColor.PURPLE, blockDataFactory);
        glassRed = createColoredIndustrialGlassHolder(DyeColor.RED, blockDataFactory);
        glassWhite = createColoredIndustrialGlassHolder(DyeColor.WHITE, blockDataFactory);
        glassYellow = createColoredIndustrialGlassHolder(DyeColor.YELLOW, blockDataFactory);
    }

    private ColoredIndustrialGlassHolder createColoredIndustrialGlassHolder(
        DyeColor dyeColor,
        Function<DyeColor, BlockData> blockDataFactory
    ) {
        var blockData = blockDataFactory.apply(dyeColor);
        var holder = createHolder(blockData);
        var coloredIndustrialGlassHolder = new ColoredIndustrialGlassHolder(dyeColor, holder);
        COLORED_INDUSTRIAL_GLASS_ENTRIES.add(coloredIndustrialGlassHolder);
        return coloredIndustrialGlassHolder;
    }

    public record ColoredIndustrialGlassHolder(
        DyeColor dyeColor,
        BLHolder<Block> holder
    ) {}
}
