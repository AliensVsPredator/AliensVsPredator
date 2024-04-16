package org.avp.mixin;

import net.minecraft.nbt.CompoundTag;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.api.entity.Host;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements Host {

    private static final String TICKS_UNTIL_PARASITE_BORN = "ticksUntilParasiteBorn";

    private int ticksUntilParasiteBorn = -1;

    protected MixinLivingEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "hurt")
    void hurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var self = MixinUtils.<LivingEntity>self(this);
        var level = self.level();

        if (!level.isClientSide() && self.getType().is(AVPEntityTags.ACID_BLEEDERS)) {
            AVPEntityTypes.ACID.get().spawn((ServerLevel) level, self.blockPosition(), MobSpawnType.NATURAL);
        }
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
