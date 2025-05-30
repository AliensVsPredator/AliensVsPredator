package com.alien.common.gameplay.block.entity.resin.node.behavior;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.block.entity.resin.node.ChargeCursor;
import com.alien.common.gameplay.block.entity.resin.node.ResinSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface VeinSpreadBehavior {

    int attemptUseCharge(
        ChargeCursor chargeCursor,
        LevelAccessor levelAccessor,
        BlockPos nodePos,
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
