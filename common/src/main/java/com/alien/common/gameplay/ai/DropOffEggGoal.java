package com.alien.common.gameplay.ai;

import com.alien.common.gameplay.entity.living.alien.EggCarrier;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.avp.common.registry.init.AVPSoundEvents;

public class DropOffEggGoal<T extends Xenomorph & EggCarrier> extends Goal {

    private static final List<BlockPos> EGG_GRID_POS_OFFSETS = generateSpiralOffsets(16);

    private final T eggCarryingXenomorph;

    private BlockPos freeEggPos;

    public DropOffEggGoal(T eggCarryingXenomorph) {
        this.eggCarryingXenomorph = eggCarryingXenomorph;
    }

    @Override
    public boolean canUse() {
        if (!canContinueToUse()) {
            return false;
        }

        this.freeEggPos = findFreeEggSpot(eggCarryingXenomorph.level(), eggCarryingXenomorph.blockPosition(), pos -> {
            var state = eggCarryingXenomorph.level().getBlockState(pos);
            return state.entityCanStandOn(eggCarryingXenomorph.level(), pos, eggCarryingXenomorph);
        }).orElse(null);

        return freeEggPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return eggCarryingXenomorph.getTarget() == null && isCarryingOvomorph();
    }

    @Override
    public void start() {
        if (freeEggPos != null) {
            eggCarryingXenomorph.getNavigation().moveTo(freeEggPos.getX(), freeEggPos.getY(), freeEggPos.getZ(), 0.5);
        }
    }

    @Override
    public void tick() {
        if (freeEggPos != null) {
            eggCarryingXenomorph.getNavigation().moveTo(freeEggPos.getX(), freeEggPos.getY(), freeEggPos.getZ(), 0.5);

            if (eggCarryingXenomorph.distanceToSqr(freeEggPos.getCenter()) <= 2 * 2) {
                var center = freeEggPos.getCenter();
                getPassengerOvomorphs().forEach(ovomorph -> {
                    eggCarryingXenomorph.level()
                        .playSound(null, ovomorph, AVPSoundEvents.ENTITY_OVOMORPH_ROOT.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                    ovomorph.isRooted.set(true);
                    ovomorph.stopRiding();
                    ovomorph.setPos(center.x, center.y, center.z);
                });
            }
        }
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
        // Snap center to nearest even-even grid position.
        var gridAlignedCenter = new BlockPos(
            (center.getX() & ~1), // force even
            center.getY(),
            (center.getZ() & ~1) // force even
        );
        var useOdd = (gridAlignedCenter.getX() & 1) != 0 && (gridAlignedCenter.getZ() & 1) != 0;

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

                    var path = eggCarryingXenomorph.getNavigation().createPath(adjustedPos, 0);
                    if (path != null && path.canReach()) {
                        return Optional.of(adjustedPos.immutable());
                    }
                }
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
