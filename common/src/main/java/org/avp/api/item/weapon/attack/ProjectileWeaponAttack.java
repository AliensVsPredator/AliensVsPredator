package org.avp.api.item.weapon.attack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.avp.api.item.weapon.WeaponItemStack;

public class ProjectileWeaponAttack extends AbstractWeaponAttack {

    protected ProjectileWeaponAttack(WeaponItemStack weaponItemStack, LivingEntity shooter) {
        super(weaponItemStack, shooter);
    }

    @Override
    public void shoot() {

    }

    @Override
    public void onBlockHit(BlockPos blockPos, Direction direction) {

    }

    @Override
    public void onEntityHit(Entity hitEntity) {

    }
}
