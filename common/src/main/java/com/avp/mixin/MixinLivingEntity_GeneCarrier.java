package com.avp.mixin;

import com.alien.common.model.alien.GeneCarrier;
import com.alien.common.util.AcidBleedUtil;
import com.lib.common.gameplay.entity.manager.GeneManager;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import com.lib.common.network.DataAccessor;
import com.lib.common.network.DataUser;
import com.lib.common.util.GeneResistanceHurtUtil;
import com.lib.common.util.TeleportUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
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

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GeneCarrier extends Entity implements GeneCarrier, DataUser {

    @Unique
    private final DataAccessor<Boolean> hasWarpEffect = getDataContainer().<Boolean>builder("hasWarpEffect")
        .networkSynchronized(ByteBufCodecs.BOOL)
        .build(false);

    @Unique
    private GeneManager avp$geneManager;

    public MixinLivingEntity_GeneCarrier(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var geneManager = getOrCreateGeneManager();

        geneManager.tick();

        if (level().isClientSide && hasWarpEffect.get()) {
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
            var hasWarpGene = getOrCreateGeneManager().getGeneContainer()
                .getActiveGeneMap()
                .hasGene(Genes.WARP);
            hasWarpEffect.set(hasWarpGene);
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
        if (
            hasWarpEffect.get()
                && (damageSource.is(DamageTypeTags.IS_PROJECTILE)
                    || random.nextInt(10) == 0)
        ) {
            var self = LivingEntity.class.cast(this);

            for (var i = 0; i < 64; ++i) {
                if (TeleportUtil.teleport(self)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "hurt")
    public void avp$hurtReturnEffects(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        var isHurt = cir.getReturnValueZ();

        if (isHurt) {
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
