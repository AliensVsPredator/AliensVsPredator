package com.blib.neoforge.internal.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.neoforge.internal.service.impl.BLibNeoForgeRegistryServiceImpl;

@ApiStatus.Internal
public final class BLibNeoForgeCompostableDataMapProvider extends DataMapProvider {

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
    protected void gather(@NotNull HolderLookup.Provider provider) {
        var registry = (BLibNeoForgeRegistryServiceImpl) BLibInternalServices.REGISTRY;
        var builder = builder(NeoForgeDataMaps.COMPOSTABLES);

        registry.getModContainer(mod)
            .getCompostableData()
            .forEach(
                compostableData -> builder.add(
                    compostableData.v1().get().asItem().builtInRegistryHolder(),
                    new Compostable(compostableData.v2(), compostableData.v3()),
                    compostableData.v4()
                )
            );
    }

    @Override
    public @NotNull String getName() {
        return mod.id() + " Compostable Data Maps";
    }
}
