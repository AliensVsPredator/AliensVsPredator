package com.blib.internal.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.internal.common.BLibDecoratedPotPatternCache;

@Mixin(DecoratedPotPatterns.class)
public abstract class MixinDecoratedPotPatterns_GetPatternForCustomSherds {

    @Inject(at = @At("HEAD"), method = "getPatternFromItem", cancellable = true)
    private static void getPatternFromItem(Item item, CallbackInfoReturnable<ResourceKey<DecoratedPotPattern>> callbackInfo) {
        if (
            item.builtInRegistryHolder().is(ItemTags.DECORATED_POT_SHERDS)
                && BLibDecoratedPotPatternCache.INSTANCE.has(item)
        ) {
            var patternResourceKey = BLibDecoratedPotPatternCache.INSTANCE.getOrNull(item);
            callbackInfo.setReturnValue(patternResourceKey);
        }
    }
}
