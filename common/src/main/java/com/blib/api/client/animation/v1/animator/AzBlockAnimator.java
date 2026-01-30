package com.blib.api.client.animation.v1.animator;

import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AzBlockAnimator<T extends BlockEntity> extends AzAnimator<Long, T> {

    protected AzBlockAnimator(AzAnimatorConfig config) {
        super(config);
    }
}
