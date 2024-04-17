package org.avp.fabric.common.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

import org.avp.api.GameObject;
import org.avp.common.registry.AVPDeferredRegistry;
import org.avp.common.worldgen.feature.AVPOreFeatures;

import java.util.function.Supplier;

public class AVPFabricWorldGenFeatures extends AVPDeferredRegistry<Void> {

    public static final AVPFabricWorldGenFeatures INSTANCE = new AVPFabricWorldGenFeatures();

    private AVPFabricWorldGenFeatures() {}

    @Override
    protected GameObject<Void> createHolder(String registryName, Supplier<Void> supplier) {
        return null;
    }

    @Override
    public void register() {
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
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            AVPOreFeatures.TITANIUM_ORE_PLACED_KEY
        );
    }
}
