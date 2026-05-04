package com.blib.internal.mixin;

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

import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.DismembermentManager;

/**
 * Gives every {@link LivingEntity} an implicit {@link DismembermentManager} so the dismemberment system can be applied
 * uniformly to vanilla mobs without each entity class wiring it up by hand. Subclasses that previously declared
 * {@code Dismemberable} explicitly should drop that declaration and the duplicated field/save logic.
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_Dismemberable extends Entity implements Dismemberable {

    @Unique
    private DismembermentManager blib$dismembermentManager;

    public MixinLivingEntity_Dismemberable(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void blib$initDismembermentManager(EntityType<?> entityType, Level level, CallbackInfo ci) {
        var self = LivingEntity.class.cast(this);
        this.blib$dismembermentManager = new DismembermentManager(self);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void blib$saveDismembermentData(CompoundTag compoundTag, CallbackInfo ci) {
        if (blib$dismembermentManager != null) {
            blib$dismembermentManager.save(compoundTag);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void blib$loadDismembermentData(CompoundTag compoundTag, CallbackInfo ci) {
        if (blib$dismembermentManager != null) {
            blib$dismembermentManager.load(compoundTag);
        }
    }

    @Override
    public DismembermentManager getDismembermentManager() {
        return blib$dismembermentManager;
    }
}
