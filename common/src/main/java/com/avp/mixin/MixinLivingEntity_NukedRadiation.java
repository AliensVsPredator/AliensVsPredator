package com.avp.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.common.effect.AVPMobEffects;
import com.avp.common.effect.RadiationStatusEffect;
import com.avp.common.util.TempAVPPredicates;
import com.avp.common.worldgen.biome.AVPBiomes;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_NukedRadiation extends Entity {

    public MixinLivingEntity_NukedRadiation(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = LivingEntity.class.cast(this);

        if (!TempAVPPredicates.canBeIrradiated(self)) {
            return;
        }

        if (!isIsEntityInAnIrradiatedBiome(self)) {
            // Entity is not in an irradiated biome, can't possibly irradiate them. Abort.
            return;
        }

        // Apply radiation effect.
        self.addEffect(new MobEffectInstance(AVPMobEffects.RADIATION, RadiationStatusEffect.EFFECT_DURATION_IN_TICKS, 0));
    }

    @Unique
    private boolean isIsEntityInAnIrradiatedBiome(LivingEntity self) {
        return self.level().getBiome(self.blockPosition()).is(AVPBiomes.NUKED_BIOME);
    }
}
