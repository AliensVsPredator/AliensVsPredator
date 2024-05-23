package org.avp.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.api.Holder;
import org.avp.api.entity.data.EntityData;
import org.avp.common.entity.data.sound.EntitySoundData;
import org.avp.common.entity.data.type.AVPEntityDataRegistry;
import org.avp.common.tag.AVPEntityTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Monster.class)
public abstract class MixinMonster_ProvideHurtAndDeathSounds extends PathfinderMob {

    protected MixinMonster_ProvideHurtAndDeathSounds(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "getHurtSound", cancellable = true)
    void getHurtSound(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> callbackInfoReturnable) {
        // We only want to affect AVP monsters.
        if (!this.getType().is(AVPEntityTypeTags.MONSTERS)) {
            callbackInfoReturnable.setReturnValue(callbackInfoReturnable.getReturnValue());
            return;
        }

        var returnValue = AVPEntityDataRegistry.INSTANCE.getEntries()
            .stream()
            .filter(data -> Objects.equals(data.getHolder().get(), this.getType()))
            .findFirst()
            .flatMap(EntityData::getSoundData)
            .map(EntitySoundData::hurtSoundEventHolder)
            .flatMap(Holder::getOptional)
            .orElse(callbackInfoReturnable.getReturnValue());

        callbackInfoReturnable.setReturnValue(returnValue);
    }

    @Inject(at = @At("HEAD"), method = "getDeathSound", cancellable = true)
    void getDeathSound(CallbackInfoReturnable<SoundEvent> callbackInfoReturnable) {
        // We only want to affect AVP monsters.
        if (!this.getType().is(AVPEntityTypeTags.MONSTERS)) {
            callbackInfoReturnable.setReturnValue(callbackInfoReturnable.getReturnValue());
            return;
        }

        var returnValue = AVPEntityDataRegistry.INSTANCE.getEntries()
            .stream()
            .filter(data -> Objects.equals(data.getHolder().get(), this.getType()))
            .findFirst()
            .flatMap(EntityData::getSoundData)
            .map(EntitySoundData::deathSoundEventHolder)
            .flatMap(Holder::getOptional)
            .orElse(callbackInfoReturnable.getReturnValue());

        callbackInfoReturnable.setReturnValue(returnValue);
    }
}
