package org.avp.common.registry.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.data.block.BlockData;
import org.avp.api.data.block.BlockTagData;
import org.avp.api.data.block.BlockModelData;
import org.avp.api.registry.AVPDeferredBlockRegistry;
import org.avp.common.game.block.AVPWoodType;

public class AVPParadiseBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPParadiseBlockRegistry INSTANCE = new AVPParadiseBlockRegistry();

    public final BLHolder<Block> dirt;

    public final BLHolder<Block> dirtMossy;

    public final BLHolder<Block> dirtPodzol;

    public final BLHolder<Block> fenceGateLarge;

    public final BLHolder<Block> fenceGateMedium;

    public final BLHolder<Block> fenceGateSmall;

    public final BLHolder<Block> fenceLarge;

    public final BLHolder<Block> fenceMedium;

    public final BLHolder<Block> fenceSmall;

    public final BLHolder<Block> grass;

    public final BLHolder<Block> leavesLarge;

    public final BLHolder<Block> leavesMedium;

    public final BLHolder<Block> leavesSmall;

    public final BLHolder<Block> logLarge;

    public final BLHolder<Block> logLargePlanks;

    public final BLHolder<Block> logMedium;

    public final BLHolder<Block> logMediumPlanks;

    public final BLHolder<Block> logSmall;

    public final BLHolder<Block> logSmallPlanks;

    public final BLHolder<Block> slabLarge;

    public final BLHolder<Block> slabMedium;

    public final BLHolder<Block> slabSmall;

    public final BLHolder<Block> stairsLarge;

    public final BLHolder<Block> stairsMedium;

    public final BLHolder<Block> stairsSmall;

    public final BLHolder<Block> woodLarge;

    public final BLHolder<Block> woodMedium;

    public final BLHolder<Block> woodSmall;

    @Override
    protected BLHolder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("paradise_"));
    }

    private AVPParadiseBlockRegistry() {
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
        var logMediumPlanksBlockData = new BlockData(
            "log_medium_planks",
            BlockModelData.cube(logMediumPlanksProperties),
            BlockTagData.none()
        );
        logMediumPlanks = createHolder("log_medium_planks", BlockModelData.cube(logMediumPlanksProperties), BlockTagData.none());

        logSmall = createHolder("log_small", BlockModelData.rotatedPillar(), BlockTagData.none());
        var logSmallPlanksProperties = BlockBehaviour.Properties.of();
        var logSmallPlanksBlockData = new BlockData("log_small_planks", BlockModelData.cube(logSmallPlanksProperties), BlockTagData.none());
        logSmallPlanks = createHolder("log_small_planks", BlockModelData.cube(logSmallPlanksProperties), BlockTagData.none());

        fenceGateLarge = createHolder("fence_gate_large", BlockModelData.fenceGate(logLargePlanks, AVPWoodType.LARGE), BlockTagData.none());
        fenceGateMedium = createHolder(
            "fence_gate_medium",
            BlockModelData.fenceGate(logMediumPlanks, AVPWoodType.MEDIUM),
            BlockTagData.none()
        );
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
