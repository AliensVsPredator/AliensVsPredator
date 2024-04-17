package org.avp.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import org.avp.common.entity.type.AVPEntityTypes;
import org.avp.common.tag.AVPEntityTags;
import org.avp.common.util.MixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityProduceAcid extends Entity {

    protected MixinLivingEntityProduceAcid(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "hurt")
    void hurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var self = MixinUtils.<LivingEntity>self(this);
        var level = self.level();

        if (!level.isClientSide() && self.getType().is(AVPEntityTags.ACID_BLEEDERS)) {
            AVPEntityTypes.INSTANCE.ACID.get().spawn((ServerLevel) level, self.blockPosition(), MobSpawnType.NATURAL);
        }
    }
}
