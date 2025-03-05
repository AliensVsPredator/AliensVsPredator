package com.avp.core.common.worldgen;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.worldgen.builder.AVPOreData;

public class AVPOres {

    private static final RuleTest DEEPSLATE_ORE_REPLACEABLES_RULE = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

    private static final RuleTest GRAVEL_REPLACEABLES_RULE = new BlockMatchTest(Blocks.GRAVEL);

    private static final RuleTest STONE_ORE_REPLACEABLES_RULE = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);

    private static final List<AVPOreData> ORE_DATA = new ArrayList<>();

    public static List<AVPOreData> getAll() {
        return Collections.unmodifiableList(ORE_DATA);
    }

    public static final AVPOreData BAUXITE_UPPER = create(
        builder("bauxite_ore_upper", OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.BAUXITE_ORE.get().defaultBlockState()))
            .count(75)
            .heightRange(HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(512)))
            .veinSize(9)
            .normalizedAirDiscardChance(0F)
    );

    public static final AVPOreData BAUXITE_MIDDLE = create(
        builder("bauxite_ore_middle", OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.BAUXITE_ORE.get().defaultBlockState()))
            .count(20)
            .heightRange(HeightRangePlacement.uniform(VerticalAnchor.absolute(9), VerticalAnchor.absolute(79)))
            .veinSize(9)
            .normalizedAirDiscardChance(0F)
    );

    public static final AVPOreData GALENA = create(
        builder("galena_ore", OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.GALENA_ORE.get().defaultBlockState()))
            .count(36)
            .heightRange(HeightRangePlacement.uniform(VerticalAnchor.absolute(9), VerticalAnchor.absolute(96)))
            .veinSize(9)
            .normalizedAirDiscardChance(0F)
    );

    public static final AVPOreData LEAD_SWAMP = create(
        builder("lead_ore_swamp", OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.GALENA_ORE.get().defaultBlockState()))
            .count(40)
            .heightRange(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)))
            .veinSize(9)
            .normalizedAirDiscardChance(0F)
    );

    public static final AVPOreData LITHIUM = create(
        builder("lithium_ore", OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.LITHIUM_ORE.get().defaultBlockState()))
            .count(8)
            .heightRange(HeightRangePlacement.uniform(VerticalAnchor.absolute(9), VerticalAnchor.absolute(40)))
            .veinSize(9)
            .normalizedAirDiscardChance(0F)
    );

    public static final AVPOreData LITHIUM_DESERT = create(
        builder("lithium_ore_desert", OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.LITHIUM_ORE.get().defaultBlockState()))
            .count(20)
            .heightRange(HeightRangePlacement.uniform(VerticalAnchor.absolute(9), VerticalAnchor.absolute(64)))
            .veinSize(6)
            .normalizedAirDiscardChance(0F)
    );

    public static final AVPOreData MONAZITE = create(
        builder("monazite_ore", OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.MONAZITE_ORE.get().defaultBlockState()))
            .count(2)
            .heightRange(HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(16)))
            .veinSize(6)
            .normalizedAirDiscardChance(0.5F)
    );

    public static final AVPOreData MONAZITE_JUNGLE = create(
        builder("monazite_ore_jungle", OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.MONAZITE_ORE.get().defaultBlockState()))
            .count(15)
            .heightRange(HeightRangePlacement.triangle(VerticalAnchor.absolute(-48), VerticalAnchor.absolute(48)))
            .veinSize(6)
            .normalizedAirDiscardChance(0F)
    );

    public static final AVPOreData SILICON_GRAVEL = create(
        builder("silicon_gravel", OreConfiguration.target(GRAVEL_REPLACEABLES_RULE, AVPBlocks.SILICA_GRAVEL.get().defaultBlockState()))
            .count(25)
            .heightRange(HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80)))
            .veinSize(13)
            .normalizedAirDiscardChance(0F)
    );

    public static final AVPOreData TITANIUM_LOWER = create(
        builder(
            "titanium_ore_lower",
            OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES_RULE, AVPBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState())
        )
            .count(10)
            .heightRange(HeightRangePlacement.triangle(VerticalAnchor.absolute(-128), VerticalAnchor.absolute(0)))
            .veinSize(9)
            .normalizedAirDiscardChance(0F)
    );

    private static final List<OreConfiguration.TargetBlockState> ZINC_TARGETS = List.of(
        OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES_RULE, AVPBlocks.DEEPSLATE_ZINC_ORE.get().defaultBlockState()),
        OreConfiguration.target(STONE_ORE_REPLACEABLES_RULE, AVPBlocks.ZINC_ORE.get().defaultBlockState())
    );

    public static final AVPOreData ZINC = create(
        builder("zinc_ore", ZINC_TARGETS)
            .count(16)
            .heightRange(HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112)))
            .veinSize(10)
            .normalizedAirDiscardChance(0.0F)
    );

    public static final AVPOreData ZINC_DRIPSTONE_CAVES = create(
        builder("zinc_ore_dripstone_caves", ZINC_TARGETS)
            .count(16)
            .heightRange(HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112)))
            .veinSize(20)
            .normalizedAirDiscardChance(0.0F)
    );

    private static AVPOreData.Builder builder(String name, OreConfiguration.TargetBlockState targetBlockState) {
        return builder(name, List.of(targetBlockState));
    }

    private static AVPOreData.Builder builder(String name, List<OreConfiguration.TargetBlockState> targetBlockStates) {
        return AVPOreData.builder(name, targetBlockStates)
            .biomeFilter(BiomeFilter.biome())
            .spread();
    }

    private static AVPOreData create(AVPOreData.Builder builder) {
        var data = builder.build();
        ORE_DATA.add(data);
        return data;
    }
}
