package com.blib.internal.mixin.azurelib;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

import com.blib.azurelib.common.animation.AzAnimator;
import com.blib.azurelib.common.animation.AzAnimatorAccessor;
import com.blib.azurelib.common.animation.cache.AzIdentifiableItemStackAnimatorCache;
import com.blib.azurelib.common.animation.impl.AzItemAnimator;
import com.blib.azurelib.common.util.AzureLibUtil;
import com.blib.mod.common.registry.init.BLibDataComponents;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin_AzItemAnimatorCache implements AzAnimatorAccessor<UUID, ItemStack> {

    @Override
    public void setAnimator(@Nullable AzAnimator<UUID, ItemStack> animator) {
        var itemStack = AzureLibUtil.<ItemStack>self(this);
        AzIdentifiableItemStackAnimatorCache.getInstance().add(itemStack, (AzItemAnimator) animator);
    }

    @Override
    public @Nullable AzAnimator<UUID, ItemStack> getAnimatorOrNull() {
        var self = AzureLibUtil.<ItemStack>self(this);
        var uuid = self.getComponents().get(BLibDataComponents.AZ_ID.get());
        return AzIdentifiableItemStackAnimatorCache.getInstance().getOrNull(uuid);
    }
}
