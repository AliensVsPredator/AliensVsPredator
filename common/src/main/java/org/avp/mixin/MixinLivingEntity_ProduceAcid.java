package org.avp.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
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
            var tenPercentMaxHealth = self.getMaxHealth() * 0.1;
            if (doesAttackHurtXenomorphBadly(damageSource, damage, tenPercentMaxHealth, self)) {
                var randomPos = computeRandomPosFromBoundingBox(self);
                spawnAcid(damage, level, randomPos, self, tenPercentMaxHealth);
            }
        }
    }

    @Unique
    private void spawnAcid(float damage, Level level, BlockPos randomPos, LivingEntity self, double tenPercentMaxHealth) {
        var acidEntityType = AcidData.INSTANCE.getHolder().get();
        var acidEntity = acidEntityType.create(level);

        if (acidEntity != null) {
            acidEntity.moveTo(randomPos, self.getYRot(), self.getXRot());
            level.addFreshEntity(acidEntity);
            // Acid multiplier scales with how much damage was dealt in this attack.
            var multiplier = Math.max((int) (damage / tenPercentMaxHealth), 1);
            acidEntity.setMultiplier(multiplier);
        }
    }

    @Unique
    private boolean doesAttackHurtXenomorphBadly(DamageSource damageSource, float damage, double tenPercentMaxHealth, LivingEntity self) {
        return damage > tenPercentMaxHealth ||
            self.getHealth() <= self.getMaxHealth() * 0.33 ||
            damageSource.is(AVPDamageTypeTags.IS_PUNCTURING) ||
            hurtByPlayerWithAxeOrSwordWeapon(damageSource);
    }

    @Unique
    private @NotNull BlockPos computeRandomPosFromBoundingBox(LivingEntity self) {
        var box = self.getBoundingBox();
        var pos = self.blockPosition();
        var randomX = random.nextInt((int) Math.ceil(box.maxX - box.minX)) * (random.nextBoolean() ? -1 : 1);
        var randomZ = random.nextInt((int) Math.ceil(box.maxZ - box.minZ)) * (random.nextBoolean() ? -1 : 1);
        return pos.offset(randomX, 0, randomZ);
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
