package com.blib.internal.mixin.azurelib;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.AzAnimatorAccessor;

@Mixin(Entity.class)
public abstract class EntityMixin_AzEntityAnimatorCache implements AzAnimatorAccessor<UUID, Entity> {

    @Unique
    @Nullable
    private AzAnimator<UUID, Entity> animator;

    @Override
    public void setAnimator(@Nullable AzAnimator<UUID, Entity> animator) {
        this.animator = animator;
    }

    @Override
    public @Nullable AzAnimator<UUID, Entity> getAnimatorOrNull() {
        return animator;
    }
}
