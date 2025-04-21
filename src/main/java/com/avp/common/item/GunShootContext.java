package com.avp.common.item;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import com.avp.common.component.DataComponents;
import com.avp.common.item.gun.FireModeConfig;
import com.avp.common.item.gun.GunConfig;
import com.avp.common.item.gun.attack.GunAttackConfig;
import com.avp.common.util.AVPPredicates;
import com.avp.common.util.EnchantmentUtil;
import com.avp.common.util.GunLightUtil;

public final class GunShootContext {

    public static Option<GunShootContext> create(
        LivingEntity shooter,
        ItemStack itemStack,
        int tickProgress
    ) {
        return !(itemStack.getItem() instanceof GunItem gunItem)
            ? Option.none()
            : Option.some(new GunShootContext(shooter, gunItem, itemStack, tickProgress));
    }

    private final int currentAmmunition;

    private final FireModeConfig fireModeConfig;

    private final GunConfig gunConfig;

    private final GunItem gunItem;

    private final boolean hasInfinity;

    private final boolean isFirstTick;

    private final boolean isShooterImmortal;

    private final ItemStack itemStack;

    private final LivingEntity shooter;

    private final int tickProgress;

    private GunShootContext(LivingEntity shooter, GunItem gunItem, ItemStack itemStack, int tickProgress) {
        this.currentAmmunition = itemStack.getOrDefault(DataComponents.AMMUNITION, 0);
        this.fireModeConfig = gunItem.getGunConfig().getDefaultFireMode();
        this.gunConfig = gunItem.getGunConfig();
        this.gunItem = gunItem;
        this.hasInfinity = EnchantmentUtil.getLevel(shooter.level(), itemStack, Enchantments.INFINITY) > 0;
        this.isFirstTick = tickProgress == 0;
        this.isShooterImmortal = AVPPredicates.IS_IMMORTAL.test(shooter);
        this.itemStack = itemStack;
        this.shooter = shooter;
        this.tickProgress = tickProgress;
    }

    public Result shoot() {
        var shootDelayInTicks = fireModeConfig.shootDelayInTicks();

        playShootStartSoundEffect();

        if (tickProgress < shootDelayInTicks) {
            return Result.DELAYED;
        }

        if (shooter instanceof Player player && player.getCooldowns().isOnCooldown(itemStack.getItem())) {
            return Result.COOLDOWN;
        }

        // TODO: Revisit this.
        var supplier = gunConfig.ammunitionItemSupplier();
        var hasAmmunitionStream = gunItem == AVPItems.OLD_PAINLESS
            && supplier != null
            && shooter instanceof ServerPlayer serverPlayer
            && GunReloading.consumeItemAmountFromInventory(serverPlayer, supplier.get(), 1) == GunReloading.ItemConsumptionResult.Full.INSTANCE;
        var hasAmmunition = hasAmmunitionStream || currentAmmunition > 0;

        if (shooter instanceof Player && !isShooterImmortal && !hasInfinity && currentAmmunition <= 0 && !hasAmmunition) {
            GunReloading.reload((ServerPlayer) shooter);
            return Result.RELOADING;
        }

        var gunAttackConfig = new GunAttackConfig(gunConfig, fireModeConfig, shooter, itemStack);
        var gunAttack = fireModeConfig
            .gunAttackSupplier()
            .apply(gunAttackConfig);

        gunAttack.shoot();

        runPostEffects();

        return Result.SHOT;
    }

    private void playShootStartSoundEffect() {
        var shootStartSoundEvent = fireModeConfig.shootStartSoundEvent();

        if (shootStartSoundEvent != null && isFirstTick) {
            shooter.level().playSound(null, shooter.blockPosition(), shootStartSoundEvent, SoundSource.PLAYERS);
        }
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

    public enum Result {
        COOLDOWN,
        DELAYED,
        RELOADING,
        SHOT
    }
}
