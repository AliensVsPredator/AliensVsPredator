package org.avp.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.api.entity.Host;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_Host extends Entity implements Host {

    private static final String TICKS_UNTIL_PARASITE_BORN = "ticksUntilParasiteBorn";

    private int ticksUntilParasiteBorn = -1;

    protected MixinLivingEntity_Host(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "defineSynchedData")
    void defineSynchedData(CallbackInfo callbackInfo) {
        // TODO:
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    void addAdditionalSaveData(CompoundTag nbt, CallbackInfo callbackInfo) {
        nbt.putInt(TICKS_UNTIL_PARASITE_BORN, ticksUntilParasiteBorn);
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    void readAdditionalSaveData(CompoundTag nbt, CallbackInfo callbackInfo) {
        if (nbt.contains(TICKS_UNTIL_PARASITE_BORN)) {
            ticksUntilParasiteBorn = nbt.getInt(TICKS_UNTIL_PARASITE_BORN);
        }
    }

    @Override
    public int getTicksUntilParasiteBorn() {
        return ticksUntilParasiteBorn;
    }
}
