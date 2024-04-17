package org.avp.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPParadiseBlocks extends AVPDeferredBlockRegistry {

    public static final AVPParadiseBlocks INSTANCE = new AVPParadiseBlocks();

    public final Holder<Block> DIRT;

    public final Holder<Block> DIRT_MOSSY;

    public final Holder<Block> DIRT_PODZOL;

    public final Holder<Block> FENCE_GATE_LARGE;

    public final Holder<Block> FENCE_GATE_MEDIUM;

    public final Holder<Block> FENCE_GATE_SMALL;

    public final Holder<Block> FENCE_LARGE;

    public final Holder<Block> FENCE_MEDIUM;

    public final Holder<Block> FENCE_SMALL;

    public final Holder<Block> GRASS;

    public final Holder<Block> LEAVES_LARGE;

    public final Holder<Block> LEAVES_MEDIUM;

    public final Holder<Block> LEAVES_SMALL;

    public final Holder<Block> LOG_LARGE;

    public final Holder<Block> LOG_LARGE_PLANKS;

    public final Holder<Block> LOG_MEDIUM;

    public final Holder<Block> LOG_MEDIUM_PLANKS;

    public final Holder<Block> LOG_SMALL;

    public final Holder<Block> LOG_SMALL_PLANKS;

    public final Holder<Block> SLAB_LARGE;

    public final Holder<Block> SLAB_MEDIUM;

    public final Holder<Block> SLAB_SMALL;

    public final Holder<Block> STAIRS_LARGE;

    public final Holder<Block> STAIRS_MEDIUM;

    public final Holder<Block> STAIRS_SMALL;

    public final Holder<Block> WOOD_LARGE;

    public final Holder<Block> WOOD_MEDIUM;

    public final Holder<Block> WOOD_SMALL;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("paradise_" + registryName, blockDataBuilder);
    }

    private AVPParadiseBlocks() {
        DIRT = createHolder("dirt", BlockData.simple(BlockBehaviour.Properties.of()));
        DIRT_MOSSY = createHolder(
            "dirt_mossy",
            BlockData.simple(BlockBehaviour.Properties.of())
        );
        DIRT_PODZOL = createHolder(
            "dirt_podzol",
            BlockDataUtils.grass(DIRT, BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL))
        );
        GRASS = createHolder(
            "grass",
            BlockDataUtils.grass(DIRT, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK))
        );

        LEAVES_LARGE = createHolder("leaves_large", BlockData.simple(BlockBehaviour.Properties.of()));
        LEAVES_MEDIUM = createHolder("leaves_medium", BlockData.simple(BlockBehaviour.Properties.of()));
        LEAVES_SMALL = createHolder("leaves_small", BlockData.simple(BlockBehaviour.Properties.of()));

        LOG_LARGE = createHolder("log_large", BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of()));
        LOG_LARGE_PLANKS = createHolder("log_large_planks", BlockData.simple(BlockBehaviour.Properties.of()));

        LOG_MEDIUM = createHolder("log_medium", BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of()));
        LOG_MEDIUM_PLANKS = createHolder(
            "log_medium_planks",
            BlockData.simple(BlockBehaviour.Properties.of())
        );

        LOG_SMALL = createHolder(
            "log_small",
            BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of())
        );
        LOG_SMALL_PLANKS = createHolder(
            "log_small_planks",
            BlockData.simple(BlockBehaviour.Properties.of())
        );

        FENCE_GATE_LARGE = createHolder(
            "fence_gate_large",
            BlockDataUtils.fenceGate(LOG_LARGE_PLANKS, AVPWoodType.LARGE, BlockBehaviour.Properties.of())
        );
        FENCE_GATE_MEDIUM = createHolder(
            "fence_gate_medium",
            BlockDataUtils.fenceGate(LOG_MEDIUM_PLANKS, AVPWoodType.MEDIUM, BlockBehaviour.Properties.of())
        );
        FENCE_GATE_SMALL = createHolder(
            "fence_gate_small",
            BlockDataUtils.fenceGate(LOG_SMALL_PLANKS, AVPWoodType.SMALL, BlockBehaviour.Properties.of())
        );

        FENCE_LARGE = createHolder(
            "fence_large",
            BlockDataUtils.fence(LOG_LARGE_PLANKS, BlockBehaviour.Properties.of())
        );
        FENCE_MEDIUM = createHolder(
            "fence_medium",
            BlockDataUtils.fence(LOG_MEDIUM_PLANKS, BlockBehaviour.Properties.of())
        );
        FENCE_SMALL = createHolder(
            "fence_small",
            BlockDataUtils.fence(LOG_SMALL_PLANKS, BlockBehaviour.Properties.of())
        );

        SLAB_LARGE = createHolder(
            "slab_large",
            BlockDataUtils.slab(LOG_LARGE_PLANKS, BlockBehaviour.Properties.of())
        );
        SLAB_MEDIUM = createHolder(
            "slab_medium",
            BlockDataUtils.slab(LOG_MEDIUM_PLANKS, BlockBehaviour.Properties.of())
        );
        SLAB_SMALL = createHolder(
            "slab_small",
            BlockDataUtils.slab(LOG_SMALL_PLANKS, BlockBehaviour.Properties.of())
        );

        STAIRS_LARGE = createHolder(
            "stairs_large",
            BlockDataUtils.stairs(LOG_LARGE_PLANKS, BlockBehaviour.Properties.of())
        );
        STAIRS_MEDIUM = createHolder(
            "stairs_medium",
            BlockDataUtils.stairs(LOG_MEDIUM_PLANKS, BlockBehaviour.Properties.of())
        );
        STAIRS_SMALL = createHolder(
            "stairs_small",
            BlockDataUtils.stairs(LOG_SMALL_PLANKS, BlockBehaviour.Properties.of())
        );

        WOOD_LARGE = createHolder(
            "wood_large",
            BlockDataUtils.wood(LOG_LARGE, BlockBehaviour.Properties.of())
        );
        WOOD_MEDIUM = createHolder(
            "wood_medium",
            BlockDataUtils.wood(LOG_MEDIUM, BlockBehaviour.Properties.of())
        );
        WOOD_SMALL = createHolder(
            "wood_small",
            BlockDataUtils.wood(LOG_SMALL, BlockBehaviour.Properties.of())
        );
    }
}
