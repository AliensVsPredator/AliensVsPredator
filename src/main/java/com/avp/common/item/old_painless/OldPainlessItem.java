package com.avp.common.item.old_painless;

import com.avp.common.util.AVPPredicates;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import com.avp.common.item.GunItem;
import com.avp.common.item.gun.FireModeConfig;
import com.avp.common.item.gun.GunData;
import com.avp.common.item.gun.attack.GunAttackConfig;
import com.avp.common.util.EnchantmentUtil;
import com.avp.common.util.GunLightUtil;

public class OldPainlessItem extends GunItem {

    private final AzCommand spinUp = AzCommand.create(
        OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
        OldPainlessAnimationRefs.SPIN_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private final AzCommand spinDown = AzCommand.create(
        OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
        OldPainlessAnimationRefs.SPIN_DOWN_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private final AzCommand spin = AzCommand.create(
        OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
        OldPainlessAnimationRefs.SPIN_LOOP_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    public OldPainlessItem() {
        super(GunData.OLD_PAINLESS);
        idle = AzCommand.create(
            OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
            OldPainlessAnimationRefs.IDLE_ANIMATION_NAME,
            AzPlayBehaviors.LOOP
        );
        shoot = AzCommand.compose(spinUp, spin);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int tickCountdown) {
        var tickProgress = START_TICK_PROGRESS - tickCountdown;
        var positiveTickProgress = Math.abs(tickProgress);
        var isFirstTick = positiveTickProgress == 0;
        var fireModeConfig = gunConfig.getDefaultFireMode();
        var shootStartSoundEvent = fireModeConfig.shootStartSoundEvent();
        if (shootStartSoundEvent != null && isFirstTick) {
            spinUp.sendForItem(livingEntity, itemStack);
        }
        super.onUseTick(level, livingEntity, itemStack, tickCountdown);
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        var fireModeConfig = gunConfig.getDefaultFireMode();
        var shootFinishSoundEvent = fireModeConfig.shootFinishSoundEvent();

        if (shootFinishSoundEvent != null) {
            spinDown.sendForItem(livingEntity, itemStack);
        }
        super.releaseUsing(itemStack, level, livingEntity, i);
    }

    @Override
    public void tryShoot(
        LivingEntity shooter,
        ItemStack itemStack,
        FireModeConfig fireModeConfig,
        int positiveTickProgress,
        int tickProgress
    ) {
        var level = shooter.level();
        var shootDelayInTicks = fireModeConfig.shootDelayInTicks();
        var primaryShootSoundFrequencyInTicks = fireModeConfig.primaryShootSoundFrequencyInTicks();
        var secondaryShootSoundFrequencyInTicks = fireModeConfig.secondaryShootSoundFrequencyInTicks();
        var hasInfinity = EnchantmentUtil.getLevel(level, itemStack, Enchantments.INFINITY) > 0;

        // TODO: Revisit this.
        var didConsume = shooter instanceof Player player && (AVPPredicates.IS_IMMORTAL.test(player) || hasInfinity || consumeItemAmountFromInventory(
            1,
            player.getInventory(),
            gunConfig.ammunitionItemSupplier().get(),
            player
        ));

        if (didConsume) {
            var gunAttackConfig = new GunAttackConfig(gunConfig, fireModeConfig, shooter, itemStack);
            var gunAttack = fireModeConfig
                .gunAttackSupplier()
                .apply(gunAttackConfig);

            playUseAnimations(shooter, itemStack);
            gunAttack.shoot();

            itemStack.hurtAndBreak(1, shooter, EquipmentSlot.MAINHAND);

            if (primaryShootSoundFrequencyInTicks <= 0 || tickProgress % primaryShootSoundFrequencyInTicks == 0) {
                level.playSound(null, shooter.blockPosition(), fireModeConfig.primaryShootSoundEvent(), SoundSource.PLAYERS);
            }

            GunLightUtil.spawnLightSource(shooter);
        }

        var secondaryShootSoundEvent = fireModeConfig.secondaryShootSoundEvent();

        if (
            secondaryShootSoundEvent != null &&
                (positiveTickProgress == shootDelayInTicks || (positiveTickProgress + shootDelayInTicks)
                    % secondaryShootSoundFrequencyInTicks == 0)
        ) {
            level.playSound(null, shooter.blockPosition(), secondaryShootSoundEvent, SoundSource.PLAYERS);
        }
    }

    @Override
    protected void playReleaseUsingAnimations(Entity shooter, ItemStack itemStack) {
        idle.sendForItem(shooter, itemStack);
    }

    @Override
    protected void playUseAnimations(Entity shooter, ItemStack itemStack) {
        spin.sendForItem(shooter, itemStack);
    }
}
