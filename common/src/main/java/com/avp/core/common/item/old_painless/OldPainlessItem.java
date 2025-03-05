package com.avp.core.common.item.old_painless;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import com.avp.core.common.item.GunItem;
import com.avp.core.common.item.gun.FireModeConfig;
import com.avp.core.common.item.gun.GunData;
import com.avp.core.common.item.gun.attack.GunAttackConfig;
import com.avp.core.common.util.EnchantmentUtil;
import com.avp.core.common.util.GunLightUtil;

public class OldPainlessItem extends GunItem {

    private final AzCommand idle;

    private final AzCommand spin;

    public OldPainlessItem() {
        super(GunData.OLD_PAINLESS);
        idle = AzCommand.create(
            OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
            OldPainlessAnimationRefs.IDLE_ANIMATION_NAME,
            AzPlayBehaviors.LOOP
        );
        spin = AzCommand.create(
            OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
            OldPainlessAnimationRefs.SPIN_ANIMATION_NAME,
            AzPlayBehaviors.LOOP
        );
    }

    @Override
    protected void tryShoot(
        Level level,
        ItemStack itemStack,
        Player player,
        FireModeConfig fireModeConfig,
        int positiveTickProgress,
        int shootDelayInTicks,
        int secondaryShootSoundFrequencyInTicks,
        int primaryShootSoundFrequencyInTicks,
        int tickProgress
    ) {
        var hasInfinity = EnchantmentUtil.getLevel(level, itemStack, Enchantments.INFINITY) > 0;
        var isPlayerCreative = player.isCreative() || player.isSpectator();
        var didConsume = isPlayerCreative || hasInfinity || consumeItemAmountFromInventory(
            1,
            player.getInventory(),
            gunConfig.ammunitionItemSupplier().get()
        );

        if (didConsume) {
            var gunAttackConfig = new GunAttackConfig(gunConfig, fireModeConfig, player, itemStack);
            var gunAttack = fireModeConfig
                .gunAttackSupplier()
                .apply(gunAttackConfig);

            gunAttack.shoot();

            itemStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

            if (primaryShootSoundFrequencyInTicks <= 0 || tickProgress % primaryShootSoundFrequencyInTicks == 0) {
                level.playSound(null, player.blockPosition(), fireModeConfig.primaryShootSoundEvent().get(), SoundSource.PLAYERS);
            }

            GunLightUtil.spawnLightSource(player);
        }

        var secondaryShootSoundEvent = fireModeConfig.secondaryShootSoundEvent();

        if (
            secondaryShootSoundEvent != null &&
                (positiveTickProgress == shootDelayInTicks || (positiveTickProgress + shootDelayInTicks)
                    % secondaryShootSoundFrequencyInTicks == 0)
        ) {
            level.playSound(null, player.blockPosition(), secondaryShootSoundEvent.get(), SoundSource.PLAYERS);
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
