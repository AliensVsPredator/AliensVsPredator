package com.avp.core.common.item.gun;

import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.gun.attack.FlamethrowProjectileGunAttack;
import com.avp.core.common.item.gun.attack.RocketProjectileGunAttack;
import com.avp.core.common.sound.AVPSoundEvents;

public class GunData {

    public static final GunConfig F903WE_RIFLE = GunConfig.builder()
        .withDurability(2048)
        .withMaximumAmmunition(32)
        .withReloadTimeInTicks(20 * 3)
        .withAmmunitionItemSupplier(AVPItems.SMALL_BULLET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(2)
                .withDamage(2F * 2)
                .withRange(64)
                .withRecoil(2.5F)
                .build()
        )
        .build();

    public static final GunConfig FLAMETHROWER_SEVASTOPOL = GunConfig.builder()
        .withDurability(4096)
        .withMaximumAmmunition(1000)
        .withReloadAmount(1000)
        .withReloadTimeInTicks(20 * 5)
        .withAmmunitionItemSupplier(AVPItems.FUEL_TANK)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(1)
                .withDamage(1F)
                .withPrimaryShootSoundFrequencyInTicks(7)
                .withRange(16)
                .withReloadFinishSound(AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_FINISH)
                .withReloadStartSound(AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_START)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_SHOOT)
                .withGunAttackSupplier(FlamethrowProjectileGunAttack::new)
                .build()
        )
        .build();

    public static final GunConfig M37_12_SHOTGUN = GunConfig.builder()
        .withDurability(1024)
        .withMaximumAmmunition(6)
        .withReloadTimeInTicks(20 * 4)
        .withAmmunitionItemSupplier(AVPItems.SHOTGUN_BULLET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(20)
                .withDamage(8F * 2)
                .withKnockback(0.75F)
                .withRange(6)
                .withRecoil(8F)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_M37_12_SHOTGUN_SHOOT)
                .build()
        )
        .build();

    public static final GunConfig M41A_PULSE_RIFLE = GunConfig.builder()
        .withDurability(2048 + 1024)
        .withMaximumAmmunition(99)
        .withReloadTimeInTicks(20 * 3)
        .withAmmunitionItemSupplier(AVPItems.CASELESS_BULLET)
        // Burst
        .withFireMode(
            FireModeConfig.builder()
                .withConsumedAmmunitionPerShot(4)
                .withCooldownInTicks(10)
                .withDamage(2F * 2 * 4) // 4 bullets per shot in burst mode
                .withRange(64)
                .withRecoil(2.25F)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_M41A_PULSE_RIFLE_SHOOT)
                .build()
        )
        .build();

    public static final GunConfig M42A3_SNIPER_RIFLE = GunConfig.builder()
        .withDurability(1024)
        .withMaximumAmmunition(6)
        .withReloadTimeInTicks(20 * 7 + 10)
        .withAmmunitionItemSupplier(AVPItems.HEAVY_BULLET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(30)
                .withDamage(15F * 2)
                .withRange(128)
                .withRecoil(2.25F)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_M42A3_SNIPER_RIFLE_SHOOT)
                .build()
        )
        .build();

    public static final GunConfig M4RA_BATTLE_RIFLE = GunConfig.builder()
        .withDurability(2048)
        .withMaximumAmmunition(90)
        .withReloadTimeInTicks(20 * 4)
        .withAmmunitionItemSupplier(AVPItems.MEDIUM_BULLET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(2)
                .withDamage(2F * 2)
                .withRange(64)
                .withRecoil(1.25F)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_M4RA_BATTLE_RIFLE_SHOOT)
                .build()
        )
        .build();

    public static final GunConfig M56_SMARTGUN = GunConfig.builder()
        .withDurability(4096)
        .withMaximumAmmunition(500)
        .withReloadTimeInTicks(20 * 7)
        .withAmmunitionItemSupplier(AVPItems.CASELESS_BULLET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(1)
                .withDamage(2F * 2)
                .withRange(64)
                .withRecoil(1F)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_M56_SMARTGUN_SHOOT)
                .build()
        )
        .build();

    public static final GunConfig M6B_ROCKET_LAUNCHER = GunConfig.builder()
        .withDurability(512)
        .withMaximumAmmunition(4)
        .withReloadTimeInTicks(20 * 4)
        .withAmmunitionItemSupplier(AVPItems.ROCKET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(60)
                .withDamage(8F * 2)
                .withRange(100)
                .withRecoil(12F)
                .withReloadFinishSound(AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_FINISH)
                .withReloadStartSound(AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_START)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_SHOOT)
                .withGunAttackSupplier(RocketProjectileGunAttack::new)
                .build()
        )
        .build();

    public static final GunConfig M88_MOD_4_COMBAT_PISTOL = GunConfig.builder()
        .withDurability(1024)
        .withMaximumAmmunition(18)
        .withReloadTimeInTicks(10 * 2 + 10)
        .withAmmunitionItemSupplier(AVPItems.SMALL_BULLET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(6)
                .withDamage(5F * 1)
                .withRange(32)
                .withRecoil(0.35F)
                .withReloadStartSound(AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_RELOAD)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_SHOOT)
                .build()
        )
        .build();

    public static final GunConfig OLD_PAINLESS = GunConfig.builder()
        .withDurability(4096)
        .withMaximumAmmunition(Integer.MAX_VALUE)
        .withReloadTimeInTicks(0)
        .withAmmunitionItemSupplier(AVPItems.HEAVY_BULLET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(1)
                .withDamage(2F * 2)
                .withRange(64)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT)
                .withPrimaryShootSoundFrequencyInTicks(10)
                .withSecondaryShootSound(AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_SPINNING)
                .withSecondaryShootSoundFrequencyInTicks(30)
                .withShootDelayInTicks(20)
                .withShootFinishSound(AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_FINISH)
                .withShootStartSound(AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_START)
                .build()
        )
        .build();

    public static final GunConfig ZX_76_SHOTGUN = GunConfig.builder()
        .withDurability(1024)
        .withMaximumAmmunition(12)
        .withReloadTimeInTicks(20 * 6)
        .withAmmunitionItemSupplier(AVPItems.SHOTGUN_BULLET)
        .withFireMode(
            FireModeConfig.builder()
                .withCooldownInTicks(20)
                .withDamage(8F * 2)
                .withKnockback(0.75F)
                .withRange(6)
                .withRecoil(8F)
                .withPrimaryShootSound(AVPSoundEvents.WEAPON_ZX_76_SHOTGUN_SHOOT)
                .build()
        )
        .build();
}
