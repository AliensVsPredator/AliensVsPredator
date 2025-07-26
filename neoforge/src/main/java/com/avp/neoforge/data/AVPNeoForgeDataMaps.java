package com.avp.neoforge.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

import com.avp.neoforge.service.NeoForgeRegistryService;
import com.avp.service.Services;

public class AVPNeoForgeDataMaps extends DataMapProvider {

    private static final NeoForgeRegistryService REGISTRY = (NeoForgeRegistryService) Services.REGISTRY;

    protected AVPNeoForgeDataMaps(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        var compostablesBuilder = builder(NeoForgeDataMaps.COMPOSTABLES);

        REGISTRY.getCompostableData()
            .forEach(
                compostableData -> compostablesBuilder.add(
                    compostableData.v1().get().asItem().builtInRegistryHolder(),
                    new Compostable(compostableData.v2(), compostableData.v3()),
                    compostableData.v4()
                )
            );

        var furnaceFuelBuilder = builder(NeoForgeDataMaps.FURNACE_FUELS);

        REGISTRY.getFurnaceFuelPairs()
            .forEach(
                fuelPair -> furnaceFuelBuilder.add(
                    fuelPair.v1().get().asItem().builtInRegistryHolder(),
                    new FurnaceFuel(fuelPair.v2()),
                    false
                )
            );
    }
}
