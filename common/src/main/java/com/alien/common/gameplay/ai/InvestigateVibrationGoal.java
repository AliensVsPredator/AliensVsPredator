package com.alien.common.gameplay.ai;

import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.util.AlienPredicates;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;
import java.util.Objects;

public class InvestigateVibrationGoal extends Goal {

    private final Xenomorph xenomorph;

    private Path path;

    public InvestigateVibrationGoal(Xenomorph xenomorph) {
        this.xenomorph = xenomorph;

        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (xenomorph.getTarget() != null) {
            return false;
        }

        var lastVibration = xenomorph.getVibrationSystemManager()
            .getVibrationData()
            .getCurrentVibration();

        if (lastVibration == null || !(lastVibration.entity() instanceof LivingEntity livingEntity)) {
            return false;
        }

        if (!AlienPredicates.canTarget(xenomorph, livingEntity)) {
            return false;
        }

        var pos = lastVibration.pos();
        this.path = xenomorph.getNavigation().createPath(pos.x, pos.y, pos.z, 0);

        return path != null;
    }

    @Override
    public boolean canContinueToUse() {
        return path != null
            && xenomorph.getTarget() == null;
    }

    @Override
    public void start() {
        if (path != null) {
            xenomorph.getNavigation().moveTo(path, 0.5);
        }
    }

    @Override
    public void tick() {
        var lastVibration = xenomorph.getVibrationSystemManager()
            .getVibrationData()
            .getCurrentVibration();

        if (lastVibration != null && !Objects.equals(path.getTarget(), BlockPos.containing(lastVibration.pos()))) {
            var pos = lastVibration.pos();
            this.path = xenomorph.getNavigation().createPath(pos.x, pos.y, pos.z, 0);

            if (path != null) {
                xenomorph.getNavigation().moveTo(path, 0.5);
            }
        }

        if (xenomorph.getNavigation().isDone()) {
            this.path = null;
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.path = null;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }
}
