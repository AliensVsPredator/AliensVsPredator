package org.avp.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.common.registry.alien.maturation.AVPAlienMaturationRegistry;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.api.util.TypeUtil;

@Mixin(Mob.class)
public abstract class MixinMob_AliensMature extends LivingEntity {

    protected MixinMob_AliensMature(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    void tick(CallbackInfo callbackInfo) {
        var self = TypeUtil.<Mob>self(this);
        var level = self.level();

        if (level.isClientSide)
            return;
        if (!self.getType().is(AVPEntityTypeTags.ALIENS))
            return;
        if (self.tickCount % 20 != 0)
            return;

        var maturationStepOptional = AVPAlienMaturationRegistry.lookup(null, self.getType());

        maturationStepOptional.ifPresent(maturationStep -> {
            var entityTypeToMatureInto = maturationStep.to();

            if (self.getTarget() != null)
                return;
            if (self.tickCount < self.getLastHurtByMobTimestamp() + 20 * 10)
                return;
            if (self.tickCount < maturationStep.growthTime())
                return;
            if (!maturationStep.canMaturePredicate().test(self))
                return;

            // TODO: Various other checks here.
            // - Is there enough space to mature?
            // - What about the original entity's NBT?

            self.remove(RemovalReason.DISCARDED);
            entityTypeToMatureInto.spawn((ServerLevel) level, self.blockPosition(), MobSpawnType.NATURAL);
        });
    }
}
