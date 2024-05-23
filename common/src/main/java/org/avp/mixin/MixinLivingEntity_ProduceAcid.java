package org.avp.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.avp.common.entity.data.AcidData;
import org.avp.common.tag.AVPDamageTypeTags;
import org.avp.common.tag.AVPEntityTypeTags;
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

        if (!level.isClientSide() && self.getType().is(AVPEntityTypeTags.ACID_BLEEDERS)) {
            if (
                damage > self.getMaxHealth() * 0.1 ||
                    self.getHealth() <= self.getMaxHealth() * 0.33 ||
                    damageSource.is(AVPDamageTypeTags.IS_PUNCTURING) ||
                    hurtByPlayerWithAxeOrSwordWeapon(damageSource)
            ) {
                var box = self.getBoundingBox();
                var pos = self.blockPosition();
                var randomX = random.nextInt((int) Math.ceil(box.maxX - box.minX)) * (random.nextBoolean() ? -1 : 1);
                var randomZ = random.nextInt((int) Math.ceil(box.maxZ - box.minZ)) * (random.nextBoolean() ? -1 : 1);
                var randomPos = pos.offset(randomX, 0, randomZ);
                AcidData.INSTANCE.getHolder().get().spawn((ServerLevel) level, randomPos, MobSpawnType.NATURAL);
            }
        }
    }

    @Unique
    private boolean hurtByPlayerWithAxeOrSwordWeapon(DamageSource damageSource) {
        Player thePlayer;
        var maybePlayer = damageSource.getEntity();
        var maybePlayerOther = damageSource.getDirectEntity();

        if (maybePlayer instanceof Player player) {
            thePlayer = player;
        } else if (maybePlayerOther instanceof Player playerOther) {
            thePlayer = playerOther;
        } else {
            return false;
        }

        var mainhandItem = thePlayer.getMainHandItem();
        return mainhandItem.is(ItemTags.AXES) || mainhandItem.is(ItemTags.SWORDS);
    }
}
