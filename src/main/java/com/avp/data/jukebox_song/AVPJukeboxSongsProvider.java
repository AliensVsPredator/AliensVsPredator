package com.avp.data.jukebox_song;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import com.avp.common.sound.AVPJukeboxSongs;

public class AVPJukeboxSongsProvider extends FabricDynamicRegistryProvider {

    public AVPJukeboxSongsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.add(AVPJukeboxSongs.ALIEN_MUSIC_1, AVPJukeboxSongs.createAlienMusic1Song());
        entries.add(AVPJukeboxSongs.PREDATOR_MUSIC_1, AVPJukeboxSongs.createPredatorMusic1Song());
    }

    @Override
    public @NotNull String getName() {
        return "AVP Jukebox Songs";
    }
}
