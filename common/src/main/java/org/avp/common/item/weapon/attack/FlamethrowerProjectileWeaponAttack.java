package org.avp.common.item.weapon.attack;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.item.weapon.WeaponItemStack;
import org.avp.api.item.weapon.attack.ProjectileWeaponAttack;
import org.avp.common.entity.AVPThrownGrenade;

public class FlamethrowerProjectileWeaponAttack extends ProjectileWeaponAttack {
    public FlamethrowerProjectileWeaponAttack(WeaponItemStack weaponItemStack, LivingEntity shooter) {
        super(weaponItemStack, shooter);
    }

    @Override
    public void shoot() {
        var thrownGrenade = new AVPThrownGrenade(level, shooter);
        thrownGrenade.setItem(weaponItemStack.getItemStack());
        thrownGrenade.setIncendiary(false);
        thrownGrenade.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(thrownGrenade);
    }
}
