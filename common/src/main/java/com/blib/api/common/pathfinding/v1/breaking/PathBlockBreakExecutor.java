package com.blib.api.common.pathfinding.v1.breaking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.block.v1.BlockBreakProgressManager;
import com.blib.api.common.pathfinding.v1.evaluator.PathBlockBreakingConfig;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorApi;
import com.blib.api.common.pathfinding.v1.node.PathBlockBreakPlan;

/**
 * Server-side executor for path nodes that require block-breaking before movement into the node.
 */
public final class PathBlockBreakExecutor {

    private @Nullable BlockPos activeBlockPos;

    private @Nullable BlockState activeBlockState;

    public Result tick(PathfinderMob actor, PathNavigatorApi navigator) {
        var level = actor.level();

        if (level.isClientSide()) {
            // Block breaking is server-side only.
            return Result.IDLE;
        }

        var node = navigator.getState().getCurrentNode();

        var featureControl = navigator.getFeatureControl();

        if (
            node == null
                || !node.requiresBlockBreaking()
                || !featureControl.getPathfindingFeatures().blockBreaking()
        ) {
            reset(level);
            return Result.IDLE;
        }

        var plan = node.getBlockBreakPlan();
        var breakConfig = navigator.getRuntimeConfig().getConfig().getEvaluatorConfig().getBlockBreakingConfig();

        if (!breakConfig.enabled() || breakConfig.damagePerTick() <= 0.0f) {
            reset(level);
            return Result.INVALIDATED;
        }

        if (activeBlockPos != null && !plan.blocks().contains(activeBlockPos)) {
            reset(level);
        }

        if (activeBlockPos != null && activeBlockState != null) {
            var currentState = level.getBlockState(activeBlockPos);

            if (!currentState.equals(activeBlockState)) {
                reset(level);
                return Result.INVALIDATED;
            }
        }

        var blockPos = firstBlockingBlock(level, plan);

        if (blockPos == null) {
            reset(level);
            return Result.IDLE;
        }

        var blockState = level.getBlockState(blockPos);

        if (!breakConfig.canBreak(level, blockPos, blockState)) {
            reset(level);
            return Result.INVALIDATED;
        }

        this.activeBlockPos = blockPos.immutable();
        this.activeBlockState = blockState;

        holdAtBlock(actor, blockPos);
        navigator.markPathfindingFeatureUsed(PathfindingFeature.BLOCK_BREAKING);
        navigator.markPathProgress();

        var result = BlockBreakProgressManager.damage(level, blockPos, breakConfig.damagePerTick());

        switch (result) {
            case DAMAGED -> {/* NO-OP */}
            case DESTROYED -> {
                clearActiveBlock();

                if (firstBlockingBlock(level, plan) == null) {
                    return Result.IDLE;
                }
            }
            case NOT_DAMAGED -> {
                reset(level);
                return Result.INVALIDATED;
            }
        }

        return Result.BREAKING;
    }

    public void reset(Level level) {
        if (activeBlockPos != null) {
            BlockBreakProgressManager.resetProgress(level, activeBlockPos);
        }

        clearActiveBlock();
    }

    private @Nullable BlockPos firstBlockingBlock(Level level, PathBlockBreakPlan plan) {
        for (var block : plan.blocks()) {
            if (requiresBreaking(level, block)) {
                return block;
            }
        }

        return null;
    }

    private boolean requiresBreaking(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);

        if (state.isAir()) {
            return false;
        }

        return !state.getFluidState().isEmpty()
            || !state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty();
    }

    private void holdAtBlock(PathfinderMob actor, BlockPos pos) {
        actor.getLookControl().setLookAt(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
        actor.getMoveControl().setWantedPosition(actor.getX(), actor.getY(), actor.getZ(), 0.0d);
    }

    private void clearActiveBlock() {
        activeBlockPos = null;
        activeBlockState = null;
    }

    public enum Result {
        IDLE,
        BREAKING,
        INVALIDATED
    }
}
