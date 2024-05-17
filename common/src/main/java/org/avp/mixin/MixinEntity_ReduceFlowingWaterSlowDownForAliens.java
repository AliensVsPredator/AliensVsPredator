package org.avp.mixin;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.avp.common.tag.AVPEntityTags;
import org.avp.common.util.MixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

@Mixin(Entity.class)
public abstract class MixinEntity_ReduceFlowingWaterSlowDownForAliens {
    @ModifyVariable(
        method = "updateFluidHeightAndDoFluidPushing(Lnet/minecraft/tags/TagKey;D)Z",
        at = @At("HEAD"),
        argsOnly = true
    )
    private double modifyWaterCurrentSlowDown(double waterCurrentSlowDownModifier, TagKey<Fluid> fluidTagKey) {
        var self = MixinUtils.<Entity>self(this);
        if (Objects.equals(fluidTagKey, FluidTags.WATER) && self.getType().is(AVPEntityTags.ALIENS)) {
            return 0;
        }

        return waterCurrentSlowDownModifier;
    }
}
