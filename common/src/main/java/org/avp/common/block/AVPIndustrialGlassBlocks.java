package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

import java.util.List;

public class AVPIndustrialGlassBlocks extends AVPDeferredBlockRegistry {

    public static final AVPIndustrialGlassBlocks INSTANCE = new AVPIndustrialGlassBlocks();

    public final Holder<Block> grayGlass;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("industrial_glass_" + registryName, blockDataBuilder);
    }

    private AVPIndustrialGlassBlocks() {
        var industrialGlassProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS);

        var stoneOrMetal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        grayGlass = createHolder("gray", BlockDataUtils.transparent(industrialGlassProperties).tags(stoneOrMetal));
    }
}
