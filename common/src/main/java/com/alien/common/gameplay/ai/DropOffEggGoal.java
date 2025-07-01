package com.alien.common.gameplay.ai;

import com.alien.common.gameplay.entity.living.alien.EggCarrier;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.avp.common.registry.init.AVPSoundEvents;

public class DropOffEggGoal<T extends Xenomorph & EggCarrier> extends Goal {

    private static final List<BlockPos> EGG_GRID_POS_OFFSETS = generateSpiralOffsets(16);

    private final T eggCarryingXenomorph;

    private final Set<BlockPos> failedEggSpots;

    private BlockPos freeEggPos;

    private Path pathToFreeEggPos;

    public DropOffEggGoal(T eggCarryingXenomorph) {
        this.eggCarryingXenomorph = eggCarryingXenomorph;
        this.failedEggSpots = new HashSet<>();
    }

    @Override
    public boolean canUse() {
        if (isUnableToMoveOvomorph()) {
            return false;
        }

        this.freeEggPos = findFreeEggSpot(eggCarryingXenomorph.level(), eggCarryingXenomorph.blockPosition(), pos -> {
            var state = eggCarryingXenomorph.level().getBlockState(pos);
            return state.entityCanStandOn(eggCarryingXenomorph.level(), pos, eggCarryingXenomorph);
        }).orElse(null);

        if (freeEggPos != null) {
            this.pathToFreeEggPos = eggCarryingXenomorph.getNavigation().createPath(freeEggPos, 0);

            if (pathToFreeEggPos != null) {
                eggCarryingXenomorph.getNavigation().moveTo(pathToFreeEggPos, 0.5);
            }
        }

        var hasAccessibleFreeEggPos = freeEggPos != null && pathToFreeEggPos != null;

        if (!hasAccessibleFreeEggPos) {
            // Can't move the egg anywhere? Kill it.
            getPassengerOvomorphs()
                .forEach(Entity::discard);
        }

        return hasAccessibleFreeEggPos;
    }

    @Override
    public boolean canContinueToUse() {
        return freeEggPos != null
            && pathToFreeEggPos != null
            && !pathToFreeEggPos.isDone()
            && pathToFreeEggPos.canReach()
            && !isUnableToMoveOvomorph();
    }

    @Override
    public void start() {
        if (pathToFreeEggPos != null) {
            eggCarryingXenomorph.getNavigation().moveTo(pathToFreeEggPos, 0.5);
        }
    }

