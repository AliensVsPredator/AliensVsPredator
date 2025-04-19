package com.avp.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

import com.avp.common.worldgen.biome.AVPBiomeTags;

public class AVPBiomeTagProvider extends FabricTagProvider<Biome> {

    public AVPBiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.BIOME, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(AVPBiomeTags.HAS_BADLANDS_ALTAR)
            .add(Biomes.BADLANDS);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_DESERT_ALTAR)
            .add(Biomes.DESERT);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_DEEPSLATE_ALTAR)
            .addOptionalTag(BiomeTags.IS_OVERWORLD);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_JUNGLE_ALTAR)
            .add(Biomes.JUNGLE)
            .add(Biomes.BAMBOO_JUNGLE)
            .add(Biomes.SPARSE_JUNGLE);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_NETHER_ALTAR)
            .add(Biomes.NETHER_WASTES)
            .add(Biomes.CRIMSON_FOREST);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_MARINE_CAMP_GRASS)
            .add(Biomes.MEADOW)
            .add(Biomes.PLAINS)
            .add(Biomes.FOREST)
            .add(Biomes.BIRCH_FOREST);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_MOBILE_LAB)
            .add(Biomes.SAVANNA)
            .add(Biomes.STONY_SHORE)
            .add(Biomes.SNOWY_PLAINS)
            .add(Biomes.BADLANDS)
            .add(Biomes.CHERRY_GROVE)
            .add(Biomes.DESERT);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_OUTPOST_COMMS)
            .add(Biomes.WINDSWEPT_SAVANNA)
            .add(Biomes.WINDSWEPT_HILLS)
            .add(Biomes.FLOWER_FOREST)
            .add(Biomes.SUNFLOWER_PLAINS);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_OUTPOST_MUNITION)
            .add(Biomes.TAIGA)
            .add(Biomes.SNOWY_TAIGA);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_ALTAR)
            .addTag(AVPBiomeTags.HAS_BADLANDS_ALTAR)
            .addTag(AVPBiomeTags.HAS_DESERT_ALTAR);
    }
}
