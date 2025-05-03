package com.avp.neoforge.data;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.TempAVPItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class AVPNeoForgeDataMaps extends DataMapProvider {

    protected AVPNeoForgeDataMaps(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        builder(NeoForgeDataMaps.FURNACE_FUELS)
            .add(
                TempAVPItems.CARBON_DUST.get().builtInRegistryHolder(),
                new FurnaceFuel(800),
                false
            );
        builder(NeoForgeDataMaps.COMPOSTABLES)
            .add(TempAVPItems.IRRADIATED_RESIN_BALL.get().builtInRegistryHolder(), new Compostable(0.3F), false)
            .add(TempAVPItems.ABERRANT_RESIN_BALL.get().builtInRegistryHolder(), new Compostable(0.3F), false)
            .add(TempAVPItems.NETHER_RESIN_BALL.get().builtInRegistryHolder(), new Compostable(0.3F), false)
            .add(TempAVPItems.RESIN_BALL.get().builtInRegistryHolder(), new Compostable(0.3F), false)

            .add(AVPBlocks.IRRADIATED_RESIN.get().asItem().builtInRegistryHolder(), new Compostable(1F), false)
            .add(AVPBlocks.IRRADIATED_RESIN_NODE.get().asItem().builtInRegistryHolder(), new Compostable(1F), false)
            .add(AVPBlocks.IRRADIATED_RESIN_VEIN.get().asItem().builtInRegistryHolder(), new Compostable(0.3F), false)
            .add(AVPBlocks.IRRADIATED_RESIN_WEB.get().asItem().builtInRegistryHolder(), new Compostable(0.65F), false)

            .add(AVPBlocks.ABERRANT_RESIN.get().asItem().builtInRegistryHolder(), new Compostable(1F), false)
            .add(AVPBlocks.ABERRANT_RESIN_NODE.get().asItem().builtInRegistryHolder(), new Compostable(1F), false)
            .add(AVPBlocks.ABERRANT_RESIN_VEIN.get().asItem().builtInRegistryHolder(), new Compostable(0.3F), false)
            .add(AVPBlocks.ABERRANT_RESIN_WEB.get().asItem().builtInRegistryHolder(), new Compostable(0.65F), false)

            .add(AVPBlocks.NETHER_RESIN.get().asItem().builtInRegistryHolder(), new Compostable(1F), false)
            .add(AVPBlocks.NETHER_RESIN_NODE.get().asItem().builtInRegistryHolder(), new Compostable(1F), false)
            .add(AVPBlocks.NETHER_RESIN_VEIN.get().asItem().builtInRegistryHolder(), new Compostable(0.3F), false)
            .add(AVPBlocks.NETHER_RESIN_WEB.get().asItem().builtInRegistryHolder(), new Compostable(0.65F), false)

            .add(AVPBlocks.RESIN.get().asItem().builtInRegistryHolder(), new Compostable(1F), false)
            .add(AVPBlocks.RESIN_NODE.get().asItem().builtInRegistryHolder(), new Compostable(1F), false)
            .add(AVPBlocks.RESIN_VEIN.get().asItem().builtInRegistryHolder(), new Compostable(0.3F), false)
            .add(AVPBlocks.RESIN_WEB.get().asItem().builtInRegistryHolder(), new Compostable(0.65F), false);
    }
}
