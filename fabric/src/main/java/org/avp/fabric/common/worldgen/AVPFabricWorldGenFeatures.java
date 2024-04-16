package org.avp.fabric.common.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

import org.avp.common.worldgen.feature.AVPOreFeatures;

public class AVPFabricWorldGenFeatures {

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    static {
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            AVPOreFeatures.BAUXITE_ORE_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            AVPOreFeatures.COBALT_ORE_PLACED_KEY
        );
        BiomeModifications.addFeature(
            ctx -> ctx.hasTag(BiomeTags.IS_OCEAN) || ctx.hasTag(BiomeTags.IS_RIVER) || ctx.hasTag(BiomeTags.IS_BEACH),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            AVPOreFeatures.LITHIUM_ORE_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            AVPOreFeatures.MONAZITE_ORE_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            AVPOreFeatures.SILICA_ORE_PLACED_KEY
        );
    }

    private AVPFabricWorldGenFeatures() {
        throw new UnsupportedOperationException();
    }
}
