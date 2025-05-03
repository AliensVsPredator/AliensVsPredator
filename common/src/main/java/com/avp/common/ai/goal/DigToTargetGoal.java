package com.avp.common.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.block.AVPBlocks;
import com.avp.server.BlockBreakProgressManager;

public class DigToTargetGoal extends Goal {

    private static final float BREAKING_SPEED = 50F;

    private final Mob mob;

    private final double reachDistance;

    private final double maxDistanceFromTarget;

    private final List<BlockPos> targetBlockPositions = new ArrayList<>();

    private final int parallelBlockBreakCount;

    private BlockState blockState = null;

    private Vec3 lastPosition = null;

    private int lastProgressTick = 0;

    private double lastDistanceToTarget = Double.MAX_VALUE;

    public DigToTargetGoal(Mob mob) {
        this(mob, 16);
    }

    public DigToTargetGoal(Mob mob, double maxDistanceFromTarget) {
        this(mob, maxDistanceFromTarget, 1);
    }

    public DigToTargetGoal(Mob mob, double maxDistanceFromTarget, int parallelBlockBreakCount) {
        this.mob = mob;
        this.reachDistance = 4;
        this.maxDistanceFromTarget = maxDistanceFromTarget * maxDistanceFromTarget;
        this.parallelBlockBreakCount = parallelBlockBreakCount;
    }

    @Override
    public boolean canUse() {
        var target = mob.getTarget();

        if (target == null || !mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }

        if (mob.getHealth() < (mob.getMaxHealth() / 2)) {
            return false;
        }

        return mob.onGround()
            && isStuck(target)
            && (mob.distanceToSqr(target) > 2d || !mob.getSensing().hasLineOfSight(target))
            && mob.distanceToSqr(target) < maxDistanceFromTarget;
    }

    @Override
    public boolean canContinueToUse() {
        var target = mob.getTarget();

        if (target == null || !target.isAlive() || targetBlockPositions.isEmpty()) {
            return false;
        }

        if (mob.getHealth() < (mob.getMaxHealth() / 2)) {
            return false;
        }

        return mob.onGround()
            && !mob.level().getBlockState(targetBlockPositions.getFirst()).isAir()
            && targetBlockPositions.getFirst().distSqr(mob.blockPosition()) < reachDistance * reachDistance;
    }

    @Override
    public void start() {
        var target = mob.getTarget();

        if (target == null) {
            return;
        }

        gatherTargetBlocks(target);

        if (!targetBlockPositions.isEmpty()) {
            initBlockBreak();
            mob.setAggressive(true);
        }
    }

    @Override
    public void stop() {
        if (!targetBlockPositions.isEmpty()) {
            targetBlockPositions.clear();
        }

        this.blockState = null;
        this.lastPosition = null;
        this.mob.setAggressive(false);
    }

    @Override
    public void tick() {
        var target = mob.getTarget();

        if (target == null || targetBlockPositions.isEmpty()) {
            return;
        }

        if (mob.tickCount % 4 == 0) {
            for (int i = 0; i < targetBlockPositions.size() && i < parallelBlockBreakCount; i++) {
                breakBlockAtPosition(targetBlockPositions.get(i), target);
            }
        }
    }

    private void breakBlockAtPosition(BlockPos pos, LivingEntity target) {
        mob.getLookControl().setLookAt(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);

        BlockBreakProgressManager.damage(mob.level(), pos, BREAKING_SPEED);

        var soundType = blockState.getSoundType();

        mob.level()
            .playSound(
                null,
                pos,
                soundType.getHitSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 8.0F,
                soundType.getPitch() * 0.5F
            );

        if (mob.level().getBlockState(pos).is(Blocks.AIR)) {
            targetBlockPositions.removeFirst();

            if (!targetBlockPositions.isEmpty()) {
                initBlockBreak();
            } else if (mob.distanceToSqr(target) > 2d && !mob.getSensing().hasLineOfSight(target)) {
                start();
            }
        }
    }

    private void initBlockBreak() {
        this.blockState = mob.level().getBlockState(targetBlockPositions.getFirst());
    }

    private void gatherTargetBlocks(@NotNull LivingEntity target) {
        int mobWidth = Mth.ceil(mob.getBbWidth());
        int mobHeight = Mth.ceil(mob.getBbHeight());

        for (var i = 0; i < mobHeight; i++) {
            for (var j = -mobWidth / 2; j <= mobWidth / 2; j++) { // Loop through the width
                for (var k = -mobWidth / 2; k <= mobWidth / 2; k++) {
                    var from = mob.position().add(j, i + 0.5d, k);
                    var to = target.getEyePosition(1f).add(j, i, k);
                    var clipContext = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob);
                    var rayTraceResult = mob.level().clip(clipContext);

                    if (
                        rayTraceResult.getType() == HitResult.Type.MISS
                            || targetBlockPositions.contains(rayTraceResult.getBlockPos())
                            || rayTraceResult.getBlockPos().getY() > 320
                    ) { // TODO: The max y level mobs can mine up to
                        continue;
                    }

                    double distance = mob.distanceToSqr(rayTraceResult.getLocation());

                    if (distance > reachDistance * reachDistance) {
                        continue;
                    }

                    BlockState state = mob.level().getBlockState(rayTraceResult.getBlockPos());

                    if (
                        state.hasBlockEntity()
                            || state.getDestroySpeed(mob.level(), rayTraceResult.getBlockPos()) == -1
                            // TODO: Make this configurable
                            || state.getBlock().defaultDestroyTime() >= AVPBlocks.STEEL_BLOCK.get().defaultDestroyTime()
                            // TODO: Make this configurable
                            || state.is(AVPBlockTags.XENOMORPH_IMMUNE)
                    ) {
                        continue;
                    }

                    // TODO: Blacklist blocks here

                    // TODO: Check if block is below walkable path and exclude.

                    targetBlockPositions.add(rayTraceResult.getBlockPos());
                }
            }
        }

        Collections.reverse(targetBlockPositions);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean isStuck(@NotNull LivingEntity target) {
        double currentDistanceToTarget = mob.distanceToSqr(target);

        // If very close, no need to consider stuck.
        if (currentDistanceToTarget <= mob.getBbWidth() * mob.getBbWidth()) {
            resetProgress();
            return false;
        }

        if (lastPosition == null) {
            // First call: record initial info.
            this.lastPosition = mob.position();
            this.lastProgressTick = mob.tickCount;
            this.lastDistanceToTarget = currentDistanceToTarget;
            return false;
        }

        // If mob moved meaningfully closer to target, update progress .
        if (currentDistanceToTarget < lastDistanceToTarget - 0.5d) {
            // must be significantly closer (0.5 blocks^2).
            this.lastPosition = mob.position();
            this.lastProgressTick = mob.tickCount;
            this.lastDistanceToTarget = currentDistanceToTarget;
            return false;
        }

        // If no meaningful progress in 4 ticks, consider stuck.
        return mob.tickCount - lastProgressTick >= 4;
    }

    private void resetProgress() {
        lastPosition = null;
        lastProgressTick = 0;
        lastDistanceToTarget = Double.MAX_VALUE;
    }
}
