package org.avp.common.block;

import net.minecraft.world.level.block.Block;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockModelData;
import org.avp.api.block.BlockTagData;
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
    protected Holder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("paradise_"));
    }

    private AVPParadiseBlocks() {
        dirt = createHolder(new BlockData("dirt", BlockModelData.cube(), BlockTagData.none()));
        // TODO:
        dirtMossy = createHolder(new BlockData("dirt_mossy", BlockModelData.cube(), BlockTagData.none()));
        // TODO:
        dirtPodzol = createHolder(new BlockData("dirt_podzol", BlockModelData.cube(), BlockTagData.none()));
        // TODO:
        grass = createHolder(new BlockData("dirt_grass", BlockModelData.cube(), BlockTagData.none()));

        leavesLarge = createHolder(new BlockData("leaves_large", BlockModelData.cube(), BlockTagData.none()));
        leavesMedium = createHolder(new BlockData("leaves_medium", BlockModelData.cube(), BlockTagData.none()));
        leavesSmall = createHolder(new BlockData("leaves_small", BlockModelData.cube(), BlockTagData.none()));

        logLarge = createHolder(new BlockData("log_large", BlockModelData.rotatedPillar(), BlockTagData.none()));
        logLargePlanks = createHolder(new BlockData("log_large_planks", BlockModelData.cube(), BlockTagData.none()));

        logMedium = createHolder(new BlockData("log_medium", BlockModelData.rotatedPillar(), BlockTagData.none()));
        logMediumPlanks = createHolder(new BlockData("log_medium_planks", BlockModelData.cube(), BlockTagData.none()));

        logSmall = createHolder(new BlockData("log_small", BlockModelData.rotatedPillar(), BlockTagData.none()));
        logSmallPlanks = createHolder(new BlockData("log_small_planks", BlockModelData.cube(), BlockTagData.none()));

        fenceGateLarge = createHolder(new BlockData("fence_gate_large", BlockModelData.fenceGate(logLargePlanks, AVPWoodType.LARGE), BlockTagData.none()));
        fenceGateMedium = createHolder(new BlockData("fence_gate_medium", BlockModelData.fenceGate(logMediumPlanks, AVPWoodType.MEDIUM), BlockTagData.none()));
        fenceGateSmall = createHolder(new BlockData("fence_gate_small", BlockModelData.fenceGate(logSmallPlanks, AVPWoodType.SMALL), BlockTagData.none()));

        fenceLarge = createHolder(new BlockData("fence_large", BlockModelData.fence(logLargePlanks), BlockTagData.none()));
        fenceMedium = createHolder(new BlockData("fence_medium", BlockModelData.fence(logMediumPlanks), BlockTagData.none()));
        fenceSmall = createHolder(new BlockData("fence_small", BlockModelData.fence(logSmallPlanks), BlockTagData.none()));

        slabLarge = createHolder(new BlockData("slab_large", BlockModelData.slab(logLargePlanks), BlockTagData.none()));
        slabMedium = createHolder(new BlockData("slab_medium", BlockModelData.slab(logMediumPlanks), BlockTagData.none()));
        slabSmall = createHolder(new BlockData("slab_small", BlockModelData.slab(logSmallPlanks), BlockTagData.none()));

        stairsLarge = createHolder(new BlockData("stairs_large", BlockModelData.stairs(logLargePlanks), BlockTagData.none()));
        stairsMedium = createHolder(new BlockData("stairs_medium", BlockModelData.stairs(logMediumPlanks), BlockTagData.none()));
        stairsSmall = createHolder(new BlockData("stairs_small", BlockModelData.stairs(logSmallPlanks), BlockTagData.none()));

        woodLarge = createHolder(new BlockData("wood_large", BlockModelData.wood(logLarge), BlockTagData.none()));
        woodMedium = createHolder(new BlockData("wood_medium", BlockModelData.wood(logMedium), BlockTagData.none()));
        woodSmall = createHolder(new BlockData("wood_small", BlockModelData.wood(logSmall), BlockTagData.none()));
    }
}
