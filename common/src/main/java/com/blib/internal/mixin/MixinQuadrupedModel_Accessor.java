package com.blib.internal.mixin;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the {@code protected} {@link ModelPart} fields on {@link QuadrupedModel} so the dismemberment system can
 * resolve body/legs by name without reflection. Head is exposed via the {@code HeadedModel} interface, but the body and
 * legs require this accessor.
 */
@Mixin(QuadrupedModel.class)
public interface MixinQuadrupedModel_Accessor {

    @Accessor("head")
    ModelPart blib$getHead();

    @Accessor("body")
    ModelPart blib$getBody();

    @Accessor("rightHindLeg")
    ModelPart blib$getRightHindLeg();

    @Accessor("leftHindLeg")
    ModelPart blib$getLeftHindLeg();

    @Accessor("rightFrontLeg")
    ModelPart blib$getRightFrontLeg();

    @Accessor("leftFrontLeg")
    ModelPart blib$getLeftFrontLeg();
}
