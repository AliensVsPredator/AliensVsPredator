package org.avp.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import org.avp.common.tag.AVPDamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.avp.common.entity.type.AVPEntityTypes;
import org.avp.common.tag.AVPEntityTags;
import org.avp.common.util.MixinUtils;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_ProduceAcid extends Entity {

    protected MixinLivingEntity_ProduceAcid(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "hurt")
    void hurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var self = MixinUtils.<LivingEntity>self(this);
        var level = self.level();

        if (!level.isClientSide() && self.getType().is(AVPEntityTags.ACID_BLEEDERS)) {
            if (!damageSource.is(AVPDamageTypeTags.PUNCTURE)) {
                return;
            }

            var box = self.getBoundingBox();
            var pos = self.blockPosition();
            var randomX = random.nextInt((int) Math.ceil(box.maxX - box.minX)) * (random.nextBoolean() ? -1 : 1);
            var randomZ = random.nextInt((int) Math.ceil(box.maxZ - box.minZ)) * (random.nextBoolean() ? -1 : 1);
            var randomPos = pos.offset(randomX, 0, randomZ);
            AVPEntityTypes.INSTANCE.acid.get().spawn((ServerLevel) level, randomPos, MobSpawnType.NATURAL);
        }
    }
}