    @Override
    public void tick() {
        if (freeEggPos == null || pathToFreeEggPos == null || !pathToFreeEggPos.canReach()) {
            this.freeEggPos = null;
            this.pathToFreeEggPos = null;
            return;
        }

        eggCarryingXenomorph.getNavigation().moveTo(pathToFreeEggPos, 0.5);

        if (eggCarryingXenomorph.distanceToSqr(freeEggPos.getCenter()) <= 2 * 2) {
            var center = freeEggPos.getCenter();
            getPassengerOvomorphs().forEach(ovomorph -> {
                eggCarryingXenomorph.level()
                    .playSound(null, ovomorph, AVPSoundEvents.ENTITY_OVOMORPH_ROOT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                ovomorph.isRooted.set(true);
                ovomorph.stopRiding();
                ovomorph.setPos(center.x, center.y, center.z);

                var randomYaw = eggCarryingXenomorph.getRandom().nextFloat() * 360.0F;

                ovomorph.setYRot(randomYaw);
                ovomorph.setYHeadRot(randomYaw);
                ovomorph.yBodyRot = randomYaw;

                ovomorph.yRotO = randomYaw;
                ovomorph.yHeadRotO = randomYaw;
                ovomorph.yBodyRotO = randomYaw;
            });
        }
    }

    @Override
    public void stop() {
        super.stop();

        failedEggSpots.clear();
        this.freeEggPos = null;
        this.pathToFreeEggPos = null;
    }

    private boolean isUnableToMoveOvomorph() {
        return eggCarryingXenomorph.getTarget() != null || !isCarryingOvomorph();
    }

    private boolean isCarryingOvomorph() {
        return !getPassengerOvomorphs().isEmpty();
    }

    private List<Ovomorph> getPassengerOvomorphs() {
        return eggCarryingXenomorph.getPassengers()
            .stream()
            .filter(passenger -> passenger instanceof Ovomorph)
            .map(passenger -> (Ovomorph) passenger)
            .toList();
    }


    private Optional<BlockPos> findFreeEggSpot(
        Level level,
        BlockPos center,
        Predicate<BlockPos> isWalkable
    ) {
        var centerIsOdd = (center.getX() & 1) != 0 && (center.getZ() & 1) != 0;

        // Try preferred parity first (based on current position)
        var result = tryFindWithParity(level, center, isWalkable, centerIsOdd);

        // If no result, try the opposite parity
        if (result.isEmpty()) {
            result = tryFindWithParity(level, center, isWalkable, !centerIsOdd);
        }

        return result;
    }

    private Optional<BlockPos> tryFindWithParity(
        Level level,
        BlockPos center,
        Predicate<BlockPos> isWalkable,
        boolean useOdd
    ) {
        // Snap to correct grid center for the desired parity
        var baseX = (center.getX() & ~1) + (useOdd ? 1 : 0);
        var baseZ = (center.getZ() & ~1) + (useOdd ? 1 : 0);
        var gridAlignedCenter = new BlockPos(baseX, center.getY(), baseZ);

        var viable = new ArrayList<BlockPos>();

        for (var offset : EGG_GRID_POS_OFFSETS) {
            var pos = gridAlignedCenter.offset(offset);

            // Only proceed if pos.x and pos.z match the expected parity
            if (((pos.getX() & 1) == 0) == useOdd) {
                continue;
            }

            if (((pos.getZ() & 1) == 0) == useOdd) {
                continue;
            }

            // Try to adjust the Y up/down to find a valid drop-off spot
            final int verticalSearchRange = 4; // how far up/down to search

            for (var dy = -verticalSearchRange; dy <= verticalSearchRange; dy++) {
                var adjustedPos = pos.above(dy);

                if (failedEggSpots.contains(adjustedPos)) {
                    continue;
                }

                var below = adjustedPos.below();

                if (!isWalkable.test(below)) {
                    continue;
                }

                var state = level.getBlockState(adjustedPos);
                var aboveState = level.getBlockState(adjustedPos.above());

                if (
                    (state.isAir() || state.canBeReplaced())
                        && (aboveState.isAir() || aboveState.canBeReplaced())
                        && level.getEntities(null, new AABB(adjustedPos)).isEmpty()
                ) {
                    viable.add(adjustedPos.immutable());
                }
            }
        }

        viable.sort(Comparator.comparingDouble(pos -> pos.distSqr(gridAlignedCenter)));

        for (int i = 0; i < Math.min(viable.size(), 5); i++) {
            var pos = viable.get(i);
            var path = eggCarryingXenomorph.getNavigation().createPath(pos, 0);

            if (path != null && path.canReach()) {
                return Optional.of(pos);
            } else {
                failedEggSpots.add(pos);
            }
        }

        return Optional.empty();
    }

    private static List<BlockPos> generateSpiralOffsets(int maxDist) {
        var step = 2;
        var offsets = new ArrayList<BlockPos>();

        for (var dist = 0; dist <= maxDist; dist += step) {
            for (var dx = -dist; dx <= dist; dx += step) {
                var dz = dist - Math.abs(dx);

                if ((dz & 1) != 0) {
                    continue;
                }

                offsets.add(new BlockPos(dx, 0, dz));

                if (dz != 0) {
                    offsets.add(new BlockPos(dx, 0, -dz));
                }
            }
        }

        return offsets;
    }

}
