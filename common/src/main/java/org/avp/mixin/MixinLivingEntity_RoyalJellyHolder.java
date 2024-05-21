package org.avp.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.avp.api.entity.RoyalJellyHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_RoyalJellyHolder extends Entity implements RoyalJellyHolder {

    private static final String HAS_ROYAL_JELLY_KEY = "HasRoyalJelly";

    private boolean hasRoyalJelly = false;

    protected MixinLivingEntity_RoyalJellyHolder(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    void addAdditionalSaveData(CompoundTag nbt, CallbackInfo callbackInfo) {
        if (hasRoyalJelly) {
            nbt.putBoolean(HAS_ROYAL_JELLY_KEY, true);
        }
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    void readAdditionalSaveData(CompoundTag nbt, CallbackInfo callbackInfo) {
        this.hasRoyalJelly = nbt.getBoolean(HAS_ROYAL_JELLY_KEY);
    }

    @Override
    public boolean hasRoyalJelly() {
        return hasRoyalJelly;
    }

    @Override
    public void setHasRoyalJelly(boolean hasRoyalJelly) {
        this.hasRoyalJelly = hasRoyalJelly;
    }
}
