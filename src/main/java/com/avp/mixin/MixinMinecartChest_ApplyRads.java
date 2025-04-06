package com.avp.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.avp.common.effect.AVPEffects;
import com.avp.common.util.AVPPredicates;

@Mixin(MinecartChest.class)
public abstract class MixinMinecartChest_ApplyRads extends Entity {

    public MixinMinecartChest_ApplyRads(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        var chest = (MinecartChest) (Object) this;

        if (AVPPredicates.containsIrradiatedItems(chest)) {
            applyRadiationEffect(chest);
        }
    }

    @Unique
    private void applyRadiationEffect(MinecartChest chest) {
        var effectRadius = chest.getBoundingBox().inflate(3);

        chest.level()
            .getEntitiesOfClass(LivingEntity.class, effectRadius, AVPPredicates::canBeIrradiated)
            .forEach(target -> {
                var mobEffectInstance = new MobEffectInstance(AVPEffects.RADIATION_EFFECT, Integer.MAX_VALUE, 0);
                target.addEffect(mobEffectInstance);
            });
    }

}
