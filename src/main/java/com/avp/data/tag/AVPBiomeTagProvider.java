package com.avp.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
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

        getOrCreateTagBuilder(AVPBiomeTags.HAS_MARINE_CAMP_GRASS)
            .add(Biomes.MEADOW)
            .add(Biomes.PLAINS)
            .add(Biomes.FOREST)
            .add(Biomes.BIRCH_FOREST);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_OUTPOST_COMMS)
            .add(Biomes.MEADOW)
            .add(Biomes.PLAINS)
            .add(Biomes.FOREST)
            .add(Biomes.BIRCH_FOREST);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_ALTAR)
            .addTag(AVPBiomeTags.HAS_BADLANDS_ALTAR)
            .addTag(AVPBiomeTags.HAS_DESERT_ALTAR);
    }
}
