package com.blib.internal.mixin;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Exposes the private {@code children} map on {@link ModelPart} so the dismemberment renderer can walk a part's subtree
 * by name — needed to selectively hide/zero-pose descendants when a limb fragment renders, since
 * {@code ModelPart.render} is recursive and gives no opt-out for individual children.
 */
@Mixin(ModelPart.class)
public interface MixinModelPart_Accessor {

    @Accessor(value = "children")
    Map<String, ModelPart> blib$getChildren();
}
