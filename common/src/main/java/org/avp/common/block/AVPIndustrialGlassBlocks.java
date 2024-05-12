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

import java.util.List;

public class AVPIndustrialGlassBlocks extends AVPDeferredBlockRegistry {

    public static final AVPIndustrialGlassBlocks INSTANCE = new AVPIndustrialGlassBlocks();

    public final Holder<Block> glass;

    public final IndustrialGlassHolder glassBlack;

    public final IndustrialGlassHolder glassBlue;

    public final IndustrialGlassHolder glassBrown;

    public final IndustrialGlassHolder glassCyan;

    public final IndustrialGlassHolder glassGray;

    public final IndustrialGlassHolder glassGreen;

    public final IndustrialGlassHolder glassLightBlue;

    public final IndustrialGlassHolder glassLightGray;

    public final IndustrialGlassHolder glassLime;

    public final IndustrialGlassHolder glassMagenta;

    public final IndustrialGlassHolder glassOrange;

    public final IndustrialGlassHolder glassPink;

    public final IndustrialGlassHolder glassPurple;

    public final IndustrialGlassHolder glassRed;

    public final IndustrialGlassHolder glassWhite;

    public final IndustrialGlassHolder glassYellow;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("industrial_glass_" + registryName, blockDataBuilder);
    }

    private AVPIndustrialGlassBlocks() {
        var industrialGlassProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS);
        var glassTags = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, AVPBlockTags.INDUSTRIAL_GLASS);
        var blockDataBuilder = BlockDataUtils.transparent(industrialGlassProperties).tags(glassTags);

        glass = super.createHolder("industrial_glass", blockDataBuilder);

        glassBlack = createCustomHolder(Items.BLACK_DYE, "black", blockDataBuilder);
        glassBlue = createCustomHolder(Items.BLUE_DYE, "blue", blockDataBuilder);
        glassBrown = createCustomHolder(Items.BROWN_DYE, "brown", blockDataBuilder);
        glassCyan = createCustomHolder(Items.CYAN_DYE, "cyan", blockDataBuilder);
        glassGray = createCustomHolder(Items.GRAY_DYE, "gray", blockDataBuilder);
        glassGreen = createCustomHolder(Items.GREEN_DYE, "green", blockDataBuilder);
        glassLightBlue = createCustomHolder(Items.LIGHT_BLUE_DYE, "light_blue", blockDataBuilder);
        glassLightGray = createCustomHolder(Items.LIGHT_GRAY_DYE, "light_gray", blockDataBuilder);
        glassLime = createCustomHolder(Items.LIME_DYE, "lime", blockDataBuilder);
        glassMagenta = createCustomHolder(Items.MAGENTA_DYE, "magenta", blockDataBuilder);
        glassOrange = createCustomHolder(Items.ORANGE_DYE, "orange", blockDataBuilder);
        glassPink = createCustomHolder(Items.PINK_DYE, "pink", blockDataBuilder);
        glassPurple = createCustomHolder(Items.PURPLE_DYE, "purple", blockDataBuilder);
        glassRed = createCustomHolder(Items.RED_DYE, "red", blockDataBuilder);
        glassWhite = createCustomHolder(Items.WHITE_DYE, "white", blockDataBuilder);
        glassYellow = createCustomHolder(Items.YELLOW_DYE, "yellow", blockDataBuilder);
    }

    private IndustrialGlassHolder createCustomHolder(Item dyeItem, String name, BlockData.Builder blockDataBuilder) {
        return new IndustrialGlassHolder(dyeItem, createHolder(name, blockDataBuilder));
    }

    public record IndustrialGlassHolder(
        Item dyeItem,
        Holder<Block> holder
    ) {}
}
