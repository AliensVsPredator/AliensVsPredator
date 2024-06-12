package org.avp.common.game.item.weapon;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.weapon.WeaponItemStack;
import org.avp.api.weapon.attack.ProjectileWeaponAttack;
import org.avp.common.game.entity.Rocket;

public class M83A2SADARProjectileWeaponAttack extends ProjectileWeaponAttack {
    public M83A2SADARProjectileWeaponAttack(WeaponItemStack weaponItemStack, LivingEntity shooter) {
        super(weaponItemStack, shooter);
    }

    @Override
    public void shoot() {
        var rocket = new Rocket(level, shooter);
        rocket.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(rocket);
    }
}
