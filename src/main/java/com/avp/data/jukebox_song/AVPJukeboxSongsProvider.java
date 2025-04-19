package com.avp.data.jukebox_song;

import com.avp.common.sound.AVPJukeboxSongs;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AVPJukeboxSongsProvider extends FabricDynamicRegistryProvider {

    public AVPJukeboxSongsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.add(AVPJukeboxSongs.ALIEN_MUSIC, AVPJukeboxSongs.createAlienSong());
        entries.add(AVPJukeboxSongs.PREDATOR_MUSIC, AVPJukeboxSongs.createPredatorSong());
    }

    @Override
    public @NotNull String getName() {
        return "AVP Jukebox Songs";
    }
}
