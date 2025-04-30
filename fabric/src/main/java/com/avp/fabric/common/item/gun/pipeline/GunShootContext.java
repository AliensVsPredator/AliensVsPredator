package com.avp.fabric.common.item.gun.pipeline;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

import com.avp.fabric.common.component.DataComponents;
import com.avp.fabric.common.item.GunItem;
import com.avp.fabric.common.item.gun.FireModeConfig;
import com.avp.fabric.common.item.gun.GunConfig;
import com.avp.fabric.common.item.gun.attack.GunAttackConfig;
import com.avp.fabric.common.item.gun.pipeline.step.GunShootStep;
import com.avp.fabric.common.item.gun.pipeline.step.impl.CheckCooldownStep;
import com.avp.fabric.common.item.gun.pipeline.step.impl.CheckReloadingStep;
import com.avp.fabric.common.item.gun.pipeline.step.impl.CheckShootDelayStep;
import com.avp.fabric.common.util.AVPPredicates;
import com.avp.fabric.common.util.EnchantmentUtil;
import com.avp.fabric.common.util.GunLightUtil;

public record GunShootContext(
    int currentAmmunition,
    FireModeConfig fireModeConfig,
    GunConfig gunConfig,
    GunItem gunItem,
    boolean hasInfinity,
    boolean isFirstTick,
    boolean isShooterImmortal,
    ItemStack itemStack,
    LivingEntity shooter,
    int tickProgress
) {

    public static Option<GunShootContext> create(
        LivingEntity shooter,
        ItemStack itemStack,
        int tickProgress
    ) {
        return !(itemStack.getItem() instanceof GunItem gunItem)
            ? Option.none()
            : Option.some(new GunShootContext(shooter, gunItem, itemStack, tickProgress));
    }

    // TODO: Maybe the cooldown step should come before the shoot delay step?
    private static final List<GunShootStep> STEPS = List.of(
        CheckShootDelayStep.INSTANCE,
        CheckCooldownStep.INSTANCE,
        CheckReloadingStep.INSTANCE
    );

    public GunShootContext(LivingEntity shooter, GunItem gunItem, ItemStack itemStack, int tickProgress) {
        this(
            itemStack.getOrDefault(DataComponents.AMMUNITION, 0),
            gunItem.getGunConfig().getDefaultFireMode(),
            gunItem.getGunConfig(),
            gunItem,
            EnchantmentUtil.getLevel(shooter.level(), itemStack, Enchantments.INFINITY) > 0,
            tickProgress == 0,
            AVPPredicates.IS_IMMORTAL.test(shooter),
            itemStack,
            shooter,
            tickProgress
        );
    }

    public GunShootResult shoot() {
        for (var step : STEPS) {
            var result = step.apply(this);

            if (result != GunShootResult.CONTINUE) {
                return result;
            }
        }

        var gunAttackConfig = new GunAttackConfig(gunConfig, fireModeConfig, shooter, itemStack);
        var gunAttack = fireModeConfig
            .gunAttackSupplier()
            .apply(gunAttackConfig);

        gunAttack.shoot();

        runPostEffects();

        return GunShootResult.SHOT;
    }

    private void runPostEffects() {
        GunLightUtil.spawnLightSource(shooter);

        consumeAmmunition();

        updateItemStackDamage();

        playSecondaryShootSoundEffect();

        playPrimaryShootSoundEffects();

        if (shooter instanceof Player player) {
            player.getCooldowns().addCooldown(gunItem, fireModeConfig.cooldownInTicks());
        }
    }

    private void consumeAmmunition() {
        if (!isShooterImmortal && !hasInfinity) {
            itemStack.set(DataComponents.AMMUNITION, Math.max(currentAmmunition - fireModeConfig.consumedAmmunitionPerShot(), 0));
        }
    }

    private void updateItemStackDamage() {
        if (!isShooterImmortal) {
            itemStack.hurtAndBreak(1, shooter, EquipmentSlot.MAINHAND);
        }
    }

    private void playSecondaryShootSoundEffect() {
        var level = shooter.level();
        var shootDelayInTicks = fireModeConfig.shootDelayInTicks();
        var secondaryShootSoundFrequencyInTicks = fireModeConfig.secondaryShootSoundFrequencyInTicks();
        var secondaryShootSoundEvent = fireModeConfig.secondaryShootSoundEvent();

        if (
            secondaryShootSoundEvent != null &&
                (tickProgress == shootDelayInTicks || (tickProgress + shootDelayInTicks)
                    % secondaryShootSoundFrequencyInTicks == 0)
        ) {
            level.playSound(null, shooter.blockPosition(), secondaryShootSoundEvent, SoundSource.PLAYERS);
        }
    }

    private void playPrimaryShootSoundEffects() {
        var level = shooter.level();
        var primaryShootSoundFrequencyInTicks = fireModeConfig.primaryShootSoundFrequencyInTicks();

        if (primaryShootSoundFrequencyInTicks <= 0 || tickProgress % primaryShootSoundFrequencyInTicks == 0) {
            level.playSound(null, shooter.blockPosition(), fireModeConfig.primaryShootSoundEvent(), SoundSource.PLAYERS);
        }
    }

}
