package com.avp.fabric.common.sound;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

import com.avp.AVPResources;
import com.avp.common.sound.AVPSoundEvents;

public class AVPJukeboxSongs {

    public static final ResourceKey<JukeboxSong> ALIEN_MUSIC_1 = register("alien_music_1");

    public static final ResourceKey<JukeboxSong> PREDATOR_MUSIC_1 = register("predator_music_1");

    public static JukeboxSong createAlienMusic1Song() {
        return new JukeboxSong(
            Holder.direct(AVPSoundEvents.JUKEBOX_SOUNDS_ALIEN_MUSIC_1.get()),
            Component.translatable("jukebox_song.avp.alien_music_1"),
            180,
            12
        );
    }

    public static JukeboxSong createPredatorMusic1Song() {
        return new JukeboxSong(
            Holder.direct(AVPSoundEvents.JUKEBOX_SOUNDS_PREDATOR_MUSIC_1.get()),
            Component.translatable("jukebox_song.avp.predator_music_1"),
            184,
            12
        );
    }

    private static ResourceKey<JukeboxSong> register(String id) {
        var resourceLocation = AVPResources.location(id);
        return ResourceKey.create(Registries.JUKEBOX_SONG, resourceLocation);
    }

    public static void initialize() {}
}
