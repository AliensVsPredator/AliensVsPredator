package com.avp.core.common.item.gun.attack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public abstract class ProjectileGunAttack extends AbstractGunAttack {

    protected ProjectileGunAttack(GunAttackConfig gunAttackConfig) {
        super(gunAttackConfig);
    }

    @Override
    public void onBlockHit(BlockPos blockPos, Direction direction) {}

    @Override
    public void onEntityHit(Entity hitEntity) {}
}
