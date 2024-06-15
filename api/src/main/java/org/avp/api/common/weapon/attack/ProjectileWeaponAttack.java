package org.avp.api.common.weapon.attack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.avp.api.common.weapon.WeaponItemStack;

public abstract class ProjectileWeaponAttack extends AbstractWeaponAttack {

    protected ProjectileWeaponAttack(WeaponItemStack weaponItemStack, LivingEntity shooter) {
        super(weaponItemStack, shooter);
    }

    @Override
    public void onBlockHit(BlockPos blockPos, Direction direction) {}

    @Override
    public void onEntityHit(Entity hitEntity) {}
}
