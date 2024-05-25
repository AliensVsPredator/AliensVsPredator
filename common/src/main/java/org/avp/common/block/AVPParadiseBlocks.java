package org.avp.common.block;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.model.BlockModelData;
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
        dirt = createHolder("dirt", BlockModelData.cube(), BlockTagData.none());
        // TODO:
        dirtMossy = createHolder("dirt_mossy", BlockModelData.cube(), BlockTagData.none());
        // TODO:
        dirtPodzol = createHolder("dirt_podzol", BlockModelData.grass(dirt), BlockTagData.none());
        // TODO:
        grass = createHolder("grass", BlockModelData.grass(dirt), BlockTagData.none());

        leavesLarge = createHolder("leaves_large", BlockModelData.cube(), BlockTagData.none());
        leavesMedium = createHolder("leaves_medium", BlockModelData.cube(), BlockTagData.none());
        leavesSmall = createHolder("leaves_small", BlockModelData.cube(), BlockTagData.none());

        logLarge = createHolder("log_large", BlockModelData.rotatedPillar(), BlockTagData.none());
        var logLargePlanksProperties = BlockBehaviour.Properties.of();
        var logLargePlanksBlockData = new BlockData("log_large_planks", BlockModelData.cube(logLargePlanksProperties), BlockTagData.none());
        logLargePlanks = createHolder(logLargePlanksBlockData);

        logMedium = createHolder("log_medium", BlockModelData.rotatedPillar(), BlockTagData.none());
        var logMediumPlanksProperties = BlockBehaviour.Properties.of();
        var logMediumPlanksBlockData = new BlockData("log_medium_planks", BlockModelData.cube(logMediumPlanksProperties), BlockTagData.none());
        logMediumPlanks = createHolder("log_medium_planks", BlockModelData.cube(logMediumPlanksProperties), BlockTagData.none());

        logSmall = createHolder("log_small", BlockModelData.rotatedPillar(), BlockTagData.none());
        var logSmallPlanksProperties = BlockBehaviour.Properties.of();
        var logSmallPlanksBlockData = new BlockData("log_small_planks", BlockModelData.cube(logSmallPlanksProperties), BlockTagData.none());
        logSmallPlanks = createHolder("log_small_planks", BlockModelData.cube(logSmallPlanksProperties), BlockTagData.none());

        fenceGateLarge = createHolder("fence_gate_large", BlockModelData.fenceGate(logLargePlanks, AVPWoodType.LARGE), BlockTagData.none());
        fenceGateMedium = createHolder("fence_gate_medium", BlockModelData.fenceGate(logMediumPlanks, AVPWoodType.MEDIUM), BlockTagData.none());
        fenceGateSmall = createHolder("fence_gate_small", BlockModelData.fenceGate(logSmallPlanks, AVPWoodType.SMALL), BlockTagData.none());

        fenceLarge = createHolder("fence_large", BlockModelData.fence(logLargePlanks), BlockTagData.none());
        fenceMedium = createHolder("fence_medium", BlockModelData.fence(logMediumPlanks), BlockTagData.none());
        fenceSmall = createHolder("fence_small", BlockModelData.fence(logSmallPlanks), BlockTagData.none());

        slabLarge = createHolder(BlockData.toSlab(logLargePlanks, logLargePlanksProperties, logLargePlanksBlockData));
        slabMedium = createHolder(BlockData.toSlab(logMediumPlanks, logMediumPlanksProperties, logMediumPlanksBlockData));
        slabSmall = createHolder(BlockData.toSlab(logSmallPlanks, logSmallPlanksProperties, logSmallPlanksBlockData));

        stairsLarge = createHolder(BlockData.toStairs(logLargePlanks, logLargePlanksProperties, logLargePlanksBlockData));
        stairsMedium = createHolder(BlockData.toStairs(logMediumPlanks, logMediumPlanksProperties, logMediumPlanksBlockData));
        stairsSmall = createHolder(BlockData.toStairs(logSmallPlanks, logSmallPlanksProperties, logSmallPlanksBlockData));

        woodLarge = createHolder("wood_large", BlockModelData.wood(logLarge), BlockTagData.none());
        woodMedium = createHolder("wood_medium", BlockModelData.wood(logMedium), BlockTagData.none());
        woodSmall = createHolder("wood_small", BlockModelData.wood(logSmall), BlockTagData.none());
    }
}
