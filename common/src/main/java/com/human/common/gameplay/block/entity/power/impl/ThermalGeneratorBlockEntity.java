package com.human.common.gameplay.block.entity.power.impl;

import com.human.common.gameplay.block.IndustrialFurnaceBlock;
import com.human.common.gameplay.block.entity.power.PowerNodeBlockEntity;
import com.human.common.gameplay.power.PowerNode;
import com.just.core.functional.function.Function;
import com.just.core.functional.function.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

import com.avp.common.registry.init.AVPBlockEntityTypes;
import com.avp.common.registry.init.block.AVPBlocks;

public class ThermalGeneratorBlockEntity extends PowerNodeBlockEntity implements PowerNode.PowerProducer {

    private static final Lazy<Map<Block, Function<BlockState, Integer>>> HEAT_VALUES = Lazy.of(
        () -> Map.ofEntries(
            Map.entry(Blocks.LAVA, $ -> 400),
            Map.entry(Blocks.LAVA_CAULDRON, $ -> 300),
            Map.entry(Blocks.FIRE, $ -> 160),
            Map.entry(Blocks.SOUL_FIRE, $ -> 120),
            Map.entry(Blocks.CAMPFIRE, $ -> 100),
            Map.entry(Blocks.MAGMA_BLOCK, $ -> 80),
            Map.entry(Blocks.SOUL_CAMPFIRE, $ -> 60),
            Map.entry(Blocks.LANTERN, $ -> 30),
            Map.entry(Blocks.TORCH, $ -> 20),
            Map.entry(Blocks.WALL_TORCH, $ -> 20),
            Map.entry(Blocks.REDSTONE_TORCH, $ -> 10),
            Map.entry(Blocks.REDSTONE_WALL_TORCH, $ -> 10),

            // Lit-dependent blocks.
            Map.entry(Blocks.BLAST_FURNACE, state -> state.getValue(BlastFurnaceBlock.LIT) ? 200 : 0),
            Map.entry(Blocks.FURNACE, state -> state.getValue(FurnaceBlock.LIT) ? 180 : 0),
            Map.entry(Blocks.SMOKER, state -> state.getValue(SmokerBlock.LIT) ? 170 : 0),

            // AVP Lit-dependent blocks
            Map.entry(AVPBlocks.INDUSTRIAL_FURNACE.get(), state -> state.getValue(IndustrialFurnaceBlock.LIT) ? 190 : 0)
        )
    );

    public ThermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AVPBlockEntityTypes.THERMAL_GENERATOR.get(), pos, state);
    }

    @Override
    public long getAvailablePower() {
        if (level == null || level.isClientSide) {
            return 0;
        }

        var totalHeat = 0;

        for (var direction : Direction.values()) {
            var neighborPos = worldPosition.relative(direction);
            var neighborState = level.getBlockState(neighborPos);
            var neighborBlock = neighborState.getBlock();
            var heatFn = HEAT_VALUES.get().get(neighborBlock);

            if (heatFn != null) {
                totalHeat += heatFn.apply(neighborState);
            }
        }

        return totalHeat;
    }

    @Override
    public long extractPower(long maxAmount) {
        // Always gives exactly what was requested.
        return maxAmount;
    }
}
