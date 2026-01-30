package com.blib.internal.mixin.azurelib;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.AzAnimatorAccessor;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin_AzBlockEntityAnimatorCache implements AzAnimatorAccessor<Long, BlockEntity> {

    @Unique
    @Nullable
    private AzAnimator<Long, BlockEntity> animator;

    @Override
    public void setAnimator(@Nullable AzAnimator<Long, BlockEntity> animator) {
        this.animator = animator;
    }

    @Override
    public @Nullable AzAnimator<Long, BlockEntity> getAnimatorOrNull() {
        return animator;
    }
}
