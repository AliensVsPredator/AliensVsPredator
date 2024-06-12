package org.avp.api.weapon.data;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.weapon.WeaponItemStack;
import org.avp.api.weapon.attack.AbstractWeaponAttack;

import java.util.function.BiFunction;

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
