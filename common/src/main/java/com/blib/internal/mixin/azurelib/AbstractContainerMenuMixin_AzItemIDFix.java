package com.blib.internal.mixin.azurelib;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.blib.azurelib.common.animation.cache.AzIdentityRegistry;
import com.blib.mod.common.registry.init.BLibDataComponents;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin_AzItemIDFix {

    @Unique
    private static final int DEFAULT_AZ_ID = -1;

    @WrapOperation(
        method = "doClick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;",
            ordinal = 1
        )
    )
    public ItemStack azurelib$syncAzureIDWithRemote(ItemStack itemStack, int count, Operation<ItemStack> original) {
        var copyStack = original.call(itemStack, count);

        if (AzIdentityRegistry.hasIdentity(itemStack.getItem()) && copyStack.has(BLibDataComponents.AZ_ID.get())) {
            copyStack.remove(BLibDataComponents.AZ_ID.get());
        }

        return copyStack;
    }

    @WrapOperation(
        method = "synchronizeSlotToRemote", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    public boolean azurelib$syncAzureIDWithRemote(
        ItemStack itemStack,
        ItemStack comparisonItemStack,
        Operation<Boolean> original
    ) {
        return azurelib$compareStacksWithAzureID(itemStack, comparisonItemStack, original);
    }

    @WrapOperation(
        method = "triggerSlotListeners", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    public boolean azurelib$detectSlotChangeWithAzureID(
        ItemStack itemStack,
        ItemStack comparisonItemStack,
        Operation<Boolean> original
    ) {
        return azurelib$compareStacksWithAzureID(itemStack, comparisonItemStack, original);
    }

    @Unique
    private boolean azurelib$compareStacksWithAzureID(
        ItemStack itemStack,
        ItemStack comparisonItemStack,
        Operation<Boolean> original
    ) {
        if (AzIdentityRegistry.hasIdentity(itemStack.getItem())) {
            return original.call(itemStack, comparisonItemStack) && azurelib$stacksHaveMatchingAzID(
                itemStack,
                comparisonItemStack
            );
        }
        return original.call(itemStack, comparisonItemStack);
    }

    @Unique
    private boolean azurelib$stacksHaveMatchingAzID(ItemStack itemStack, ItemStack comparisonItemStack) {
        return itemStack.getOrDefault(BLibDataComponents.AZ_ID.get(), DEFAULT_AZ_ID)
            .equals(comparisonItemStack.getOrDefault(BLibDataComponents.AZ_ID.get(), DEFAULT_AZ_ID));
    }

}
