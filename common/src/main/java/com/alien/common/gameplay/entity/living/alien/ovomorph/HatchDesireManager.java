package com.alien.common.gameplay.entity.living.alien.ovomorph;

import com.lib.common.network.DataAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;

import java.util.Objects;

import com.avp.common.util.AVPPredicates;

public class HatchDesireManager {

    private static final int MAXIMUM_DESIRE_TO_HATCH = 100;

    private final Ovomorph ovomorph;

    private final DataAccessor<Integer> desireToHatch;

    private VibrationInfo lastVibrationInfo;

    public HatchDesireManager(Ovomorph ovomorph) {
        this.ovomorph = ovomorph;

        this.desireToHatch = ovomorph.getDataContainer()
            .<Integer>builder("desireToHatch")
            .persistent(Codec.INT)
            .build(0);
    }

    public void tick() {
        handleVibration();

        if (ovomorph.tickCount % 20 == 0) {
            addDesire(-1);
        }

        if (ovomorph.getHatchManager().isHatched()) {
            desireToHatch.reset();
        }
    }

    private void handleVibration() {
        var vibrationSystemManager = ovomorph.getVibrationSystemManager();
        var vibrationInfo = vibrationSystemManager.getVibrationData()
            .getCurrentVibration();

        if (vibrationInfo != null && !Objects.equals(vibrationInfo, lastVibrationInfo)) {
            var radius = vibrationSystemManager.getVibrationUser()
                .getListenerRadius();
            var sourceEntity = vibrationInfo.entity();

            if (
                sourceEntity != null && AVPPredicates.isFreeHost(ovomorph, sourceEntity) && ovomorph.getSensing()
                    .hasLineOfSight(sourceEntity)
            ) {
                var baseDesire = (int) Math.abs(radius - vibrationInfo.distance());

                var level = ovomorph.level();
                var blockPos = ovomorph.blockPosition();
                var brightness = level.getRawBrightness(blockPos, 0);
                var bonusFactor = 5.0;
                var clampedBrightness = Math.clamp(brightness, bonusFactor, 15);
                // At full brightness, the bonus is 3x for the egg being more likely to hatch.
                var brightnessBonus = (int) (clampedBrightness / bonusFactor);

                addDesire(baseDesire * brightnessBonus);
            }

            this.lastVibrationInfo = vibrationInfo;
        }
    }

    public boolean wantsToHatch() {
        return desireToHatch.get() == MAXIMUM_DESIRE_TO_HATCH;
    }

    private void addDesire(int desireAmount) {
        desireToHatch.set(Math.clamp(desireToHatch.get() + desireAmount, 0, MAXIMUM_DESIRE_TO_HATCH));
    }
}
