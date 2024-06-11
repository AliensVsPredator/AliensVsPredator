package org.avp.fabric.common.data.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.function.Function;

import org.avp.api.data.block.BlockData;
import org.avp.api.data.block.BlockModelDataType;
import org.avp.api.registry.AVPDeferredBlockRegistry;
import org.avp.common.registry.item.AVPSpawnEggItemRegistry;

import static net.minecraft.data.models.BlockModelGenerators.MULTIFACE_GENERATOR;

public class AVPFabricBlockModelProvider {

    public static void addBlockModels(BlockModelGenerators generator) {
        AVPDeferredBlockRegistry.getDataEntries().forEach(tuple -> computeBlockModels(generator, tuple.first().get(), tuple.second()));

        // Listen, I don't like this any more than you do. But Mojang also does this, so...
        AVPSpawnEggItemRegistry.INSTANCE.getValues()
            .forEach(
                holder -> generator.delegateItemModel(
                    holder.get(),
                    ModelLocationUtils.decorateItemModelLocation("template_spawn_egg")
                )
            );
    }

    private static void computeBlockModels(BlockModelGenerators generator, Block block, BlockData blockData) {
        var blockModelDataType = blockData.blockModelData().blockModelDataTypeFactory().apply(block);
        var genType = blockModelDataType.getGenType();

        switch (genType) {
            case CUBE -> generateCubeBlockModel(generator, (BlockModelDataType.Cube) blockModelDataType);
            case GRASS_LIKE -> generateGrassLikeBlockModel(generator, (BlockModelDataType.GrassLike) blockModelDataType);
            case FENCE -> generateFenceBlockModel(generator, (BlockModelDataType.Fence) blockModelDataType);
            case FENCE_GATE -> generateFenceGateBlockModel(generator, (BlockModelDataType.FenceGate) blockModelDataType);
            case MULTI_FACE -> generateMultiFaceBlockModel(generator, (BlockModelDataType.MultiFace) blockModelDataType);
            case ROTATED_PILLAR -> generateRotatedPillarBlockModel(generator, (BlockModelDataType.RotatedPillar) blockModelDataType);
            case ROTATED_VARIANT -> generateRotatedVariantBlockModel(generator, (BlockModelDataType.RotatedVariant) blockModelDataType);
            case SLAB -> generateSlabBlockModel(generator, (BlockModelDataType.Slab) blockModelDataType);
            case STAIR -> generateStairBlockModel(generator, (BlockModelDataType.Stair) blockModelDataType);
            case WALL -> generateWallBlockModel(generator, (BlockModelDataType.Wall) blockModelDataType);
            case WOOD -> generateWoodBlockModel(generator, (BlockModelDataType.Wood) blockModelDataType);
            default -> throw new IllegalStateException("Unhandled genType " + genType);
        }
    }

    private static void generateCubeBlockModel(BlockModelGenerators generator, BlockModelDataType.Cube provider) {
        generator.createTrivialBlock(provider.block(), TexturedModel.CUBE);
    }

