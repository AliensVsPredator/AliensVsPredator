package com.avp.mixin;

import com.alien.common.model.alien.GeneCarrier;
import com.alien.common.util.AcidBleedUtil;
import com.alien.common.util.GeneResistanceHurtUtil;
import com.lib.common.gameplay.entity.manager.GeneManager;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import net.minecraft.nbt.CompoundTag;
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
public abstract class MixinLivingEntity_GeneCarrier extends Entity implements GeneCarrier {

    @Unique
    private GeneManager geneManager;

    public MixinLivingEntity_GeneCarrier(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        getOrCreateGeneManager().tick();
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

    @Inject(at = @At("RETURN"), method = "hurt")
    public void avp$hurtBleedAcid(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        var isHurt = cir.getReturnValueZ();

        if (isHurt) {
            var geneManager = getOrCreateGeneManager();
            // TODO: Factor in additive in here.
            var acidBloodChance = geneManager.getActiveGeneValue(Genes.ACIDIC_BLOOD, GeneOperationType.MULTIPLICATIVE);

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
        if (geneManager == null) {
            var self = LivingEntity.class.cast(this);
            this.geneManager = new GeneManager(self);
        }

        return geneManager;
    }

    @Override
    public void setGeneManager(GeneManager geneManager) {
        this.geneManager = geneManager;
    }
}
