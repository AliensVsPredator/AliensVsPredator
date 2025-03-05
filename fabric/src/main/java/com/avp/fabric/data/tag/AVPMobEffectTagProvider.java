package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.concurrent.CompletableFuture;

import com.avp.core.common.level.effect.AVPMobEffectTags;

public class AVPMobEffectTagProvider extends FabricTagProvider<MobEffect> {

    public AVPMobEffectTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.MOB_EFFECT, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(AVPMobEffectTags.DOES_NOT_AFFECT_ALIENS)
            .add(
                MobEffects.BLINDNESS.value(),
                MobEffects.CONFUSION.value(),
                MobEffects.DARKNESS.value(),
                MobEffects.DIG_SLOWDOWN.value(),
                MobEffects.HARM.value(),
                MobEffects.INFESTED.value(),
                MobEffects.MOVEMENT_SLOWDOWN.value(),
                MobEffects.POISON.value(),
                MobEffects.WEAKNESS.value(),
                MobEffects.WITHER.value()
            );
    }
}
