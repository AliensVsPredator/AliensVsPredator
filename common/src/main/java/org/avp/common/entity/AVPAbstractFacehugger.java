package org.avp.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.entity.Morphable;
import org.avp.common.entity.data.OvamorphData;
import org.jetbrains.annotations.NotNull;

public abstract class AVPAbstractFacehugger extends AVPAbstractParasite implements Morphable {

    protected AVPAbstractFacehugger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.1F, 0.25F);
    }

    @Override
    public EntityType<?> getEntityTypeForPreviousForm() {
        return OvamorphData.INSTANCE.getHolder().get();
    }
}
