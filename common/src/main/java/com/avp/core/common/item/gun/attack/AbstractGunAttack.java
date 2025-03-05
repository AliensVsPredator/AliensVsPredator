package com.avp.core.common.item.gun.attack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public abstract class AbstractGunAttack {

    protected final GunAttackConfig gunAttackConfig;

    protected AbstractGunAttack(GunAttackConfig gunAttackConfig) {
        this.gunAttackConfig = gunAttackConfig;
    }

    public abstract void shoot();

    public abstract void onBlockHit(BlockPos blockPos, Direction direction);

    public abstract void onEntityHit(Entity hitEntity);
}
