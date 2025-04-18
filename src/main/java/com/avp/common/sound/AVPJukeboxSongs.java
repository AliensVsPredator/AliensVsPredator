package com.avp.common.sound;

import com.avp.AVPResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public class AVPJukeboxSongs {

    public static final ResourceKey<JukeboxSong> ALIEN_MUSIC = register("alien_music");

    public static final ResourceKey<JukeboxSong> PREDATOR_MUSIC = register("predator_music");

    private static ResourceKey<JukeboxSong> register(String id) {
        var resourceLocation = AVPResources.location(id);
        return ResourceKey.create(Registries.JUKEBOX_SONG, resourceLocation);
    }

    public static void initialize() {}
}
