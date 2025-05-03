package com.avp.common.block.resin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import com.avp.common.effect.AVPMobEffects;
import com.avp.common.effect.RadiationStatusEffect;
import com.avp.common.util.AVPPredicates;

public class IrradiatedResinBlock extends ResinBlock {

    public IrradiatedResinBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState, @NotNull Entity entity) {
        if (AVPPredicates.canBeIrradiated(entity) && entity instanceof LivingEntity livingEntity) {
            // Apply radiation effect.
            livingEntity.addEffect(
                new MobEffectInstance(AVPMobEffects.RADIATION.getHolder(), RadiationStatusEffect.EFFECT_DURATION_IN_TICKS, 0)
            );
        }

        super.stepOn(level, blockPos, blockState, entity);
    }
}
