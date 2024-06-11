package org.avp.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.data.entity.EntityData;
import org.avp.common.registry.AVPEntityDataRegistry;
import org.avp.api.data.entity.EntitySoundData;
import org.avp.common.data.tag.AVPEntityTypeTags;

@Mixin(Mob.class)
public abstract class MixinMob_ProvideAmbientSounds extends LivingEntity {

    protected MixinMob_ProvideAmbientSounds(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "getAmbientSound", cancellable = true)
    void getAmbientSound(CallbackInfoReturnable<SoundEvent> callbackInfoReturnable) {
        // We only want to affect AVP monsters.
        if (!this.getType().is(AVPEntityTypeTags.MONSTERS)) {
            callbackInfoReturnable.setReturnValue(callbackInfoReturnable.getReturnValue());
            return;
        }

        callbackInfoReturnable.setReturnValue(
            AVPEntityDataRegistry.INSTANCE.getEntries()
                .stream()
                .filter(data -> Objects.equals(data.getHolder().get(), this.getType()))
                .findFirst()
                .flatMap(EntityData::getSoundData)
                .map(EntitySoundData::ambientSoundEventHolder)
                .flatMap(BLHolder::getOptional)
                .orElse(callbackInfoReturnable.getReturnValue())
        );
    }
}
