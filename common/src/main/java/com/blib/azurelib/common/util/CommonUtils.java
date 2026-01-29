package com.blib.azurelib.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public record CommonUtils() {

    public static void summonAoE(
        LivingEntity entity,
        ParticleOptions particle,
        int yOffset,
        int duration,
        float radius,
        boolean hasEffect,
        @Nullable Holder<MobEffect> effect,
        int effectTime
    ) {
        var areaEffectCloudEntity = new AreaEffectCloud(
            entity.level(),
            entity.getX(),
            entity.getY() + yOffset,
            entity.getZ()
        );
        areaEffectCloudEntity.setRadius(radius);
        areaEffectCloudEntity.setDuration(duration);
        areaEffectCloudEntity.setParticle(particle);
        areaEffectCloudEntity.setRadiusPerTick(
            -areaEffectCloudEntity.getRadius() / areaEffectCloudEntity.getDuration()
        );
        if (hasEffect && effect != null && !entity.hasEffect(effect))
            areaEffectCloudEntity.addEffect(new MobEffectInstance(effect, effectTime, 0));
        entity.level().addFreshEntity(areaEffectCloudEntity);
    }
}
