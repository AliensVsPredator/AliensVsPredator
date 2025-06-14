package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.lib.common.gameplay.goap.GOAPSensor;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;

import java.util.EnumMap;
import java.util.Objects;

import com.avp.common.util.AVPPredicates;

public class WantsToHatchSensor implements GOAPSensor<Ovomorph> {

    private static final int MAX_DESIRE = 100;

    private final EnumMap<LightLayer, Integer> lastBrightnessMap;

    private boolean isInitialized = false;

    private int desireToHatch = 0;

    private VibrationInfo lastVibrationInfo;

    public WantsToHatchSensor() {
        this.lastBrightnessMap = new EnumMap<>(LightLayer.class);
    }

    @Override
    public void sense(Ovomorph ovomorph, GOAPMutableWorldState worldState) {
        var level = ovomorph.level();
        var blockPos = ovomorph.blockPosition();
        var blockBrightness = level.getBrightness(LightLayer.BLOCK, blockPos);
        var skyBrightness = level.getBrightness(LightLayer.SKY, blockPos);

        if (!isInitialized) {
            lastBrightnessMap.put(LightLayer.BLOCK, blockBrightness);
            lastBrightnessMap.put(LightLayer.SKY, skyBrightness);
            this.isInitialized = true;
            return;
        }

        lastBrightnessMap.forEach((lightLayer, lastBrightness) -> {
            var currentBrightness = level.getBrightness(lightLayer, blockPos);

            if (currentBrightness != lastBrightness) {
                var brightnessDifference = Math.abs(currentBrightness - lastBrightness);
                addDesire(brightnessDifference);
            }
        });

        var vibrationSystemManager = ovomorph.getVibrationSystemManager();
        var vibrationInfo = vibrationSystemManager.getVibrationData()
            .getCurrentVibration();

        if (vibrationInfo != null && !Objects.equals(vibrationInfo, lastVibrationInfo)) {
            var radius = vibrationSystemManager.getVibrationUser()
                .getListenerRadius();
            var sourceEntity = vibrationInfo.entity();

            if (sourceEntity != null && AVPPredicates.isHost(sourceEntity) && ovomorph.getSensing().hasLineOfSight(sourceEntity)) {
                addDesire((int) Math.abs(radius - vibrationInfo.distance()) * 2);
            }

            this.lastVibrationInfo = vibrationInfo;
        }

        worldState.set(OvomorphGOAP.WANTS_TO_HATCH, desireToHatch == MAX_DESIRE);

        lastBrightnessMap.put(LightLayer.BLOCK, blockBrightness);
        lastBrightnessMap.put(LightLayer.SKY, skyBrightness);

        if (ovomorph.tickCount % 20 == 0) {
            addDesire(-1);
        }
    }

    private void addDesire(int desireAmount) {
        this.desireToHatch = Math.clamp(desireToHatch + desireAmount, 0, MAX_DESIRE);
    }
}
