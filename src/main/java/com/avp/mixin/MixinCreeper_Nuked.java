package com.avp.mixin;

import com.avp.common.worldgen.biome.AVPBiomes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Creeper.class)
public abstract class MixinCreeper_Nuked extends Monster {

    protected MixinCreeper_Nuked(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isPowered", at = @At("HEAD"), cancellable = true)
    private void setCharged(CallbackInfoReturnable<Boolean> cir){
        if (this.level().getBiome(this.blockPosition()).is(AVPBiomes.NUKED_BIOME)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
