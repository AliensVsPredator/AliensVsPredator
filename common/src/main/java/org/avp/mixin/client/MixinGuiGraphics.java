package org.avp.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.avp.api.item.weapon.WeaponItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import org.avp.common.game.item.AbstractAVPWeaponItem;
import org.avp.api.util.TypeUtil;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {

    @Inject(
        at = @At(
            value = "HEAD",
            target = "net/minecraft/client/gui/GuiGraphics.renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V"
        ),
        method = "net/minecraft/client/gui/GuiGraphics.renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
        cancellable = true
    )
    public void renderItem(
        @Nullable LivingEntity livingEntity,
        @Nullable Level level,
        ItemStack itemStack,
        int x,
        int y,
        int $0,
        int $1,
        CallbackInfo callbackInfo
    ) {
        if (itemStack.isEmpty())
            return;

        var item = itemStack.getItem();

        if (!(item instanceof AbstractAVPWeaponItem weaponItem))
            return;

        if (!itemStack.hasTag())
            return;

        var self = TypeUtil.<GuiGraphics>self(this);
        var weaponData = weaponItem.getWeaponData();
        var weaponItemStack = new WeaponItemStack(itemStack, weaponData);
        var bulletEffects = weaponItemStack.getBulletEffects();

        if (bulletEffects == null)
            return;

        bulletEffects.stream()
            .findFirst()
            .ifPresent(bulletEffect -> self.renderOutline(x, y, 16, 16, bulletEffect.getColor()));
    }
}
