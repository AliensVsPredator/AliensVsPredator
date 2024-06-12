package org.avp.common.game.item.weapon;

import net.minecraft.world.entity.LivingEntity;

import org.avp.api.common.weapon.WeaponItemStack;
import org.avp.api.common.weapon.attack.ProjectileWeaponAttack;
import org.avp.common.game.entity.ThrownGrenade;

public class FlamethrowerProjectileWeaponAttack extends ProjectileWeaponAttack {

    public FlamethrowerProjectileWeaponAttack(WeaponItemStack weaponItemStack, LivingEntity shooter) {
        super(weaponItemStack, shooter);
    }

    @Override
    public void shoot() {
        var thrownGrenade = new ThrownGrenade(level, shooter);
        thrownGrenade.setItem(weaponItemStack.getItemStack());
        thrownGrenade.setIncendiary(false);
        thrownGrenade.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(thrownGrenade);
    }
}
