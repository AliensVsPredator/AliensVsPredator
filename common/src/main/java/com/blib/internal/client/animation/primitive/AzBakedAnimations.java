package com.blib.internal.client.animation.primitive;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import com.blib.internal.client.animation.cache.AzBakedAnimationCache;
import com.blib.internal.common.exception.AzureLibException;

public record AzBakedAnimations(
    Map<String, AzBakedAnimation> animations,
    Map<String, ResourceLocation> includes
) {

    @Nullable
    public AzBakedAnimation getAnimation(String name) {
        AzBakedAnimation result = animations.get(name);
        if (result == null && includes != null) {
            ResourceLocation otherFileID = includes.getOrDefault(name, null);
            if (otherFileID != null) {
                AzBakedAnimations otherBakedAnims = AzBakedAnimationCache.getInstance().getNullable(otherFileID);
                if (otherBakedAnims.equals(this)) {
                    throw new AzureLibException(
                        "The animation file '" + otherFileID +
                            "' refers back to itself through includes."
                    );
                } else {
                    result = otherBakedAnims.getAnimationWithoutIncludes(name);
                }
            }
        }
        return result;
    }

    private AzBakedAnimation getAnimationWithoutIncludes(String name) {
        return animations.get(name);
    }

}
