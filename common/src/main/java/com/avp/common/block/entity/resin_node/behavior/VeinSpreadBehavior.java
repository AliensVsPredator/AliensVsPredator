package com.avp.common.block.entity.resin_node.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import com.avp.common.block.entity.resin_node.ChargeCursor;
import com.avp.common.block.entity.resin_node.ResinSpreader;
import com.avp.common.entity.living.alien.AlienVariantTypes;

public interface VeinSpreadBehavior {

    int attemptUseCharge(
        ChargeCursor chargeCursor,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        RandomSource randomSource,
        ResinSpreader resinSpreader
    );

    default byte getResinSpreadDelayInTicks() {
        return 1;
    }

    default boolean attemptSpreadVein(
        BlockPos nodePos,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        BlockState blockState,
        @Nullable Collection<Direction> facings
    ) {
        var block = levelAccessor.getBlockState(nodePos).getBlock();

        return AlienVariantTypes.getFor(block)
            .isSomeAnd(alienVariantType -> {
                var resinVeinBlock = alienVariantType.resinVein().get();
                var numberOfDirectionsSpreadTowards = resinVeinBlock.getSpreader()
                    .spreadAll(blockState, levelAccessor, blockPos, false);

                return numberOfDirectionsSpreadTowards > 0L;
            });
    }

    default boolean canChangeBlockStateOnSpread() {
        return true;
    }

    default int updateDecayDelay(int decayDelay) {
        return 1;
    }
}
