package com.avp.fabric.mixin.client;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.common.util.AmmunitionIndicatorUtil;
import com.avp.fabric.common.component.DataComponents;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.item.GunItem;
import com.avp.fabric.common.util.AVPPredicates;

@Mixin(Player.class)
public abstract class MixinPlayerEntity_LowAmmunitionIndicator extends LivingEntity {

    @Unique
    private long lastUpdateTimeMillis = 0L;

    @Unique
    private AmmunitionIndicatorUtil.DisplayState currentDisplayState = AmmunitionIndicatorUtil.DisplayState.NOTHING;

    protected MixinPlayerEntity_LowAmmunitionIndicator(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = { "tick" }, at = { @At("HEAD") })
    public void tellPlayer(CallbackInfo ci) {
        var self = Player.class.cast(this);
        var mainHandItem = self.getMainHandItem();

        if (
            // Don't try to show ammo indicator server-side.
            !self.level().isClientSide
                // Don't show ammo indicator for creative/spectator players.
                || AVPPredicates.IS_IMMORTAL.test(self)
                // Don't show ammo indicator if the player is not holding an item.
                || mainHandItem.isEmpty()
                // Don't show ammo indicator if the player is not holding a gun.
                || !(mainHandItem.getItem() instanceof GunItem gunItem)
                // Don't show ammo indicator for weapons that do not store ammunition in their item stack.
                || gunItem == AVPItems.OLD_PAINLESS
        ) {
            return;
        }

        displayAmmunitionIndicator(gunItem, mainHandItem, self);
    }

    @Unique
    private void displayAmmunitionIndicator(GunItem gunItem, ItemStack mainHandItem, Player player) {
        int currentAmmunition = mainHandItem.getOrDefault(DataComponents.AMMUNITION, 0);
        var maximumAmmunition = gunItem.getGunConfig().maximumAmmunition();

        // Store the current display state for comparison later on.
        var previousDisplayState = currentDisplayState;

        // Update the current display state based on the user's weapon's ammo state.
        setDisplayStateForAmmunitionState(currentAmmunition, maximumAmmunition);

        var currentTime = System.currentTimeMillis();
        // If the current state does not match the previous state, then we can update immediately.
        // Otherwise, we can update after 1 second has elapsed. We update every 1 second because displays fade over
        // time.
        var shouldUpdate = currentDisplayState != previousDisplayState || currentTime - lastUpdateTimeMillis >= 1000L;

        if (!shouldUpdate) {
            return;
        }

        this.lastUpdateTimeMillis = currentTime;

        player.displayClientMessage(AmmunitionIndicatorUtil.DISPLAY_STATE_COMPONENT_MAP.get(currentDisplayState), true);
    }

    @Unique
    private void setDisplayStateForAmmunitionState(int currentAmmunition, int maximumAmmunition) {
        if (currentAmmunition == 0) {
            this.currentDisplayState = AmmunitionIndicatorUtil.DisplayState.NO_AMMUNITION;
        } else if (currentAmmunition < maximumAmmunition * 0.2) {
            this.currentDisplayState = AmmunitionIndicatorUtil.DisplayState.LOW_AMMUNITION;
        } else {
            this.currentDisplayState = AmmunitionIndicatorUtil.DisplayState.NOTHING;
        }
    }
}
