package com.blib.internal.mixin.azurelib;

import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import com.blib.api.client.animation.v1.identity.AzIdentityRegistry;
import com.blib.internal.common.util.AzureLibUtil;
import com.blib.mod.common.registry.init.BLibDataComponents;

@Mixin(ItemStack.class)
public class ItemStackMixin_AzItemStackIdentityRegistry {

    @Inject(
        method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V",
        at = @At("TAIL")
    )
    public void az_addIdentityComponent(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
        var self = AzureLibUtil.<ItemStack>self(this);

        if (AzIdentityRegistry.hasIdentity(self.getItem()) && !components.has(BLibDataComponents.AZ_ID.get())) {
            components.set(BLibDataComponents.AZ_ID.get(), UUID.randomUUID());
        }
    }
}
