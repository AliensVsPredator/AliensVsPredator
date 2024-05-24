package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockHolderSet;
import org.avp.api.block.BlockHolderSetData;
import org.avp.api.block.BlockModelData;
import org.avp.api.block.BlockTagData;
import org.avp.common.registry.AVPDeferredBlockRegistry;

import java.util.Set;

public class AVPYautjaShipBlocks extends AVPDeferredBlockRegistry {

    public static final AVPYautjaShipBlocks INSTANCE = new AVPYautjaShipBlocks();

    public final BlockHolderSet brick;

    public final Holder<Block> decor1;

    public final Holder<Block> decor2;

    public final BlockHolderSet decor3;

    public final Holder<Block> panel;

    public final Holder<Block> supportPillar;

    public final Holder<Block> wallBase;

    @Override
    protected Holder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("yautja_ship_"));
    }

    private AVPYautjaShipBlocks() {
        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
            .mapColor(MapColor.COLOR_RED)
            .strength(75, 1500);

        var tags = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL));

        var brickBlockData = new BlockData("brick", BlockModelData.cube(properties), tags);
        brick = registerBlockHolderSet(new BlockHolderSetData(properties, brickBlockData));

        decor1 = createHolder("decor_1", BlockModelData.cube(properties), tags);
        decor2 = createHolder("decor_2", BlockModelData.cube(properties), tags);

        var decor3BlockData = new BlockData("decor_3", BlockModelData.cube(properties), tags);
        decor3 = registerBlockHolderSet(new BlockHolderSetData(properties, decor3BlockData));

        panel = createHolder("panel", BlockModelData.cube(properties), tags);
        supportPillar = createHolder("support_pillar", BlockModelData.cube(properties), tags);
        wallBase = createHolder("wall_base", BlockModelData.cube(properties), tags);
    }
}
