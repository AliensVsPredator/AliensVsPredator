package com.human.common.gameplay.item.old_painless;

import com.human.common.gameplay.item.GunItem;
import com.human.common.gameplay.item.gun.GunData;
import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
        this.idle = AzCommand.create(
            OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
            OldPainlessAnimationRefs.IDLE_ANIMATION_NAME,
            AzPlayBehaviors.LOOP
        );
        this.shoot = AzCommand.compose(spinUp, spin);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, int tickCountdown) {
        var tickProgress = Math.abs(START_TICK_PROGRESS - tickCountdown);
        var isFirstTick = tickProgress == 0;

        if (isFirstTick) {
            spinUp.sendForItem(livingEntity, itemStack);
        }

        super.onUseTick(level, livingEntity, itemStack, tickCountdown);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity, int i) {
        var fireModeConfig = getGunConfig().getDefaultFireMode();
        var shootFinishSoundEvent = fireModeConfig.shootFinishSoundEvent();

        if (shootFinishSoundEvent != null) {
            spinDown.sendForItem(livingEntity, itemStack);
        }

        super.releaseUsing(itemStack, level, livingEntity, i);
    }

    @Override
    protected void playUseAnimations(Entity shooter, ItemStack itemStack) {
        spin.sendForItem(shooter, itemStack);
    }
}
