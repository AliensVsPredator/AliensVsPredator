package org.avp.mixin;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import org.avp.common.damage.AVPDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageTypes.class)
public interface MixinDamageTypes_RegisterModdedDamageTypes {

    @Inject(at = @At("TAIL"), method = "bootstrap")
    private static void bootstrap(BootstapContext<DamageType> bootstrapContext, CallbackInfo callbackInfo) {
        bootstrapContext.register(AVPDamageTypes.INSTANCE.acid, new DamageType("acid", 0.1F));
        bootstrapContext.register(AVPDamageTypes.INSTANCE.bullet, new DamageType("bullet", 0.1F));
    }
}
