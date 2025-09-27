package com.avp.mixin;

import com.just.goap.GOAP;
import com.lib.common.gameplay.goap.GOAPUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_GOAPUser extends Entity implements GOAPUser<Entity> {

    @Unique
    private GOAP<Entity> goap;

    public MixinLivingEntity_GOAPUser(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.goap = createGOAP();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        if (!level().isClientSide && goap != null) {
            goap.update(this);
        }
    }

    @Override
    public @Nullable GOAP<Entity> createGOAP() {
        return null;
    }

    @Override
    public @Nullable GOAP<Entity> getGOAPOrNull() {
        return goap;
    }
}
