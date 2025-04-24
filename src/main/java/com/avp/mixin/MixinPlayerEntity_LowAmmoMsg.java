package com.avp.mixin;

import com.avp.common.component.DataComponents;
import com.avp.common.item.GunItem;
import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayerEntity_LowAmmoMsg extends LivingEntity {

    private long lastUpdateTime = 0L;

    protected MixinPlayerEntity_LowAmmoMsg(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = { "tick" }, at = { @At("HEAD") })
    public void tellPlayer(CallbackInfo ci) {
        var self = Player.class.cast(this);
        var mainHandItem = self.getMainHandItem();
        if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof GunItem gunItem) {
            var player = ClientUtils.getClientPlayer();
            var currentAmmo = mainHandItem.getOrDefault(DataComponents.AMMUNITION, 0);
            var maxAmmo = gunItem.getGunConfig().maximumAmmunition();
            if (player != null && currentAmmo == 0) {
                var currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdateTime >= 1000L) {
                    lastUpdateTime = currentTime;
                    player.displayClientMessage(Component.translatable("tooltip.avp.no_ammo_warning").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC), true);
                }
            }
            if (player != null && currentAmmo < maxAmmo * 0.1) {
                var currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdateTime >= 1000L) {
                    lastUpdateTime = currentTime;
                    player.displayClientMessage(Component.translatable("tooltip.avp.low_ammo_warning").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC), true);
                }
            }
        }
    }
}
