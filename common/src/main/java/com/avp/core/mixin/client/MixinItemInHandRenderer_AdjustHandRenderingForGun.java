package com.avp.core.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.core.common.item.GunItem;

@Mixin(value = ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer_AdjustHandRenderingForGun {

    @Mutable
    @Shadow
    @Final
    private final Minecraft minecraft;

    @Shadow
    private float mainHandHeight;

    @Shadow
    private float offHandHeight;

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private ItemStack offHandItem;

    protected MixinItemInHandRenderer_AdjustHandRenderingForGun(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void cancelAnimation(CallbackInfo ci) {
        var clientPlayerEntity = this.minecraft.player;
        assert clientPlayerEntity != null;
        var itemStack = clientPlayerEntity.getMainHandItem();
        var itemStack2 = clientPlayerEntity.getOffhandItem();
        if ((this.mainHandItem.getItem() instanceof GunItem) && ItemStack.isSameItem(mainHandItem, itemStack)) {
            this.mainHandHeight = 1;
            this.mainHandItem = itemStack;
        }
        if ((this.offHandItem.getItem() instanceof GunItem) && ItemStack.isSameItem(offHandItem, itemStack2)) {
            this.offHandHeight = 1;
            this.offHandItem = itemStack2;
        }
    }
}
