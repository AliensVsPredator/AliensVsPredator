package org.avp.common.item.weapon.attack;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.item.weapon.WeaponItemStack;
import org.avp.api.item.weapon.attack.ProjectileWeaponAttack;

public class FlamethrowerProjectileWeaponAttack extends ProjectileWeaponAttack {
    public FlamethrowerProjectileWeaponAttack(WeaponItemStack weaponItemStack, LivingEntity shooter) {
        super(weaponItemStack, shooter);
    }
}
