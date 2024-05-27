package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockHolderSet;
import org.avp.api.block.BlockTagData;
import org.avp.api.block.GrateBlock;
import org.avp.api.block.model.BlockModelData;
import org.avp.api.block.model.render_type.BlockModelRenderType;
import org.avp.api.block.model.type.BlockModelDataType;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPBlocks extends AVPDeferredBlockRegistry {

    public static final AVPBlocks INSTANCE = new AVPBlocks();

    public final FullBlockHolderMetalSet aluminum;

    public final BlockHolderMetalSet copper;

    public final FullBlockHolderMetalSet ferroaluminum;

    public final Holder<Block> orioniteBlock;

    public final FullBlockHolderMetalSet steel;

    public final FullBlockHolderMetalSet titanium;

    private BlockHolderSet createBlockHolderSet(Holder<Block> holder, BlockBehaviour.Properties properties, BlockData blockData) {
        return new BlockHolderSet(
            holder,
            createHolder(BlockData.toSlab(holder, properties, blockData)),
            createHolder(BlockData.toStairs(holder, properties, blockData)),
            createHolder(BlockData.toWall(holder, properties, blockData))
        );
    }

    private BlockHolderMetalSet createBlockHolderMetalSet(
        String baseName,
        BlockBehaviour.Properties properties,
        BlockTagData blockTagData
    ) {
        var cubeModelData = BlockModelData.cube(properties);

        var grateProperties = BlockBehaviour.Properties.of().noOcclusion();
        var grateModelData = new BlockModelData(
            () -> new GrateBlock(grateProperties),
            BlockModelDataType.Cube::new,
            BlockModelRenderType.TRANSLUCENT
        );

        var platedBlockData = new BlockData(baseName + "_plated", cubeModelData, blockTagData);
        var platedParentHolder = createHolder(platedBlockData);

        var platedStackBlockData = new BlockData(baseName + "_plated_stack", cubeModelData, blockTagData);
        var platedStackParentHolder = createHolder(platedStackBlockData);

        var platedChevronBlockData = new BlockData(baseName + "_plated_chevron", cubeModelData, blockTagData);
        var platedChevronParentHolder = createHolder(platedChevronBlockData);

        return new BlockHolderMetalSet(
            createHolder(baseName + "_grate", grateModelData, blockTagData),
            createBlockHolderSet(platedParentHolder, properties, platedBlockData),
            createBlockHolderSet(platedStackParentHolder, properties, platedStackBlockData),
            createBlockHolderSet(platedChevronParentHolder, properties, platedChevronBlockData),
            createHolder(baseName + "_vent", cubeModelData, blockTagData)
        );
    }

    private FullBlockHolderMetalSet createFullBlockHolderMetalSet(
        String baseName,
        BlockBehaviour.Properties properties,
        BlockTagData blockTagData
    ) {
        var cubeModelData = BlockModelData.cube(properties);

        var baseBlockData = new BlockData(baseName + "_block", cubeModelData, blockTagData);

        var cutBlockData = new BlockData(baseName + "_cut", cubeModelData, blockTagData);
        var cutParentHolder = createHolder(cutBlockData);

        return new FullBlockHolderMetalSet(
            createHolder(baseBlockData),
            createBlockHolderSet(cutParentHolder, properties, cutBlockData),
            createBlockHolderMetalSet(baseName, properties, blockTagData)
        );
    }

    private AVPBlocks() {
        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK); // TODO:
        var copperProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK);

        var stoneTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL));
        var ironTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
        var diamondTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL));

        aluminum = createFullBlockHolderMetalSet("aluminum", properties, stoneTier);
        copper = createBlockHolderMetalSet("copper", copperProperties, stoneTier);
        ferroaluminum = createFullBlockHolderMetalSet("ferroaluminum", properties, stoneTier);
        orioniteBlock = createHolder("orionite_block", BlockModelData.cube(properties), diamondTier);
        steel = createFullBlockHolderMetalSet("steel", properties, ironTier);
        titanium = createFullBlockHolderMetalSet("titanium", properties, ironTier);
    }

    public record FullBlockHolderMetalSet(
        Holder<Block> base,
        BlockHolderSet cutSet,
        BlockHolderMetalSet extendedSet
    ) {}

    public record BlockHolderMetalSet(
        Holder<Block> grate,
        BlockHolderSet platedSet,
        BlockHolderSet platedStackSet,
        BlockHolderSet platedChevronSet,
        Holder<Block> vent
    ) {}
}
