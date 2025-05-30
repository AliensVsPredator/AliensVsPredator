package com.avp.fabric.data.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import com.avp.common.registry.key.AVPBiomeKeys;

public class AVPBiomeProvider extends FabricDynamicRegistryProvider {

    public AVPBiomeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.add(AVPBiomeKeys.NUKED_BIOME, createNukedBiome());
    }

    public static Biome createNukedBiome() {
        return new Biome.BiomeBuilder()
            .temperature(90F)
            .downfall(0F)
            .hasPrecipitation(false)
            .temperatureAdjustment(Biome.TemperatureModifier.NONE)
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                    .skyColor(12237498)
                    .fogColor(12632256)
                    .waterFogColor(10944384)
                    .waterColor(6666343)
                    .grassColorOverride(7237230)
                    .foliageColorOverride(9453889)
                    .ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.5F))
                    .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                    .build()
            )
            .mobSpawnSettings(
                new MobSpawnSettings.Builder()
                    .creatureGenerationProbability(0.1F)
                    .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.HUSK, 1, 1, 1))
                    .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 1, 1, 1))
                    .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 1, 1, 1))
                    .addMobCharge(EntityType.HUSK, 0.7, 0.15)
                    .addMobCharge(EntityType.STRAY, 0.7, 0.15)
                    .addMobCharge(EntityType.CREEPER, 0.7, 0.15)
                    .build()
            )
            .generationSettings(BiomeGenerationSettings.EMPTY)
            .build();
    }

    @Override
    public @NotNull String getName() {
        return "AVP Biome Providers";
    }
}
