package com.avp.fabric.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.List;

import com.avp.core.AVPResources;
import com.avp.core.common.block.AVPBlocks;

public class AVPCaveConfigurations {

    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUNITE_GEODE = ResourceKey.create(
        Registries.CONFIGURED_FEATURE,
        AVPResources.location("autunite_geode")
    );

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> registry) {
        var placedFeatureLookup = registry.lookup(Registries.PLACED_FEATURE);

        // FIXME:
//        registry.register(AUTUNITE_GEODE, new ConfiguredFeature<>(Feature.GEODE, createAutuniteGeodeConfiguration()));
    }

    public static GeodeConfiguration createAutuniteGeodeConfiguration() {
        return new GeodeConfiguration(
            new GeodeBlockSettings(
                BlockStateProvider.simple(Blocks.AIR),
                BlockStateProvider.simple(Blocks.GRANITE),
                new WeightedStateProvider(
                    SimpleWeightedRandomList.<BlockState>builder()
                        .add(AVPBlocks.AUTUNITE_ORE.get().defaultBlockState(), 1)
                        .add(Blocks.GRANITE.defaultBlockState(), 1)
                        .build()
                ),
                BlockStateProvider.simple(Blocks.CALCITE),
                BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                List.of(
                    Blocks.AIR.defaultBlockState()
                ),
                BlockTags.FEATURES_CANNOT_REPLACE,
                BlockTags.GEODE_INVALID_BLOCKS
            ),
            new GeodeLayerSettings(1.7, 2.2, 3.2, 4.2),
            new GeodeCrackSettings(0.95, 2.0, 2),
            0.35,
            0.083,
            true,
            UniformInt.of(4, 6),
            UniformInt.of(3, 4),
            UniformInt.of(1, 2),
            -16,
            16,
            0.05,
            1
        );
    }
}
