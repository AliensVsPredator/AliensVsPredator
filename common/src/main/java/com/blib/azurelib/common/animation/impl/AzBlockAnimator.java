package com.blib.azurelib.common.animation.impl;

import net.minecraft.world.level.block.entity.BlockEntity;

import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.AzAnimatorConfig;

public abstract class AzBlockAnimator<T extends BlockEntity> extends AzAnimator<Long, T> {

    protected AzBlockAnimator(AzAnimatorConfig config) {
        super(config);
    }
}
