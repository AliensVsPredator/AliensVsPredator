package com.blib.neoforge.data;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.neoforge.internal.service.impl.NeoForgeBLibRegistryServiceImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BLibNeoForgeCompostableDataMapProvider extends DataMapProvider {

    private final BLibMod mod;

    public BLibNeoForgeCompostableDataMapProvider(
        BLibMod mod,
        PackOutput packOutput,
        CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(packOutput, lookupProvider);
        this.mod = mod;
    }

    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        var compostablesBuilder = builder(NeoForgeDataMaps.COMPOSTABLES);

        var registry = (NeoForgeBLibRegistryServiceImpl) BLibInternalServices.REGISTRY;
        registry.getModContainer(mod)
            .getCompostableData()
            .forEach(
                compostableData -> compostablesBuilder.add(
                    compostableData.v1().get().asItem().builtInRegistryHolder(),
                    new Compostable(compostableData.v2(), compostableData.v3()),
                    compostableData.v4()
                )
            );
    }
}
