package com.avp.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.common.block.DecoratedPotPatternKeys;
import com.avp.common.item.AVPItemTags;

@Mixin(DecoratedPotPatterns.class)
public abstract class MixinDecoratedPotPatterns_GetPatternForCustomSherds {

    @Inject(at = @At("HEAD"), method = "getPatternFromItem", cancellable = true)
    private static void getPatternFromItem(Item item, CallbackInfoReturnable<ResourceKey<DecoratedPotPattern>> callbackInfo) {
        if (item.builtInRegistryHolder().is(AVPItemTags.DECORATIVE_POT_SHERDS)) {
            var patternResourceKey = DecoratedPotPatternKeys.ITEM_TO_POT_TEXTURE.get(item);
            callbackInfo.setReturnValue(patternResourceKey);
        }
    }
}
