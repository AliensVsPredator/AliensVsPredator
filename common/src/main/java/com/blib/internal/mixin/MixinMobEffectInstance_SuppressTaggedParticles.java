package com.blib.internal.mixin;

import com.blib.api.common.tag.v1.BLibMobEffectTags;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces {@link MobEffectInstance#isVisible()} to return {@code false} for any effect whose holder is in
 * {@link BLibMobEffectTags#NO_PARTICLES}, suppressing world-particle emission regardless of how the instance
 * was constructed (commands, splash potions, items that pass {@code visible = true}). The {@code showIcon}
 * flag is untouched so the inventory effect-icon still appears.
 * <p>
 * Lets downstream mods opt an effect out of particles by tag rather than by writing their own mixin into
 * {@code MobEffectInstance}.
 */
@Mixin(MobEffectInstance.class)
public abstract class MixinMobEffectInstance_SuppressTaggedParticles {

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void blib$suppressTaggedParticles(CallbackInfoReturnable<Boolean> cir) {
        var instance = (MobEffectInstance) (Object) this;

        if (instance.getEffect().is(BLibMobEffectTags.NO_PARTICLES)) {
            cir.setReturnValue(false);
        }
    }
}
