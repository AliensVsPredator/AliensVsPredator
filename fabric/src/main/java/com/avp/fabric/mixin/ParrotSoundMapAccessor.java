package com.avp.fabric.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Parrot.class)
public interface ParrotSoundMapAccessor {

    @Accessor("MOB_SOUND_MAP")
    static Map<EntityType<?>, SoundEvent> getSoundMap() {
        throw new AssertionError();
    }
}
