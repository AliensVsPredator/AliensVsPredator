package com.avp.common.block.entity.resin_node.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import com.avp.common.block.entity.resin_node.ChargeCursor;
import com.avp.common.block.entity.resin_node.ResinSpreader;
import com.avp.common.block.resin.ResinVeinRegrowUtil;
import com.avp.common.entity.living.alien.AlienVariantType;
import com.avp.common.entity.living.alien.AlienVariantTypes;
import com.avp.common.registry.AVPDeferredHolder;

public class ResinVeinSpreadBehavior implements VeinSpreadBehavior {

    public static final ResinVeinSpreadBehavior INSTANCE = new ResinVeinSpreadBehavior();

    private ResinVeinSpreadBehavior() {}

    @Override
    public boolean attemptSpreadVein(
        BlockPos nodePos,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        BlockState blockState,
        @Nullable Collection<Direction> facings
    ) {
        var nodeBlock = levelAccessor.getBlockState(nodePos).getBlock();
        var resinVeinBlock = AlienVariantTypes.getFor(nodeBlock)
            .map(AlienVariantType::resinVein)
            .map(AVPDeferredHolder::get)
            .unwrapOr(null);

        if (resinVeinBlock == null) {
            return false;
        }

        if (facings == null) {
            var spreader = resinVeinBlock.getSameSpaceSpreader();
            return spreader.spreadAll(levelAccessor.getBlockState(blockPos), levelAccessor, blockPos, false) > 0L;
        } else if (!facings.isEmpty()) {
            return isAirOrWater(blockState) && ResinVeinRegrowUtil.regrow(
                resinVeinBlock.defaultBlockState(),
                levelAccessor,
                blockPos,
                blockState,
                facings
            );
        } else {
            return VeinSpreadBehavior.super.attemptSpreadVein(nodePos, levelAccessor, blockPos, blockState, facings);
        }
    }

    @Override
    public int attemptUseCharge(
        ChargeCursor chargeCursor,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        RandomSource randomSource,
        ResinSpreader resinSpreader
    ) {
        return chargeCursor.getDecayDelay() > 0
            ? chargeCursor.getCharge()
            : 0;
    }

    @Override
    public int updateDecayDelay(int decayDelay) {
        return Math.max(decayDelay - 1, 0);
    }

    private boolean isAirOrWater(BlockState blockState) {
        return blockState.isAir() || blockState.getFluidState().is(Fluids.WATER);
    }
}
