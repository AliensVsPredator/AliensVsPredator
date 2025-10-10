package com.avp.mixin;

import com.alien.common.util.AcidBleedUtil;
import com.lib.common.gameplay.entity.manager.GeneManager;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import com.lib.common.model.GeneCarrier;
import com.lib.common.network.DataAccessor;
import com.lib.common.network.DataUser;
import com.lib.common.util.GeneResistanceHurtUtil;
import com.lib.common.util.TeleportUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.common.registry.init.AVPDataKeys;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GeneCarrier extends Entity implements GeneCarrier, DataUser {

    @Unique
    private final DataAccessor<Boolean> avp$hasWarpEffect = new DataAccessor<>(this, AVPDataKeys.ENTITY_HAS_WARP_EFFECT);

    @Unique
    private GeneManager avp$geneManager;

    public MixinLivingEntity_GeneCarrier(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var geneManager = getOrCreateGeneManager();

        geneManager.tick();

        if (level().isClientSide && avp$hasWarpEffect.get()) {
            for (int i = 0; i < 2; ++i) {
                level().addParticle(
                    ParticleTypes.PORTAL,
                    getRandomX(0.5F),
                    getRandomY() - (double) 0.25F,
                    getRandomZ(0.5F),
                    (random.nextDouble() - (double) 0.5F) * (double) 2.0F,
                    -random.nextDouble(),
                    (random.nextDouble() - (double) 0.5F) * (double) 2.0F
                );
            }
        }

        if (!level().isClientSide) {
            avp$hasWarpEffect.set(avp$hasWarpGene());
        }
    }

    @ModifyVariable(
        method = "hurt",
        at = @At(value = "HEAD"),
        index = 2,
        argsOnly = true
    )
    private float avp$hurt(float originalDamage, DamageSource damageSource) {
        // Apply resistance modifiers to the damage value.
        return GeneResistanceHurtUtil.applyResistancesToDamage(this, damageSource, originalDamage);
    }

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    public void avp$hurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (damage == 0) {
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    public void avp$preHurtEffects(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (!avp$hasWarpEffect.get()) {
            return;
        }

        // TODO: Factor in multiplicative in here.
        var warpStrength = getOrCreateGeneManager().getGeneContainer()
            .getActiveGeneMap()
            .getValue(Genes.WARP, GeneOperationType.ADDITIVE);

        if (shouldTeleportToDodgeProjectile(damageSource, warpStrength) || shouldTeleportRandomly(warpStrength)) {
            var self = LivingEntity.class.cast(this);

            for (var i = 0; i < 64; ++i) {
                if (TeleportUtil.teleport(self)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    private boolean shouldTeleportRandomly(double warpStrength) {
        // If warp strength is 0.1, then this is 1.
        // If warp strength is 0.5, then this is 0.6.
        // If warp strength is 1, then this is 0.1.
        var randomTeleportPoolSizeMultiplier = Math.clamp(1.1 - warpStrength, 0, 1.0);
        var randomTeleportPoolSize = (int) (100 * randomTeleportPoolSizeMultiplier);

        return random.nextInt(randomTeleportPoolSize) == 0;
    }

    private boolean shouldTeleportToDodgeProjectile(DamageSource damageSource, double warpStrength) {
        var projectileTeleportPoolSizeMultiplier = Math.clamp(1.1 - warpStrength, 0, 1.0);
        var projectileTeleportPoolSize = (int) (10 * projectileTeleportPoolSizeMultiplier);

        return damageSource.is(DamageTypeTags.IS_PROJECTILE)
            && random.nextInt(projectileTeleportPoolSize) == 0;
    }

    @Inject(at = @At("RETURN"), method = "hurt")
    public void avp$postHurtEffects(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        var isHurt = cir.getReturnValueZ();

        if (isHurt) {
            avp$handleAcidBloodGene(damageSource, damage);
            avp$handlePoisonousBarbsGene(damageSource, damage);
            avp$handleThornsGene(damageSource, damage);
        }
    }

    @Inject(at = @At("RETURN"), method = "isSensitiveToWater", cancellable = true)
    public void avp$isSensitiveToWater(CallbackInfoReturnable<Boolean> cir) {
        var isSensitiveToWater = cir.getReturnValueZ();
        cir.setReturnValue(isSensitiveToWater || avp$hasWarpGene());
    }

    @Unique
    private boolean avp$hasWarpGene() {
        return getOrCreateGeneManager().getGeneContainer()
            .getActiveGeneMap()
            .hasGene(Genes.WARP);
    }

    @Unique
    private void avp$handleAcidBloodGene(DamageSource damageSource, float damage) {
        // TODO: Factor in additive in here.
        var acidBloodChance = getOrCreateGeneManager().getGeneContainer()
            .getActiveGeneMap()
            .getValue(Genes.ACIDIC_BLOOD, GeneOperationType.MULTIPLICATIVE);

        if (getRandom().nextDouble() < acidBloodChance && damageSource != damageSources().genericKill()) {
            var self = LivingEntity.class.cast(this);
            var randomPos = AcidBleedUtil.computeRandomPosFromBoundingBox(self);
            AcidBleedUtil.spawnAcid(self, damage, randomPos);
        }
    }

    @Unique
    private void avp$handlePoisonousBarbsGene(DamageSource damageSource, float damage) {
        if (
            !damageSource.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
                && damageSource.getDirectEntity() instanceof LivingEntity livingEntity
        ) {
            // TODO: Factor in additive in here.
            var poisonChance = getOrCreateGeneManager().getGeneContainer()
                .getActiveGeneMap()
                .getValue(Genes.POISONOUS_BARBS, GeneOperationType.MULTIPLICATIVE);

            if (getRandom().nextDouble() < poisonChance) {
                // TODO: Amplify level with increasing levels.
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 6 * 20, 0), this);
            }
        }
    }

    @Unique
    private void avp$handleThornsGene(DamageSource damageSource, float damage) {
        if (
            !damageSource.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
                && damageSource.getDirectEntity() instanceof LivingEntity livingEntity
        ) {
            // TODO: Factor in multiplicative in here.
            var thornsDamage = getOrCreateGeneManager().getGeneContainer()
                .getActiveGeneMap()
                .getValue(Genes.THORNS, GeneOperationType.ADDITIVE);

            if (thornsDamage >= 0) {
                livingEntity.hurt(livingEntity.damageSources().thorns(this), (float) thornsDamage);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        getOrCreateGeneManager().load(compoundTag);
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        getOrCreateGeneManager().save(compoundTag);
    }

    @Override
    public GeneManager getOrCreateGeneManager() {
        if (avp$geneManager == null) {
            var self = LivingEntity.class.cast(this);
            this.avp$geneManager = new GeneManager(self);
        }

        return avp$geneManager;
    }

    @Override
    public void setGeneManager(GeneManager geneManager) {
        this.avp$geneManager = geneManager;
    }
}
