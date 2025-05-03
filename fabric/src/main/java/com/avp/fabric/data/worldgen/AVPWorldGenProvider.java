package com.avp.fabric.data.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AVPWorldGenProvider extends FabricDynamicRegistryProvider {

    public AVPWorldGenProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
        // entries.addAll(registries.lookupOrThrow(Registries.BIOME));
        // entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE));
        // entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE_SET));
        // entries.addAll(registries.lookupOrThrow(Registries.TEMPLATE_POOL));
    }

    @Override
    public @NotNull String getName() {
        return "AVP Worldgen";
    }
}
