package com.avp.fabric.data.jukebox_song;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.key.AVPJukeboxSongKeys;

public class AVPJukeboxSongsProvider extends FabricDynamicRegistryProvider {

    public AVPJukeboxSongsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.add(AVPJukeboxSongKeys.ALIEN_MUSIC_1, createAlienMusic1Song());
        entries.add(AVPJukeboxSongKeys.PREDATOR_MUSIC_1, createPredatorMusic1Song());
    }

    private JukeboxSong createAlienMusic1Song() {
        return new JukeboxSong(
            AVPSoundEvents.JUKEBOX_SOUNDS_ALIEN_MUSIC_1.getHolder(),
            Component.translatable("jukebox_song.avp.alien_music_1"),
            180,
            12
        );
    }

    private JukeboxSong createPredatorMusic1Song() {
        return new JukeboxSong(
            AVPSoundEvents.JUKEBOX_SOUNDS_PREDATOR_MUSIC_1.getHolder(),
            Component.translatable("jukebox_song.avp.predator_music_1"),
            184,
            12
        );
    }

    @Override
    public @NotNull String getName() {
        return "AVP Jukebox Songs";
    }
}
