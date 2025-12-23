package com.blib.common.gameplay.structure;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public abstract class JigsawBackedStructure extends Structure {

    protected final JigsawStructure backingJigsawStructure;

    protected final Holder<StructureTemplatePool> startPool;

    protected final Optional<ResourceLocation> startJigsawName;

    protected final int maxDepth;

    protected final HeightProvider startHeight;

    protected final boolean useExpansionHack;

    protected final Optional<Heightmap.Types> projectStartToHeightmap;

    protected final int maxDistanceFromCenter;

    protected final List<PoolAliasBinding> poolAliases;

    protected final DimensionPadding dimensionPadding;

    protected final LiquidSettings liquidSettings;

    protected JigsawBackedStructure(
        Structure.StructureSettings structureSettings,
        Holder<StructureTemplatePool> startPool,
        Optional<ResourceLocation> startJigsawName,
        int maxDepth,
        HeightProvider startHeight,
        boolean useExpansionHack,
        Optional<Heightmap.Types> projectStartToHeightmap,
        int maxDistanceFromCenter,
        List<PoolAliasBinding> poolAliases,
        DimensionPadding dimensionPadding,
        LiquidSettings liquidSettings
    ) {
        super(structureSettings);

        this.backingJigsawStructure = new JigsawStructure(
            structureSettings,
            startPool,
            startJigsawName,
            maxDepth,
            startHeight,
            useExpansionHack,
            projectStartToHeightmap,
            maxDistanceFromCenter,
            poolAliases,
            dimensionPadding,
            liquidSettings
        );
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = poolAliases;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext generationContext) {
        return backingJigsawStructure.findGenerationPoint(generationContext);
    }
}
