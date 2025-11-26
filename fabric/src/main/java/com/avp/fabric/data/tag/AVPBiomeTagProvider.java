package com.avp.fabric.data.tag;

import com.avp.common.registry.key.AVPBiomeKeys;
import com.avp.common.registry.tag.AVPBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

public class AVPBiomeTagProvider extends FabricTagProvider<Biome> {

    public AVPBiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.BIOME, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
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

        getOrCreateTagBuilder(AVPBiomeTags.HAS_OUTPOST_SUPPLY_BADLAND)
            .add(Biomes.BADLANDS);

        getOrCreateTagBuilder(AVPBiomeTags.HAS_OUTPOST_SUPPLY_DESERT)
            .add(Biomes.DESERT);

        getOrCreateTagBuilder(AVPBiomeTags.IS_IRRADIATED)
            .addOptional(AVPBiomeKeys.NUKED_BIOME);
    }
}
