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

    public final Holder<Block> dirt;

    public final Holder<Block> dirtMossy;

    public final Holder<Block> dirtPodzol;

    public final Holder<Block> fenceGateLarge;

    public final Holder<Block> fenceGateMedium;

    public final Holder<Block> fenceGateSmall;

    public final Holder<Block> fenceLarge;

    public final Holder<Block> fenceMedium;

    public final Holder<Block> fenceSmall;

    public final Holder<Block> grass;

    public final Holder<Block> leavesLarge;

    public final Holder<Block> leavesMedium;

    public final Holder<Block> leavesSmall;

    public final Holder<Block> logLarge;

    public final Holder<Block> logLargePlanks;

    public final Holder<Block> logMedium;

    public final Holder<Block> logMediumPlanks;

    public final Holder<Block> logSmall;

    public final Holder<Block> logSmallPlanks;

    public final Holder<Block> slabLarge;

    public final Holder<Block> slabMedium;

    public final Holder<Block> slabSmall;

    public final Holder<Block> stairsLarge;

    public final Holder<Block> stairsMedium;

    public final Holder<Block> stairsSmall;

    public final Holder<Block> woodLarge;

    public final Holder<Block> woodMedium;

    public final Holder<Block> woodSmall;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("paradise_" + registryName, blockDataBuilder);
    }

    private AVPParadiseBlocks() {
        dirt = createHolder("dirt", BlockData.simple(BlockBehaviour.Properties.of()));
        dirtMossy = createHolder(
            "dirt_mossy",
            BlockData.simple(BlockBehaviour.Properties.of())
        );
        dirtPodzol = createHolder(
            "dirt_podzol",
            BlockDataUtils.grass(dirt, BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL))
        );
        grass = createHolder(
            "grass",
            BlockDataUtils.grass(dirt, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK))
        );

        leavesLarge = createHolder("leaves_large", BlockData.simple(BlockBehaviour.Properties.of()));
        leavesMedium = createHolder("leaves_medium", BlockData.simple(BlockBehaviour.Properties.of()));
        leavesSmall = createHolder("leaves_small", BlockData.simple(BlockBehaviour.Properties.of()));

        logLarge = createHolder("log_large", BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of()));
        logLargePlanks = createHolder("log_large_planks", BlockData.simple(BlockBehaviour.Properties.of()));

        logMedium = createHolder("log_medium", BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of()));
        logMediumPlanks = createHolder(
            "log_medium_planks",
            BlockData.simple(BlockBehaviour.Properties.of())
        );

        logSmall = createHolder(
            "log_small",
            BlockDataUtils.rotatedPillar(BlockBehaviour.Properties.of())
        );
        logSmallPlanks = createHolder(
            "log_small_planks",
            BlockData.simple(BlockBehaviour.Properties.of())
        );

        fenceGateLarge = createHolder(
            "fence_gate_large",
            BlockDataUtils.fenceGate(logLargePlanks, AVPWoodType.LARGE, BlockBehaviour.Properties.of())
        );
        fenceGateMedium = createHolder(
            "fence_gate_medium",
            BlockDataUtils.fenceGate(logMediumPlanks, AVPWoodType.MEDIUM, BlockBehaviour.Properties.of())
        );
        fenceGateSmall = createHolder(
            "fence_gate_small",
            BlockDataUtils.fenceGate(logSmallPlanks, AVPWoodType.SMALL, BlockBehaviour.Properties.of())
        );

        fenceLarge = createHolder(
            "fence_large",
            BlockDataUtils.fence(logLargePlanks, BlockBehaviour.Properties.of())
        );
        fenceMedium = createHolder(
            "fence_medium",
            BlockDataUtils.fence(logMediumPlanks, BlockBehaviour.Properties.of())
        );
        fenceSmall = createHolder(
            "fence_small",
            BlockDataUtils.fence(logSmallPlanks, BlockBehaviour.Properties.of())
        );

        slabLarge = createHolder(
            "slab_large",
            BlockDataUtils.slab(logLargePlanks, BlockBehaviour.Properties.of())
        );
        slabMedium = createHolder(
            "slab_medium",
            BlockDataUtils.slab(logMediumPlanks, BlockBehaviour.Properties.of())
        );
        slabSmall = createHolder(
            "slab_small",
            BlockDataUtils.slab(logSmallPlanks, BlockBehaviour.Properties.of())
        );

        stairsLarge = createHolder(
            "stairs_large",
            BlockDataUtils.stairs(logLargePlanks, BlockBehaviour.Properties.of())
        );
        stairsMedium = createHolder(
            "stairs_medium",
            BlockDataUtils.stairs(logMediumPlanks, BlockBehaviour.Properties.of())
        );
        stairsSmall = createHolder(
            "stairs_small",
            BlockDataUtils.stairs(logSmallPlanks, BlockBehaviour.Properties.of())
        );

        woodLarge = createHolder(
            "wood_large",
            BlockDataUtils.wood(logLarge, BlockBehaviour.Properties.of())
        );
        woodMedium = createHolder(
            "wood_medium",
            BlockDataUtils.wood(logMedium, BlockBehaviour.Properties.of())
        );
        woodSmall = createHolder(
            "wood_small",
            BlockDataUtils.wood(logSmall, BlockBehaviour.Properties.of())
        );
    }
}
