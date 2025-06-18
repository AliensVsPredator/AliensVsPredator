package com.avp.mixin;

import com.alien.common.model.alien.GeneCarrier;
import com.lib.common.gameplay.entity.manager.GeneManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GeneCarrier extends Entity implements GeneCarrier {

    @Unique
    private GeneManager geneManager;

    public MixinLivingEntity_GeneCarrier(EntityType<?> entityType, Level level) {
        super(entityType, level);
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
