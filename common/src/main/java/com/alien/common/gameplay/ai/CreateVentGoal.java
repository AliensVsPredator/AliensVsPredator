package com.alien.common.gameplay.ai;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.lib.common.data.Cooldown;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.time.Duration;
import java.util.EnumSet;

import com.avp.common.registry.tag.AVPBlockTags;

public class CreateVentGoal extends Goal {

    private static final int WALL_DEPTH = 3;

    private static final int WALL_TOTAL_DEPTH = 4;

    private final Cooldown cooldown;

    private final Xenomorph xenomorph;

    private final Level level;

    private BlockPos wallStart;

    private Direction direction;

    private boolean drillingStarted;

    public CreateVentGoal(Xenomorph xenomorph) {
        this.xenomorph = xenomorph;
        this.cooldown = Cooldown.withCooldownTime("cooldown", Duration.ofSeconds(15));
        this.level = xenomorph.level();
        this.drillingStarted = false;

        cooldown.reset();
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        cooldown.tick();

        if (isUnableToCreateVent()) {
            return false;
        }

        var footPos = xenomorph.blockPosition();

        for (var dir : Direction.Plane.HORIZONTAL) {
            var hit = rayTraceToSolid(footPos, dir);

            if (hit instanceof BlockHitResult bhr) {
                var hitPos = bhr.getBlockPos();

                var ventAlreadyExists = xenomorph.getHiveManager()
                    .hive()
                    .isSomeAnd(hive -> !hive.getVentManager().getVentsWithinSection(hitPos).isEmpty());

                if (ventAlreadyExists) {
                    continue;
                }

                if (isValidWall(hitPos, dir)) {
                    var pathTarget = hitPos.relative(dir, WALL_TOTAL_DEPTH - 1);
                    var path = xenomorph.getNavigation().createPath(pathTarget, 1);

                    if (path != null && path.canReach()) {
                        this.wallStart = hitPos;
                        this.direction = dir;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void start() {
        var pathTarget = wallStart.relative(direction, WALL_TOTAL_DEPTH - 1);

        xenomorph.getNavigation()
            .moveTo(
                pathTarget.getX() + 0.5,
                pathTarget.getY(),
                pathTarget.getZ() + 0.5,
                0.5
            );
    }

    @Override
    public void tick() {
        if (drillingStarted || !xenomorph.getNavigation().isDone()) {
            return;
        }

        this.drillingStarted = true;

        var alienVariantType = AlienVariantTypes.getFor(xenomorph);
        var resinVentHolder = alienVariantType.resinVent();
        var resinWebHolder = alienVariantType.resinWeb();

        for (var i = 0; i < WALL_DEPTH; i++) {
            var pos = wallStart.relative(direction, i);
            level.setBlock(pos, resinWebHolder.get().defaultBlockState(), Block.UPDATE_ALL);
        }

        var placePos = wallStart.relative(direction, WALL_DEPTH);
        level.setBlock(placePos, resinVentHolder.get().defaultBlockState(), Block.UPDATE_ALL);
        cooldown.reset();
    }

    @Override
    public boolean canContinueToUse() {
        return !isUnableToCreateVent()
            && !drillingStarted;
    }

    @Override
    public void stop() {
        this.drillingStarted = false;
        this.wallStart = null;
        this.direction = null;
    }

    private boolean isUnableToCreateVent() {
        return xenomorph.getTarget() != null
            || cooldown.isActive();
    }

    private HitResult rayTraceToSolid(BlockPos start, Direction dir) {
        var from = Vec3.atCenterOf(start);
        var to = from.add(Vec3.atLowerCornerOf(dir.getNormal()).scale(16));

        return level.clip(
            new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                xenomorph
            )
        );
    }

    private boolean isValidWall(BlockPos start, Direction dir) {
        for (var i = 0; i < WALL_DEPTH; i++) {
            var current = start.relative(dir, i);

            if (isNotReplaceable(current)) {
                return false;
            }

            for (var adj : Direction.values()) {
                if (adj == dir || adj == dir.getOpposite()) {
                    continue;
                }

                if (isNotReplaceable(current.relative(adj))) {
                    return false;
                }
            }
        }

        var fourth = start.relative(dir, WALL_TOTAL_DEPTH - 1);
        return !isNotReplaceable(fourth);
    }

    private boolean isNotReplaceable(BlockPos pos) {
        var blockState = level.getBlockState(pos);

        if (blockState.is(AVPBlockTags.RESIN_VEINS)) {
            return false;
        }

        return blockState.isAir()
            || blockState.canBeReplaced()
            || !blockState.canOcclude()
            || blockState.is(AVPBlockTags.XENOMORPH_IMMUNE);
    }
}
