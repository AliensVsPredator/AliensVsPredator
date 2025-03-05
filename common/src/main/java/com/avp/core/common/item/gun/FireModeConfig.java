package com.avp.core.common.item.gun;

import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.core.common.item.gun.attack.AbstractGunAttack;
import com.avp.core.common.item.gun.attack.GunAttackConfig;
import com.avp.core.common.item.gun.attack.HitScanGunAttack;
import com.avp.core.common.sound.AVPSoundEvents;

public record FireModeConfig(
    int consumedAmmunitionPerShot,
    int cooldownInTicks,
    float damage,
    float knockback,
    Supplier<SoundEvent> primaryShootSoundEvent,
    int primaryShootSoundFrequencyInTicks,
    int range,
    float recoil,
    Supplier<SoundEvent> reloadFinishSoundEvent,
    Supplier<SoundEvent> reloadStartSoundEvent,
    @Nullable Supplier<SoundEvent> secondaryShootSoundEvent,
    int secondaryShootSoundFrequencyInTicks,
    int shootDelayInTicks,
    @Nullable Supplier<SoundEvent> shootFinishSoundEvent,
    @Nullable Supplier<SoundEvent> shootStartSoundEvent,
    Function<GunAttackConfig, AbstractGunAttack> gunAttackSupplier
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int consumedAmmunitionPerShot;

        private int cooldownInTicks;

        private float damage;

        private float knockback;

        private Supplier<SoundEvent> primaryShootSoundEvent;

        private int primaryShootSoundFrequencyInTicks;

        private int range;

        private float recoil;

        private Supplier<SoundEvent> reloadFinishSoundEvent;

        private Supplier<SoundEvent> reloadStartSoundEvent;

        private Supplier<SoundEvent> secondaryShootSoundEvent;

        private int secondaryShootSoundFrequencyInTicks;

        private int shootDelayInTicks;

        private Supplier<SoundEvent> shootFinishSoundEvent;

        private Supplier<SoundEvent> shootStartSoundEvent;

        private Function<GunAttackConfig, AbstractGunAttack> gunAttackSupplier;

        private Builder() {
            this.consumedAmmunitionPerShot = 1;
            this.cooldownInTicks = 0;
            this.damage = 1F;
            this.knockback = 0F;
            this.primaryShootSoundEvent = AVPSoundEvents.WEAPON_GENERIC_SHOOT;
            this.primaryShootSoundFrequencyInTicks = 0;
            this.range = 16;
            this.recoil = 0F;
            this.reloadFinishSoundEvent = AVPSoundEvents.WEAPON_GENERIC_RELOAD;
            this.reloadStartSoundEvent = AVPSoundEvents.WEAPON_GENERIC_RELOAD;
            this.secondaryShootSoundFrequencyInTicks = 0;
            this.shootDelayInTicks = 0;
            this.gunAttackSupplier = HitScanGunAttack::new;
        }

        public Builder withConsumedAmmunitionPerShot(int consumedAmmunitionPerShot) {
            this.consumedAmmunitionPerShot = consumedAmmunitionPerShot;
            return this;
        }

        public Builder withCooldownInTicks(int cooldownInTicks) {
            this.cooldownInTicks = cooldownInTicks;
            return this;
        }

        public Builder withDamage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder withKnockback(float knockback) {
            this.knockback = knockback;
            return this;
        }

        public Builder withPrimaryShootSound(Supplier<SoundEvent> primaryShootSoundEvent) {
            this.primaryShootSoundEvent = primaryShootSoundEvent;
            return this;
        }

        public Builder withPrimaryShootSoundFrequencyInTicks(int primaryShootSoundFrequencyInTicks) {
            this.primaryShootSoundFrequencyInTicks = primaryShootSoundFrequencyInTicks;
            return this;
        }

        public Builder withRange(int range) {
            this.range = range;
            return this;
        }

        public Builder withRecoil(float recoil) {
            this.recoil = recoil;
            return this;
        }

        public Builder withReloadFinishSound(Supplier<SoundEvent> reloadFinishSoundEvent) {
            this.reloadFinishSoundEvent = reloadFinishSoundEvent;
            return this;
        }

        public Builder withReloadStartSound(Supplier<SoundEvent> reloadStartSoundEvent) {
            this.reloadStartSoundEvent = reloadStartSoundEvent;
            return this;
        }

        public Builder withSecondaryShootSoundFrequencyInTicks(int secondaryShootSoundFrequencyInTicks) {
            this.secondaryShootSoundFrequencyInTicks = secondaryShootSoundFrequencyInTicks;
            return this;
        }

        public Builder withSecondaryShootSound(Supplier<SoundEvent> secondaryShootSoundEvent) {
            this.secondaryShootSoundEvent = secondaryShootSoundEvent;
            return this;
        }

        public Builder withShootDelayInTicks(int shootDelayInTicks) {
            this.shootDelayInTicks = shootDelayInTicks;
            return this;
        }

        public Builder withShootFinishSound(Supplier<SoundEvent> shootFinishSoundEvent) {
            this.shootFinishSoundEvent = shootFinishSoundEvent;
            return this;
        }

        public Builder withShootStartSound(Supplier<SoundEvent> shootStartSoundEvent) {
            this.shootStartSoundEvent = shootStartSoundEvent;
            return this;
        }

        public Builder withGunAttackSupplier(Function<GunAttackConfig, AbstractGunAttack> gunAttackSupplier) {
            this.gunAttackSupplier = gunAttackSupplier;
            return this;
        }

        public FireModeConfig build() {
            return new FireModeConfig(
                consumedAmmunitionPerShot,
                cooldownInTicks,
                damage,
                knockback,
                primaryShootSoundEvent,
                primaryShootSoundFrequencyInTicks,
                range,
                recoil,
                reloadFinishSoundEvent,
                reloadStartSoundEvent,
                secondaryShootSoundEvent,
                secondaryShootSoundFrequencyInTicks,
                shootDelayInTicks,
                shootFinishSoundEvent,
                shootStartSoundEvent,
                gunAttackSupplier
            );
        }
    }
}
