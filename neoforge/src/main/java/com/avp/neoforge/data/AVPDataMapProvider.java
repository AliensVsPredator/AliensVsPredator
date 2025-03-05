package com.avp.neoforge.data;

import com.avp.core.platform.service.Services;
import com.avp.neoforge.platform.service.NeoForgeRegistryService;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class AVPDataMapProvider extends DataMapProvider {

    public AVPDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }
    
    @Override
    protected void gather() {
        generateCompostables();
        generateFurnaceFuels();
    }

    // TODO: Verify this works.
    private void generateCompostables() {
        var builder = builder(NeoForgeDataMaps.COMPOSTABLES);
        var pairs = ((NeoForgeRegistryService) Services.REGISTRY).getCompostableItemLikeChancePairs();

        pairs.forEach(pair -> {
            var registryHolder = pair.getKey().get().asItem().builtInRegistryHolder();
            var compostable = new Compostable(pair.getValue(), true);
            builder.add(registryHolder, compostable, false);
        });
    }

    // TODO: Verify this works.
    private void generateFurnaceFuels() {
        var builder = builder(NeoForgeDataMaps.FURNACE_FUELS);
        var pairs = ((NeoForgeRegistryService) Services.REGISTRY).getFuelItemLikeBurnTimeInTicksPairs();

        pairs.forEach(pair -> {
            var registryHolder = pair.getKey().get().asItem().builtInRegistryHolder();
            var furnaceFuel = new FurnaceFuel(pair.getValue());
            builder.add(registryHolder, furnaceFuel, false);
        });
    }
}