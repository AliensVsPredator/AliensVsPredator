package org.avp.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.GameObject;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;

public class AVPParadiseBlocks {

    public static final GameObject<Block> DIRT;

    public static final GameObject<Block> DIRT_MOSSY;

    public static final GameObject<Block> DIRT_PODZOL;

    public static final GameObject<Block> FENCE_GATE_LARGE;

    public static final GameObject<Block> FENCE_GATE_MEDIUM;

    public static final GameObject<Block> FENCE_GATE_SMALL;

    public static final GameObject<Block> FENCE_LARGE;

    public static final GameObject<Block> FENCE_MEDIUM;

    public static final GameObject<Block> FENCE_SMALL;

    public static final GameObject<Block> GRASS;

    public static final GameObject<Block> LEAVES_LARGE;

    public static final GameObject<Block> LEAVES_MEDIUM;

    public static final GameObject<Block> LEAVES_SMALL;

    public static final GameObject<Block> LOG_LARGE;

    public static final GameObject<Block> LOG_LARGE_PLANKS;

    public static final GameObject<Block> LOG_MEDIUM;

    public static final GameObject<Block> LOG_MEDIUM_PLANKS;

    public static final GameObject<Block> LOG_SMALL;

    public static final GameObject<Block> LOG_SMALL_PLANKS;

    public static final GameObject<Block> SLAB_LARGE;

    public static final GameObject<Block> SLAB_MEDIUM;

    public static final GameObject<Block> SLAB_SMALL;

    public static final GameObject<Block> STAIRS_LARGE;

    public static final GameObject<Block> STAIRS_MEDIUM;

    public static final GameObject<Block> STAIRS_SMALL;

    public static final GameObject<Block> WOOD_LARGE;

    public static final GameObject<Block> WOOD_MEDIUM;

    public static final GameObject<Block> WOOD_SMALL;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register("paradise_" + name, builder, false);
    }

    private AVPParadiseBlocks() {}

    static {
        DIRT = register("dirt", BlockData.simple(BlockBehaviour.Properties.of()));
        DIRT_MOSSY = register(
            "dirt_mossy",
            BlockData.simple(BlockBehaviour.Properties.of())
        );
        DIRT_PODZOL = register(
            "dirt_podzol",
            BlockDataUtils.grass(DIRT, BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL))
        );
        GRASS = register(
            "grass",
            BlockDataUtils.grass(DIRT, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK))
        );

        LEAVES_LARGE = register("leaves_large", BlockData.simple(BlockBehaviour.Properties.of()));
        LEAVES_MEDIUM = register("leaves_medium", BlockData.simple(BlockBehaviour.Properties.of()));
        LEAVES_SMALL = register("leaves_small", BlockData.simple(BlockBehaviour.Properties.of()));

        LOG_LARGE = register("log_large", BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of()));
        LOG_LARGE_PLANKS = register("log_large_planks", BlockData.simple(BlockBehaviour.Properties.of()));

        LOG_MEDIUM = register("log_medium", BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of()));
        LOG_MEDIUM_PLANKS = register(
            "log_medium_planks",
            BlockData.simple(BlockBehaviour.Properties.of())
        );

        LOG_SMALL = register(
            "log_small",
            BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of())
        );
        LOG_SMALL_PLANKS = register(
            "log_small_planks",
            BlockData.simple(BlockBehaviour.Properties.of())
        );

        FENCE_GATE_LARGE = register(
            "fence_gate_large",
            BlockDataUtils.fenceGate(LOG_LARGE_PLANKS, AVPWoodType.LARGE, BlockBehaviour.Properties.of())
        );
        FENCE_GATE_MEDIUM = register(
            "fence_gate_medium",
            BlockDataUtils.fenceGate(LOG_MEDIUM_PLANKS, AVPWoodType.MEDIUM, BlockBehaviour.Properties.of())
        );
        FENCE_GATE_SMALL = register(
            "fence_gate_small",
            BlockDataUtils.fenceGate(LOG_SMALL_PLANKS, AVPWoodType.SMALL, BlockBehaviour.Properties.of())
        );

        FENCE_LARGE = register(
            "fence_large",
            BlockDataUtils.fence(LOG_LARGE_PLANKS, BlockBehaviour.Properties.of())
        );
        FENCE_MEDIUM = register(
            "fence_medium",
            BlockDataUtils.fence(LOG_MEDIUM_PLANKS, BlockBehaviour.Properties.of())
        );
        FENCE_SMALL = register(
            "fence_small",
            BlockDataUtils.fence(LOG_SMALL_PLANKS, BlockBehaviour.Properties.of())
        );

        SLAB_LARGE = register(
            "slab_large",
            BlockDataUtils.slab(LOG_LARGE_PLANKS, BlockBehaviour.Properties.of())
        );
        SLAB_MEDIUM = register(
            "slab_medium",
            BlockDataUtils.slab(LOG_MEDIUM_PLANKS, BlockBehaviour.Properties.of())
        );
        SLAB_SMALL = register(
            "slab_small",
            BlockDataUtils.slab(LOG_SMALL_PLANKS, BlockBehaviour.Properties.of())
        );

        STAIRS_LARGE = register(
            "stairs_large",
            BlockDataUtils.stairs(LOG_LARGE_PLANKS, BlockBehaviour.Properties.of())
        );
        STAIRS_MEDIUM = register(
            "stairs_medium",
            BlockDataUtils.stairs(LOG_MEDIUM_PLANKS, BlockBehaviour.Properties.of())
        );
        STAIRS_SMALL = register(
            "stairs_small",
            BlockDataUtils.stairs(LOG_SMALL_PLANKS, BlockBehaviour.Properties.of())
        );

        WOOD_LARGE = register(
            "wood_large",
            BlockDataUtils.wood(LOG_LARGE, BlockBehaviour.Properties.of())
        );
        WOOD_MEDIUM = register(
            "wood_medium",
            BlockDataUtils.wood(LOG_MEDIUM, BlockBehaviour.Properties.of())
        );
        WOOD_SMALL = register(
            "wood_small",
            BlockDataUtils.wood(LOG_SMALL, BlockBehaviour.Properties.of())
        );
    }
}
