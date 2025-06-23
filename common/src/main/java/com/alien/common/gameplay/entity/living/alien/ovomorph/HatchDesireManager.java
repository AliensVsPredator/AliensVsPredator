package com.alien.common.gameplay.entity.living.alien.ovomorph;

import com.lib.common.network.DataAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;

import java.util.EnumMap;
import java.util.Objects;

import com.avp.common.util.AVPPredicates;

public class HatchDesireManager {

    private static final int MAXIMUM_DESIRE_TO_HATCH = 100;

    private final EnumMap<LightLayer, Integer> lastBrightnessMap;

    private final Ovomorph ovomorph;

    private final DataAccessor<Integer> desireToHatch;

    private VibrationInfo lastVibrationInfo;

    public HatchDesireManager(Ovomorph ovomorph) {
        this.ovomorph = ovomorph;
        this.lastBrightnessMap = new EnumMap<>(LightLayer.class);

        this.desireToHatch = ovomorph.getDataContainer()
            .<Integer>builder("desireToHatch")
            .persistent(Codec.INT)
            .build(0);

        lastBrightnessMap.put(LightLayer.BLOCK, getBrightness(LightLayer.BLOCK));
        lastBrightnessMap.put(LightLayer.SKY, getBrightness(LightLayer.SKY));
    }

    private int getBrightness(LightLayer lightLayer) {
        return ovomorph.level().getBrightness(lightLayer, ovomorph.blockPosition());
    }

    public void tick() {
        handleBrightness();
        handleVibration();

        if (ovomorph.tickCount % 20 == 0) {
            addDesire(-1);
        }

        if (ovomorph.getHatchManager().isHatched()) {
            desireToHatch.reset();
        }
    }

    private void handleBrightness() {
        var level = ovomorph.level();
        var blockPos = ovomorph.blockPosition();
        var blockBrightness = level.getBrightness(LightLayer.BLOCK, blockPos);
        var skyBrightness = level.getBrightness(LightLayer.SKY, blockPos);

        lastBrightnessMap.forEach((lightLayer, lastBrightness) -> {
            var currentBrightness = level.getBrightness(lightLayer, blockPos);

            if (currentBrightness != lastBrightness) {
                var brightnessDifference = Math.abs(currentBrightness - lastBrightness);
                addDesire(brightnessDifference);
            }
        });

        lastBrightnessMap.put(LightLayer.BLOCK, blockBrightness);
        lastBrightnessMap.put(LightLayer.SKY, skyBrightness);
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
                addDesire((int) Math.abs(radius - vibrationInfo.distance()) * 2);
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
