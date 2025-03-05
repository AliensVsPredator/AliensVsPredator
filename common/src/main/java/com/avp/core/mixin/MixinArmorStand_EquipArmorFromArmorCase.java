package com.avp.core.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorCaseItem;

@Mixin(ArmorStand.class)
public abstract class MixinArmorStand_EquipArmorFromArmorCase {

    @Inject(at = @At("HEAD"), method = "interactAt", cancellable = true)
    public void interactAt(
        Player player,
        Vec3 vec3,
        InteractionHand interactionHand,
        CallbackInfoReturnable<InteractionResult> callbackInfo
    ) {
        var itemStack = player.getItemInHand(interactionHand);

        if (!player.level().isClientSide && itemStack.is(AVPItems.ARMOR_CASE.get())) {
            var armorStand = ArmorStand.class.cast(this);
            ArmorCaseItem.swapArmorSlots(armorStand, itemStack);
            callbackInfo.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