    private static void generateGrassLikeBlockModel(BlockModelGenerators generator, BlockModelDataType.GrassLike provider) {
        var dirtBlock = provider.dirtBlock();
        var grassBlock = provider.grassBlock();
        ResourceLocation resourceLocation = TextureMapping.getBlockTexture(dirtBlock);
        TextureMapping textureMapping2 = new TextureMapping()
            .put(TextureSlot.BOTTOM, resourceLocation)
            .copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE)
            .put(TextureSlot.TOP, TextureMapping.getBlockTexture(grassBlock, "_top"))
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(grassBlock, "_snow"));
        Variant variant = Variant.variant()
            .with(
                VariantProperties.MODEL,
                ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(
                    grassBlock,
                    "_snow",
                    textureMapping2,
                    generator.modelOutput
                )
            );
        generator.createGrassLikeBlock(grassBlock, ModelLocationUtils.getModelLocation(grassBlock), variant);
    }

    private static void generateFenceBlockModel(BlockModelGenerators generator, BlockModelDataType.Fence provider) {
        var baseBlock = provider.baseBlock();
        var fenceBlock = provider.fenceBlock();
        var modelOutput = generator.modelOutput;

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var postResourceLocation =
            ModelTemplates.FENCE_POST.create(fenceBlock, textureMapping, modelOutput);
        var sideResourceLocation =
            ModelTemplates.FENCE_SIDE.create(fenceBlock, textureMapping, modelOutput);
        var inventoryResourceLocation =
            ModelTemplates.FENCE_INVENTORY.create(fenceBlock, textureMapping, modelOutput);

        generator.delegateItemModel(fenceBlock.asItem(), inventoryResourceLocation);
        generator.blockStateOutput.accept(
            BlockModelGenerators.createFence(fenceBlock, postResourceLocation, sideResourceLocation)
        );
    }

    private static void generateFenceGateBlockModel(BlockModelGenerators generator, BlockModelDataType.FenceGate provider) {
        var baseBlock = provider.baseBlock();
        var fenceGateBlock = provider.fenceGateBlock();
        var modelOutput = generator.modelOutput;

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var openResourceLocation =
            ModelTemplates.FENCE_GATE_OPEN.create(fenceGateBlock, textureMapping, modelOutput);
        var closeResourceLocation =
            ModelTemplates.FENCE_GATE_CLOSED.create(fenceGateBlock, textureMapping, modelOutput);
        var wallOpenResourceLocation =
            ModelTemplates.FENCE_GATE_WALL_OPEN.create(fenceGateBlock, textureMapping, modelOutput);
        var wallCloseResourceLocation =
            ModelTemplates.FENCE_GATE_WALL_CLOSED.create(fenceGateBlock, textureMapping, modelOutput);

        generator.blockStateOutput.accept(
            BlockModelGenerators.createFenceGate(
                fenceGateBlock,
                openResourceLocation,
                closeResourceLocation,
                wallOpenResourceLocation,
                wallCloseResourceLocation,
                true
            )
        );
    }

    private static void generateMultiFaceBlockModel(BlockModelGenerators generator, BlockModelDataType.MultiFace provider) {
        var block = provider.baseBlock();

        generator.createSimpleFlatItemModel(block);
        ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(block);
        MultiPartGenerator multipartgenerator = MultiPartGenerator.multiPart(block);
        Condition.TerminalCondition terminalCondition = Util.make(
            Condition.condition(),
            condition -> MULTIFACE_GENERATOR.stream().map(Pair::getFirst).forEach(booleanProperty -> {
                if (block.defaultBlockState().hasProperty(booleanProperty)) {
                    condition.term(booleanProperty, false);
                }
            })
        );

        for (Pair<BooleanProperty, Function<ResourceLocation, Variant>> pair : MULTIFACE_GENERATOR) {
            BooleanProperty booleanproperty = pair.getFirst();
            Function<ResourceLocation, Variant> function = pair.getSecond();
            if (block.defaultBlockState().hasProperty(booleanproperty)) {
                multipartgenerator.with(Condition.condition().term(booleanproperty, true), function.apply(resourcelocation));
                multipartgenerator.with(terminalCondition, function.apply(resourcelocation));
            }
        }

        generator.blockStateOutput.accept(multipartgenerator);
    }

    private static void generateRotatedPillarBlockModel(BlockModelGenerators generator, BlockModelDataType.RotatedPillar provider) {
        var baseBlock = provider.block();
        var modelOutput = generator.modelOutput;

        var logMapping = TextureMapping.logColumn(baseBlock);
        var columnResourceLocation = ModelTemplates.CUBE_COLUMN.create(baseBlock, logMapping, modelOutput);
        var horizontalResourceLocation = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(baseBlock, logMapping, modelOutput);
        generator.blockStateOutput.accept(
            BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
                baseBlock,
                columnResourceLocation,
                horizontalResourceLocation
            )
        );
    }

    private static void generateRotatedVariantBlockModel(BlockModelGenerators generator, BlockModelDataType.RotatedVariant provider) {
        var baseBlock = provider.block();
        var modelOutput = generator.modelOutput;

        ResourceLocation resourceLocation = TexturedModel.CUBE.create(baseBlock, modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createRotatedVariant(baseBlock, resourceLocation));
    }

    private static void generateSlabBlockModel(BlockModelGenerators generator, BlockModelDataType.Slab provider) {
        var baseBlock = provider.baseBlock();
        var slabBlock = provider.slabBlock();
        var modelOutput = generator.modelOutput;

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var bottomResourceLocation =
            ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, modelOutput);
        var topResourceLocation =
            ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, modelOutput);
        var doubleResourceLocation =
            ModelTemplates.CUBE.getDefaultModelLocation(baseBlock);

        generator.blockStateOutput.accept(
            BlockModelGenerators.createSlab(
                slabBlock,
                bottomResourceLocation,
                topResourceLocation,
                doubleResourceLocation
            )
        );
    }

    private static void generateStairBlockModel(BlockModelGenerators generator, BlockModelDataType.Stair provider) {
        var baseBlock = provider.baseBlock();
        var stairBlock = provider.stairBlock();
        var modelOutput = generator.modelOutput;

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var straightResourceLocation =
            ModelTemplates.STAIRS_STRAIGHT.create(stairBlock, textureMapping, modelOutput);
        var innerResourceLocation =
            ModelTemplates.STAIRS_INNER.create(stairBlock, textureMapping, modelOutput);
        var outerResourceLocation =
            ModelTemplates.STAIRS_OUTER.create(stairBlock, textureMapping, modelOutput);

        generator.blockStateOutput.accept(
            BlockModelGenerators.createStairs(
                stairBlock,
                innerResourceLocation,
                straightResourceLocation,
                outerResourceLocation
            )
        );
    }

    private static void generateWallBlockModel(BlockModelGenerators generator, BlockModelDataType.Wall provider) {
        var baseBlock = provider.baseBlock();
        var wallBlock = provider.wallBlock();
        var modelOutput = generator.modelOutput;

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var postResourceLocation =
            ModelTemplates.WALL_POST.create(wallBlock, textureMapping, modelOutput);
        var lowResourceLocation =
            ModelTemplates.WALL_LOW_SIDE.create(wallBlock, textureMapping, modelOutput);
        var tallResourceLocation =
            ModelTemplates.WALL_TALL_SIDE.create(wallBlock, textureMapping, modelOutput);
        var inventoryResourceLocation =
            ModelTemplates.WALL_INVENTORY.create(wallBlock, textureMapping, modelOutput);

        generator.delegateItemModel(wallBlock.asItem(), inventoryResourceLocation);
        generator.blockStateOutput.accept(
            BlockModelGenerators.createWall(wallBlock, postResourceLocation, lowResourceLocation, tallResourceLocation)
        );
    }

    private static void generateWoodBlockModel(BlockModelGenerators generator, BlockModelDataType.Wood provider) {
        var baseBlock = provider.baseBlock();
        var woodBlock = provider.woodBlock();
        var modelOutput = generator.modelOutput;

        var logMapping = TextureMapping.logColumn(baseBlock);
        var textureMapping = logMapping.copyAndUpdate(TextureSlot.END, logMapping.get(TextureSlot.SIDE));
        var resourceLocation = ModelTemplates.CUBE_COLUMN.create(woodBlock, textureMapping, modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(woodBlock, resourceLocation));
    }

    private AVPFabricBlockModelProvider() {
        throw new UnsupportedOperationException();
    }
}
