package com.avp.data.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import com.avp.common.worldgen.biome.AVPBiomes;

public class AVPBiomeProvider extends FabricDynamicRegistryProvider {

    public AVPBiomeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.add(AVPBiomes.NUKED_BIOME, AVPBiomes.createNukedBiome());
    }

    @Override
    public @NotNull String getName() {
        return "AVP Biome Providers";
    }
}
