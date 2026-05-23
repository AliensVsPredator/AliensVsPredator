package com.blib.api.common.pathfinding.v1.breaking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.blib.api.common.block.v1.BlockBreakProgressManager;
import com.blib.api.common.pathfinding.v1.debug.PathDebugBlockPos;
import com.blib.api.common.pathfinding.v1.evaluator.PathBlockBreakingConfig;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorApi;
import com.blib.api.common.pathfinding.v1.node.PathBlockBreakPlan;

/**
 * Server-side executor for path nodes that require block-breaking before movement into the node.
 */
public final class PathBlockBreakExecutor {

    private static final Map<UUID, DebugState> DEBUG_STATES = new HashMap<>();

    private @Nullable BlockPos activeBlockPos;

    private @Nullable BlockState activeBlockState;

    public Result tick(PathfinderMob actor, PathNavigatorApi navigator) {
        var level = actor.level();

        if (level.isClientSide()) {
            // Block breaking is server-side only.
            publishDebugState(actor, Result.IDLE);
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
            publishDebugState(actor, Result.IDLE);
            return Result.IDLE;
        }

        var plan = node.getBlockBreakPlan();
        var breakConfig = navigator.getRuntimeConfig().getConfig().getEvaluatorConfig().getBlockBreakingConfig();

        if (!breakConfig.enabled() || breakConfig.damagePerTick() <= 0.0f) {
            reset(level);
            publishDebugState(actor, Result.INVALIDATED);
            return Result.INVALIDATED;
        }

        if (activeBlockPos != null && !plan.blocks().contains(activeBlockPos)) {
            reset(level);
        }

        if (activeBlockPos != null && activeBlockState != null) {
            var currentState = level.getBlockState(activeBlockPos);

            if (!currentState.equals(activeBlockState)) {
                reset(level);
                publishDebugState(actor, Result.INVALIDATED);
                return Result.INVALIDATED;
            }
        }

        var blockPos = firstBlockingBlock(level, plan);

        if (blockPos == null) {
            reset(level);
            publishDebugState(actor, Result.IDLE);
            return Result.IDLE;
        }

        var blockState = level.getBlockState(blockPos);

        if (!breakConfig.canBreak(level, blockPos, blockState)) {
            reset(level);
            publishDebugState(actor, Result.INVALIDATED);
            return Result.INVALIDATED;
        }

        this.activeBlockPos = blockPos.immutable();
        this.activeBlockState = blockState;

        holdAtBlock(actor, blockPos);
        navigator.markPathfindingFeatureUsed(PathfindingFeature.BLOCK_BREAKING);
        navigator.markPathProgress();

        var result = BlockBreakProgressManager.damage(level, blockPos, breakConfig.damagePerTick());

        switch (result) {
            case DAMAGED -> publishDebugState(actor, Result.BREAKING, blockPos, plan);
            case DESTROYED -> {
                clearActiveBlock();

                var nextBlockPos = firstBlockingBlock(level, plan);
                if (nextBlockPos == null) {
                    publishDebugState(actor, Result.IDLE);
                    return Result.IDLE;
                }
                publishDebugState(actor, Result.BREAKING, nextBlockPos, plan);
            }
            case NOT_DAMAGED -> {
                reset(level);
                publishDebugState(actor, Result.INVALIDATED);
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

    public void reset(PathfinderMob actor) {
        reset(actor.level());
        publishDebugState(actor, Result.IDLE);
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

    public static DebugState debugStateFor(Entity entity) {
        return DEBUG_STATES.getOrDefault(entity.getUUID(), DebugState.IDLE);
    }

    private static void publishDebugState(PathfinderMob actor, Result result) {
        DEBUG_STATES.put(actor.getUUID(), new DebugState(result, PathDebugBlockPos.NONE, 0.0f, false));
    }

    private static void publishDebugState(
        PathfinderMob actor,
        Result result,
        BlockPos activeBlock,
        PathBlockBreakPlan plan
    ) {
        DEBUG_STATES.put(
            actor.getUUID(),
            new DebugState(
                result,
                PathDebugBlockPos.of(activeBlock),
                BlockBreakProgressManager.getProgress(activeBlock),
                plan.blocks().contains(activeBlock)
            )
        );
    }

    public enum Result {
        IDLE,
        BREAKING,
        INVALIDATED
    }

    public record DebugState(
        Result result,
        PathDebugBlockPos activeBlock,
        float progress,
        boolean activeBlockInPlan
    ) {

        private static final DebugState IDLE = new DebugState(Result.IDLE, PathDebugBlockPos.NONE, 0.0f, false);
    }
}
