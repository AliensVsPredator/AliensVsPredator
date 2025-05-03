package com.avp.common.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

import com.avp.AVPResources;

public class AVPJukeboxSongs {

    public static final ResourceKey<JukeboxSong> ALIEN_MUSIC_1 = register("alien_music_1");

    public static final ResourceKey<JukeboxSong> PREDATOR_MUSIC_1 = register("predator_music_1");

    private static ResourceKey<JukeboxSong> register(String id) {
        var resourceLocation = AVPResources.location(id);
        return ResourceKey.create(Registries.JUKEBOX_SONG, resourceLocation);
    }

    public static void initialize() {}
}
