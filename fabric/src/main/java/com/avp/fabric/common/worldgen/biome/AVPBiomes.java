package com.avp.fabric.common.worldgen.biome;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;

import com.avp.common.AVPResources;

public class AVPBiomes {

    public static final ResourceKey<Biome> NUKED_BIOME = register("nuked_biome");

    private static ResourceKey<Biome> register(String id) {
        return ResourceKey.create(Registries.BIOME, AVPResources.location(id));
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

    public static void initialize() {}
}
