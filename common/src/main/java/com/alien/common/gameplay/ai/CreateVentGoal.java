package com.alien.common.gameplay.ai;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.lib.common.data.Cooldown;
import com.lib.common.util.DirectionUtil;
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
        var resinHolder = alienVariantType.resin();
        var resinVentHolder = alienVariantType.resinVent();
        var resinWebHolder = alienVariantType.resinWeb();

        for (var i = 0; i < WALL_DEPTH; i++) {
            var tunnelPos = wallStart.relative(direction, i);

            level.setBlock(tunnelPos, resinWebHolder.get().defaultBlockState(), Block.UPDATE_ALL);

            for (var relativeDirection : DirectionUtil.VALUES) {
                if (relativeDirection == direction || relativeDirection == direction.getOpposite()) {
                    continue;
                }

                var tunnelWallPos = tunnelPos.relative(relativeDirection);

                level.setBlock(tunnelWallPos, resinHolder.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }

        var ventPos = wallStart.relative(direction, WALL_DEPTH);

        level.setBlock(ventPos, resinVentHolder.get().defaultBlockState(), Block.UPDATE_ALL);
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
            || xenomorph.getHiveManager()
                .hive()
                .isNoneOr(
                    hive -> !hive.isAlive()
                        || hive.isAngry()
                        || !hive.getSpaceManager().isEntityWithinHive(xenomorph)
                )
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

    private boolean isValidWall(BlockPos start, Direction direction) {
        for (var i = 0; i < WALL_DEPTH; i++) {
            var tunnelPos = start.relative(direction, i);

            if (!isTunnelPosClear(tunnelPos)) {
                return false;
            }

            for (var adj : DirectionUtil.VALUES) {
                if (adj == direction || adj == direction.getOpposite()) {
                    continue;
                }

                var tunnelWallPos = tunnelPos.relative(adj);

                if (!isTunnelWallReplaceable(tunnelWallPos)) {
                    return false;
                }
            }
        }

        var ventPos = start.relative(direction, WALL_TOTAL_DEPTH - 1);

        return isTunnelPosClear(ventPos);
    }

    private boolean isTunnelPosClear(BlockPos pos) {
        var blockState = level.getBlockState(pos);

        return blockState.isAir()
            || isTunnelWallReplaceable(pos);
    }

    private boolean isTunnelWallReplaceable(BlockPos pos) {
        var blockState = level.getBlockState(pos);
        var alienVariantType = AlienVariantTypes.getFor(xenomorph);

        if (blockState.is(AVPBlockTags.XENOMORPH_IMMUNE)) {
            return false;
        }

        return blockState.is(alienVariantType.resin().get())
            || blockState.is(alienVariantType.resinReplaceableTag());
    }
}
