package com.avp.neoforge.data;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class BiomeFilterRegistryLookup implements HolderLookup.RegistryLookup<Biome> {

    private final HolderGetter<Biome> biomes;

    public BiomeFilterRegistryLookup(HolderGetter<Biome> biomes) {
        this.biomes = biomes;
    }

    @Override
    public @NotNull Optional<Holder.Reference<Biome>> get(@NotNull ResourceKey<Biome> pResourceKey) {
        return biomes.get(pResourceKey);
    }

    @Override
    public @NotNull Optional<HolderSet.Named<Biome>> get(@NotNull TagKey<Biome> pTagKey) {
        return biomes.get(pTagKey);
    }

    @Override
    public @NotNull Stream<Holder.Reference<Biome>> listElements() {
        return Stream.empty();
    }

    @Override
    public @NotNull Stream<HolderSet.Named<Biome>> listTags() {
        return Stream.empty();
    }

    @Override
    public @NotNull ResourceKey<? extends Registry<? extends Biome>> key() {
        return Registries.BIOME;
    }

    @Override
    public boolean canSerializeIn(@NotNull HolderOwner<Biome> pOwner) {
        return true;
    }

    @Override
    public @NotNull Lifecycle registryLifecycle() {
        return Lifecycle.stable();
    }
}
