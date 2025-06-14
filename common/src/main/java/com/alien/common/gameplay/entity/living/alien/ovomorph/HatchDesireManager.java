package com.alien.common.gameplay.entity.living.alien.ovomorph;

import com.avp.common.util.AVPPredicates;
import com.lib.common.gameplay.NBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;

import java.util.EnumMap;
import java.util.Objects;

public class HatchDesireManager implements NBTSerializable {

    private static final String NBT_DESIRE_TO_HATCH = "desireToHatch";

    private static final int MAXIMUM_DESIRE_TO_HATCH = 100;

    private final EnumMap<LightLayer, Integer> lastBrightnessMap;

    private final Ovomorph ovomorph;

    private int desireToHatch;

    private VibrationInfo lastVibrationInfo;

    public HatchDesireManager(Ovomorph ovomorph) {
        this.ovomorph = ovomorph;
        this.lastBrightnessMap = new EnumMap<>(LightLayer.class);
        this.desireToHatch = 0;

        lastBrightnessMap.put(LightLayer.BLOCK, getBrightness(LightLayer.BLOCK));
        lastBrightnessMap.put(LightLayer.SKY, getBrightness(LightLayer.SKY));
    }

    private int getBrightness(LightLayer lightLayer) {
        return ovomorph.level().getBrightness(lightLayer, ovomorph.blockPosition());
    }

    public void tick() {
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

        lastBrightnessMap.put(LightLayer.BLOCK, blockBrightness);
        lastBrightnessMap.put(LightLayer.SKY, skyBrightness);

        if (ovomorph.tickCount % 20 == 0) {
            addDesire(-1);
        }

        if (ovomorph.getHatchManager().isHatched()) {
            this.desireToHatch = 0;
        }
    }

    public boolean wantsToHatch() {
        return desireToHatch == MAXIMUM_DESIRE_TO_HATCH;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(NBT_DESIRE_TO_HATCH)) {
            this.desireToHatch = compoundTag.getInt(NBT_DESIRE_TO_HATCH);
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        compoundTag.putInt(NBT_DESIRE_TO_HATCH, desireToHatch);
    }

    private void addDesire(int desireAmount) {
        this.desireToHatch = Math.clamp(desireToHatch + desireAmount, 0, MAXIMUM_DESIRE_TO_HATCH);
    }
}
