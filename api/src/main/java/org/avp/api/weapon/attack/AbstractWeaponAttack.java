package org.avp.api.weapon.attack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.avp.api.weapon.WeaponItemStack;
import org.avp.api.weapon.data.FireModeData;

public abstract class AbstractWeaponAttack {

    protected final FireModeData fireModeData;

    protected final Level level;

    protected final LivingEntity shooter;

    protected final WeaponItemStack weaponItemStack;

    protected AbstractWeaponAttack(
        WeaponItemStack weaponItemStack,
        LivingEntity shooter
    ) {
        this.fireModeData = weaponItemStack.getOrSetFireMode();
        this.level = shooter.level();
        this.shooter = shooter;
        this.weaponItemStack = weaponItemStack;
    }

    public abstract void shoot();
    public abstract void onBlockHit(BlockPos blockPos, Direction direction);
    public abstract void onEntityHit(Entity hitEntity);
}
