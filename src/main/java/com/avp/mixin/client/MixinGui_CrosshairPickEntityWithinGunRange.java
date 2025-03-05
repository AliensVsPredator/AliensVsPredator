package com.avp.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.common.item.GunItem;
import com.avp.common.util.AVPPredicates;

@Mixin(Gui.class)
public abstract class MixinGui_CrosshairPickEntityWithinGunRange {

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    private void modifyAttackIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        var client = Minecraft.getInstance();
        var player = client.player;

        if (player == null) {
            return;
        }

        var itemStack = player.getMainHandItem();

        if (!(itemStack.getItem() instanceof GunItem gun)) {
            return;
        }

        var hitResult = ProjectileUtil.getHitResultOnViewVector(
            player,
            entity -> entity.getType() == EntityType.END_CRYSTAL || AVPPredicates.isLiving(entity),
            gun.gunConfig().getDefaultFireMode().range()
        );

        if (hitResult instanceof EntityHitResult entityHitResult) {
            Minecraft.getInstance().crosshairPickEntity = entityHitResult.getEntity();
        }
    }
}
