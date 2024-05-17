package org.avp.mixin;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.avp.common.item.armor.material.AVPArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class MixinEnchantment_VeritaniumCanNotBeEnchanted {

    @Inject(at = @At("HEAD"), cancellable = true, method = "canEnchant")
    void veritaniumCanNotBeEnchanted(ItemStack itemStack, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial() == AVPArmorMaterials.VERITANIUM) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
