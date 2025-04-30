package com.avp.fabric.common.block.resin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.fabric.common.effect.AVPEffects;
import com.avp.fabric.common.effect.RadiationStatusEffect;
import com.avp.fabric.common.util.AVPPredicates;

public class IrradiatedResinVeinBlock extends ResinVeinBlock {

    public IrradiatedResinVeinBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (AVPPredicates.canBeIrradiated(entity) && entity instanceof LivingEntity livingEntity) {
            // Apply radiation effect.
            livingEntity.addEffect(new MobEffectInstance(AVPEffects.RADIATION_EFFECT, RadiationStatusEffect.EFFECT_DURATION_IN_TICKS, 0));
        }

        super.stepOn(level, blockPos, blockState, entity);
    }
}
