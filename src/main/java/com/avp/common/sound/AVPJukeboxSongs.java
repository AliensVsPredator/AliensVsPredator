package com.avp.common.sound;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

import com.avp.AVPResources;

public class AVPJukeboxSongs {

    public static final ResourceKey<JukeboxSong> ALIEN_MUSIC = register("alien_music");

    public static final ResourceKey<JukeboxSong> PREDATOR_MUSIC = register("predator_music");

    public static JukeboxSong createAlienSong() {
        return new JukeboxSong(Holder.direct(AVPSoundEvents.JUKEBOX_SOUNDS_ALIEN_MUSIC), Component.translatable("jukebox_song.avp.alien_music"), 180, 12);
    }

    public static JukeboxSong createPredatorSong() {
        return new JukeboxSong(Holder.direct(AVPSoundEvents.JUKEBOX_SOUNDS_PREDATOR_MUSIC), Component.translatable("jukebox_song.avp.predator_music"), 184, 12);
    }

    private static ResourceKey<JukeboxSong> register(String id) {
        var resourceLocation = AVPResources.location(id);
        return ResourceKey.create(Registries.JUKEBOX_SONG, resourceLocation);
    }

    public static void initialize() {}
}
