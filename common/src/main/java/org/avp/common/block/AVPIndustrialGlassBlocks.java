package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockModelData;
import org.avp.api.block.BlockTagData;
import org.avp.api.block.CustomTransparentBlock;
import org.avp.api.block.model.provider.CubeBlockModelProvider;
import org.avp.api.block.model.render_type.BlockModelRenderType;
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
    protected Holder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("industrial_glass_"));
    }

    private AVPIndustrialGlassBlocks() {
        var industrialGlassProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS);
        var blockTagData = new BlockTagData(
            Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, AVPBlockTags.INDUSTRIAL_GLASS),
            Set.of(AVPItemTags.INDUSTRIAL_GLASS)
        );

        glass = super.createHolder(new BlockData(
            "industrial_glass",
            new BlockModelData(
                () -> new CustomTransparentBlock(DyeColor.WHITE, industrialGlassProperties),
                CubeBlockModelProvider::new,
                BlockModelRenderType.TRANSLUCENT
            ),
            blockTagData
        ));

        Function<DyeColor, BlockData> blockDataProvider = dyeColor -> new BlockData(
            dyeColor.getName(),
            new BlockModelData(
                () -> new CustomTransparentBlock(dyeColor, industrialGlassProperties),
                CubeBlockModelProvider::new,
                BlockModelRenderType.TRANSLUCENT
            ),
            blockTagData
        );

        glassBlack = createColoredIndustrialGlassHolder(DyeColor.BLACK, blockDataProvider);
        glassBlue = createColoredIndustrialGlassHolder(DyeColor.BLUE, blockDataProvider);
        glassBrown = createColoredIndustrialGlassHolder(DyeColor.BROWN, blockDataProvider);
        glassCyan = createColoredIndustrialGlassHolder(DyeColor.CYAN, blockDataProvider);
        glassGray = createColoredIndustrialGlassHolder(DyeColor.GRAY, blockDataProvider);
        glassGreen = createColoredIndustrialGlassHolder(DyeColor.GREEN, blockDataProvider);
        glassLightBlue = createColoredIndustrialGlassHolder(DyeColor.LIGHT_BLUE, blockDataProvider);
        glassLightGray = createColoredIndustrialGlassHolder(DyeColor.LIGHT_GRAY, blockDataProvider);
        glassLime = createColoredIndustrialGlassHolder(DyeColor.LIME, blockDataProvider);
        glassMagenta = createColoredIndustrialGlassHolder(DyeColor.MAGENTA, blockDataProvider);
        glassOrange = createColoredIndustrialGlassHolder(DyeColor.ORANGE, blockDataProvider);
        glassPink = createColoredIndustrialGlassHolder(DyeColor.PINK, blockDataProvider);
        glassPurple = createColoredIndustrialGlassHolder(DyeColor.PURPLE, blockDataProvider);
        glassRed = createColoredIndustrialGlassHolder(DyeColor.RED, blockDataProvider);
        glassWhite = createColoredIndustrialGlassHolder(DyeColor.WHITE, blockDataProvider);
        glassYellow = createColoredIndustrialGlassHolder(DyeColor.YELLOW, blockDataProvider);
    }

    private ColoredIndustrialGlassHolder createColoredIndustrialGlassHolder(DyeColor dyeColor, Function<DyeColor, BlockData> blockDataProvider) {
        var blockData = blockDataProvider.apply(dyeColor);
        var holder = createHolder(blockData);
        var coloredIndustrialGlassHolder = new ColoredIndustrialGlassHolder(dyeColor, holder);
        COLORED_INDUSTRIAL_GLASS_ENTRIES.add(coloredIndustrialGlassHolder);
        return coloredIndustrialGlassHolder;
    }

    public record ColoredIndustrialGlassHolder(DyeColor dyeColor, Holder<Block> holder) {}
}
