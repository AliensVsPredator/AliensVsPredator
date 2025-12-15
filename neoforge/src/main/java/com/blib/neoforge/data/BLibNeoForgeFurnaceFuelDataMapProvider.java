package com.blib.neoforge.data;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.neoforge.internal.service.impl.NeoForgeBLibRegistryServiceImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BLibNeoForgeFurnaceFuelDataMapProvider extends DataMapProvider {

    private final BLibMod mod;

    public BLibNeoForgeFurnaceFuelDataMapProvider(
        BLibMod mod,
        PackOutput packOutput,
        CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(packOutput, lookupProvider);
        this.mod = mod;
    }

    @Override
    protected void gather(@NotNull HolderLookup.Provider provider) {
        var registry = (NeoForgeBLibRegistryServiceImpl) BLibInternalServices.REGISTRY;
        var builder = builder(NeoForgeDataMaps.FURNACE_FUELS);

        registry.getModContainer(mod)
            .getFurnaceFuelData()
            .forEach(
                compostableData -> builder.add(
                    compostableData.v1().get().asItem().builtInRegistryHolder(),
                    new FurnaceFuel(compostableData.v2()),
                    false
                )
            );
    }

    @Override
    public @NotNull String getName() {
        return mod.id() + " Furnace Fuel Data Maps";
    }
}
