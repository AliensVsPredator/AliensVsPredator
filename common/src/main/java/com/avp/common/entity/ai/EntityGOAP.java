package com.avp.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;

import com.avp.common.goap.GOAP;

public abstract class EntityGOAP<T extends LivingEntity> extends GOAP<T> {

    @Override
    public void update(T context) {
        if (!context.isAlive() || context.isDeadOrDying()) {
            return;
        }

        super.update(context);
    }
}
