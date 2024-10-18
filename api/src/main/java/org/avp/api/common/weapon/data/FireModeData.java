package org.avp.api.common.weapon.data;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;

import org.avp.api.common.weapon.WeaponItemStack;
import org.avp.api.common.weapon.attack.AbstractWeaponAttack;

public record FireModeData(
    String identifier,
    float accuracy,
    float damage,
    int fireRateInTicks,
    float knockback,
    int range,
    float recoil,
    AmmunitionData ammunitionData,
    ReloadData reloadData,
    ShootData shootData,
    WindData windData,
    BiFunction<WeaponItemStack, LivingEntity, AbstractWeaponAttack> weaponAttackProvider
) {

    public AbstractWeaponAttack createWeaponAttack(WeaponItemStack weaponItemStack, LivingEntity shooter) {
        return weaponAttackProvider.apply(weaponItemStack, shooter);
    }

    public float getTotalDamage() {
        return damage * ammunitionData.consumedAmmunition();
    }

    public float getTotalKnockback() {
        return knockback * ammunitionData.consumedAmmunition();
    }
}
