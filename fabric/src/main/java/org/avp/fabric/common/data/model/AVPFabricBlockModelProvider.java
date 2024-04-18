package org.avp.fabric.common.data.model;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import org.avp.api.Holder;
import org.avp.api.Tuple;
import org.avp.api.block.BlockData;
import org.avp.api.block.factory.BlockFactories;
import org.avp.api.block.factory.FenceGateBlockFactory;
import org.avp.api.block.factory.StairBlockFactory;
import org.avp.common.item.AVPSpawnEggItems;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPFabricBlockModelProvider {

    public static void addBlockModels(BlockModelGenerators generator) {
        AVPDeferredBlockRegistry.getDataEntries().forEach(tuple -> computeBlockModels(generator, tuple));

        // Listen, I don't like this any more than you do. But Mojang also does this, so...
        AVPSpawnEggItems.INSTANCE.getEntries()
            .forEach(
                holder -> generator.delegateItemModel(
                    holder.get(),
                    ModelLocationUtils.decorateItemModelLocation("template_spawn_egg")
                )
            );
    }

    private static void computeBlockModels(BlockModelGenerators generator, Tuple<Holder<Block>, BlockData> tuple) {
        var blockHolder = tuple.first();
        var blockData = tuple.second();
        var parentHolder = blockData.getParent();
        var factory = blockData.getFactory();

        // Generate the corresponding model depending on the type of factory used to create the block.
        if (factory == BlockFactories.CUBE || factory == BlockFactories.TRANSPARENT) {
            generator.createTrivialCube(blockHolder.get());
        } else if (factory == BlockFactories.FENCE) {
            if (parentHolder == null) {
                throw new IllegalStateException("Null parent block for fence block!");
            }
            createFenceBlockModel(generator, parentHolder.get(), blockHolder.get());
        } else if (factory instanceof FenceGateBlockFactory) {
            if (parentHolder == null) {
                throw new IllegalStateException("Null parent block for fence gate block!");
            }
            createFenceGateBlockModel(generator, parentHolder.get(), blockHolder.get());
        } else if (factory == BlockFactories.GRASS) {
            if (parentHolder == null) {
                throw new IllegalStateException("Null parent block for grass block!");
            }
            createGrassLikeBlock(generator, parentHolder.get(), blockHolder.get());
        } else if (factory == BlockFactories.ROTATED_PILLAR) {
            createPillarBlockModel(generator, blockHolder.get());
        } else if (factory == BlockFactories.ROTATED_VARIANT) {
            createRotatedVariantBlock(generator, blockHolder.get());
        } else if (factory == BlockFactories.SLAB) {
            if (parentHolder == null) {
                throw new IllegalStateException("Null parent block for slab block!");
            }
            createSlabBlockModel(generator, parentHolder.get(), blockHolder.get());
        } else if (factory instanceof StairBlockFactory) {
            if (parentHolder == null) {
                throw new IllegalStateException("Null parent block for stair block!");
            }
            createStairBlockModel(generator, parentHolder.get(), blockHolder.get());
        } else if (factory == BlockFactories.WALL) {
            if (parentHolder == null) {
                throw new IllegalStateException("Null parent block for wall block!");
            }
            createWallBlockModel(generator, parentHolder.get(), blockHolder.get());
        } else if (factory == BlockFactories.WOOD) {
            if (parentHolder == null) {
                throw new IllegalStateException("Null parent block for wood block!");
            }
            createWoodBlockModel(generator, parentHolder.get(), blockHolder.get());
        }
    }

    private static void createFenceBlockModel(BlockModelGenerators generator, Block baseBlock, Block fenceBlock) {
        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var postResourceLocation =
            ModelTemplates.FENCE_POST.create(fenceBlock, textureMapping, generator.modelOutput);
        var sideResourceLocation =
            ModelTemplates.FENCE_SIDE.create(fenceBlock, textureMapping, generator.modelOutput);
        var inventoryResourceLocation =
            ModelTemplates.FENCE_INVENTORY.create(fenceBlock, textureMapping, generator.modelOutput);

        generator.delegateItemModel(fenceBlock.asItem(), inventoryResourceLocation);
        generator.blockStateOutput.accept(
            BlockModelGenerators.createFence(fenceBlock, postResourceLocation, sideResourceLocation)
        );
    }

    private static void createFenceGateBlockModel(BlockModelGenerators generator, Block baseBlock, Block fenceGateBlock) {
        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var openResourceLocation =
            ModelTemplates.FENCE_GATE_OPEN.create(fenceGateBlock, textureMapping, generator.modelOutput);
        var closeResourceLocation =
            ModelTemplates.FENCE_GATE_CLOSED.create(fenceGateBlock, textureMapping, generator.modelOutput);
        var wallOpenResourceLocation =
            ModelTemplates.FENCE_GATE_WALL_OPEN.create(fenceGateBlock, textureMapping, generator.modelOutput);
        var wallCloseResourceLocation =
            ModelTemplates.FENCE_GATE_WALL_CLOSED.create(fenceGateBlock, textureMapping, generator.modelOutput);

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

    private static void createGrassLikeBlock(BlockModelGenerators generator, Block dirtBlock, Block grassBlock) {
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

    private static void createPillarBlockModel(BlockModelGenerators generator, Block baseBlock) {
        var logMapping = TextureMapping.logColumn(baseBlock);
        var columnResourceLocation = ModelTemplates.CUBE_COLUMN.create(baseBlock, logMapping, generator.modelOutput);
        var horizontalResourceLocation = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(baseBlock, logMapping, generator.modelOutput);
        generator.blockStateOutput.accept(
            BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
                baseBlock,
                columnResourceLocation,
                horizontalResourceLocation
            )
        );
    }

    private static void createRotatedVariantBlock(BlockModelGenerators generator, Block baseBlock) {
        ResourceLocation resourceLocation = TexturedModel.CUBE.create(baseBlock, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createRotatedVariant(baseBlock, resourceLocation));
    }

    private static void createSlabBlockModel(BlockModelGenerators generator, Block baseBlock, Block slabBlock) {
        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var bottomResourceLocation =
            ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, generator.modelOutput);
        var topResourceLocation =
            ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, generator.modelOutput);
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

    private static void createStairBlockModel(BlockModelGenerators generator, Block baseBlock, Block stairBlock) {
        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var straightResourceLocation =
            ModelTemplates.STAIRS_STRAIGHT.create(stairBlock, textureMapping, generator.modelOutput);
        var innerResourceLocation =
            ModelTemplates.STAIRS_INNER.create(stairBlock, textureMapping, generator.modelOutput);
        var outerResourceLocation =
            ModelTemplates.STAIRS_OUTER.create(stairBlock, textureMapping, generator.modelOutput);

        generator.blockStateOutput.accept(
            BlockModelGenerators.createStairs(
                stairBlock,
                innerResourceLocation,
                straightResourceLocation,
                outerResourceLocation
            )
        );
    }

    private static void createWallBlockModel(BlockModelGenerators generator, Block baseBlock, Block wallBlock) {
        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var postResourceLocation =
            ModelTemplates.WALL_POST.create(wallBlock, textureMapping, generator.modelOutput);
        var lowResourceLocation =
            ModelTemplates.WALL_LOW_SIDE.create(wallBlock, textureMapping, generator.modelOutput);
        var tallResourceLocation =
            ModelTemplates.WALL_TALL_SIDE.create(wallBlock, textureMapping, generator.modelOutput);
        var inventoryResourceLocation =
            ModelTemplates.WALL_INVENTORY.create(wallBlock, textureMapping, generator.modelOutput);

        generator.delegateItemModel(wallBlock.asItem(), inventoryResourceLocation);
        generator.blockStateOutput.accept(
            BlockModelGenerators.createWall(wallBlock, postResourceLocation, lowResourceLocation, tallResourceLocation)
        );
    }

    private static void createWoodBlockModel(BlockModelGenerators generator, Block baseBlock, Block woodBlock) {
        var logMapping = TextureMapping.logColumn(baseBlock);
        var textureMapping = logMapping.copyAndUpdate(TextureSlot.END, logMapping.get(TextureSlot.SIDE));
        var resourceLocation = ModelTemplates.CUBE_COLUMN.create(woodBlock, textureMapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(woodBlock, resourceLocation));
    }

    private AVPFabricBlockModelProvider() {
        throw new UnsupportedOperationException();
    }
}
