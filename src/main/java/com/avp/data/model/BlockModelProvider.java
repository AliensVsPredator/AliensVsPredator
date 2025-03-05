package com.avp.data.model;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.BlockProperties;
import com.avp.common.item.SpawnEggItems;
import com.avp.data.model.generator.BarsGenerator;
import com.avp.data.model.generator.MultiFaceGenerator;

public class BlockModelProvider extends FabricModelProvider {

    public BlockModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        BlockProperties.DYE_COLOR_TO_CONCRETE_BLOCKS.forEach(
            (dyeColor, block) -> {
                var resourceLocation = ModelLocationUtils.getModelLocation(block);

                var textureMapping = TextureMapping.cube(block)
                    .put(TextureSlot.BOTTOM, resourceLocation)
                    .put(TextureSlot.TOP, resourceLocation);

                createConcreteSlab(generators, dyeColor, textureMapping, resourceLocation);

                createConcreteStairs(generators, dyeColor, textureMapping);
            }
        );

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (dyeColor, block) -> {
                var resourceLocation = ModelLocationUtils.getModelLocation(block);
                var topResourceLocation = TextureMapping.getBlockTexture(block, "_top");

                var baseTextureMapping = TextureMapping.cube(block)
                    .put(TextureSlot.END, topResourceLocation);

                var textureMapping = TextureMapping.cube(block)
                    .put(TextureSlot.BOTTOM, topResourceLocation)
                    .put(TextureSlot.TOP, topResourceLocation);

                generators.createTrivialBlock(block, baseTextureMapping, ModelTemplates.CUBE_COLUMN);

                createIndustrialConcreteSlab(generators, dyeColor, textureMapping, resourceLocation);

                createIndustrialConcreteStairs(generators, dyeColor, textureMapping);

                createIndustrialConcreteWall(generators, dyeColor, block, topResourceLocation);
            }
        );

        generators.createTrivialCube(AVPBlocks.INDUSTRIAL_GLASS);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach((dyeColor, block) -> generators.createTrivialCube(block));
        createGlassBlocks(generators, AVPBlocks.INDUSTRIAL_GLASS, AVPBlocks.INDUSTRIAL_GLASS_PANE);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (dyeColor, block) -> createGlassBlocks(generators, AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor), block)
        );

        generators.createRotatedVariantBlock(AVPBlocks.NETHER_RESIN);
        generators.createRotatedVariantBlock(AVPBlocks.NETHER_RESIN_NODE);
        MultiFaceGenerator.generate(generators, AVPBlocks.NETHER_RESIN_VEIN);
        generators.createCrossBlock(AVPBlocks.NETHER_RESIN_WEB, BlockModelGenerators.TintState.NOT_TINTED);

        generators.createRotatedVariantBlock(AVPBlocks.RESIN);
        generators.createRotatedVariantBlock(AVPBlocks.RESIN_NODE);
        MultiFaceGenerator.generate(generators, AVPBlocks.RESIN_VEIN);
        generators.createCrossBlock(AVPBlocks.RESIN_WEB, BlockModelGenerators.TintState.NOT_TINTED);

        AVPBlocks.DYE_COLOR_TO_PADDING.forEach(
            (dyeColor, block) -> {
                var slabBlock = AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor);
                var stairBlock = AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor);

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (dyeColor, block) -> {
                var slabBlock = AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor);
                var stairBlock = AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor);

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (dyeColor, block) -> {
                var slabBlock = AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor);
                var stairBlock = AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor);

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (dyeColor, block) -> {
                var slabBlock = AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor);
                var stairBlock = AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor);

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (dyeColor, block) -> {
                var slabBlock = AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor);
                var stairBlock = AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor);

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        generators.createTrivialCube(AVPBlocks.ALUMINUM_BLOCK);
        generators.createTrivialCube(AVPBlocks.AUTUNITE_BLOCK);
        generators.createTrivialCube(AVPBlocks.AUTUNITE_ORE);
        generators.createTrivialCube(AVPBlocks.BAUXITE_ORE);
        generators.createTrivialCube(AVPBlocks.BRASS_BLOCK);
        generators.createTrivialCube(AVPBlocks.CHISELED_FERROALUMINUM);
        generators.createTrivialCube(AVPBlocks.CHISELED_STEEL);
        generators.createTrivialCube(AVPBlocks.CHISELED_TITANIUM);

        generators.family(AVPBlocks.CUT_FERROALUMINUM)
            .slab(AVPBlocks.CUT_FERROALUMINUM_SLAB)
            .stairs(AVPBlocks.CUT_FERROALUMINUM_STAIRS);

        generators.family(AVPBlocks.CUT_STEEL)
            .slab(AVPBlocks.CUT_STEEL_SLAB)
            .stairs(AVPBlocks.CUT_STEEL_STAIRS);

        generators.family(AVPBlocks.CUT_TITANIUM)
            .slab(AVPBlocks.CUT_TITANIUM_SLAB)
            .stairs(AVPBlocks.CUT_TITANIUM_STAIRS);

        generators.createTrivialCube(AVPBlocks.DEEPSLATE_TITANIUM_ORE);
        generators.createTrivialCube(AVPBlocks.DEEPSLATE_ZINC_ORE);
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_BLOCK);
        BarsGenerator.generate(generators, AVPBlocks.FERROALUMINUM_CHAIN_FENCE);
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.FERROALUMINUM_COLUMN,
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_GRATE);
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_PLATING);
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_SIDING);
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_STANDING);
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_TREAD);
        generators.createTrivialCube(AVPBlocks.GALENA_ORE);
        generators.createTrivialCube(AVPBlocks.LEAD_BLOCK);
        generators.createTrivialCube(AVPBlocks.LITHIUM_BLOCK);
        generators.createTrivialCube(AVPBlocks.LITHIUM_ORE);
        generators.createTrivialCube(AVPBlocks.MONAZITE_ORE);
        generators.createTrivialCube(AVPBlocks.RAW_BAUXITE_BLOCK);
        generators.createTrivialCube(AVPBlocks.RAW_GALENA_BLOCK);
        generators.createTrivialCube(AVPBlocks.RAW_MONAZITE_BLOCK);
        generators.createTrivialCube(AVPBlocks.RAW_SILICA_BLOCK);
        generators.createTrivialCube(AVPBlocks.RAW_TITANIUM_BLOCK);
        generators.createTrivialCube(AVPBlocks.RAW_ZINC_BLOCK);
        generators.createCrossBlock(AVPBlocks.RAZOR_WIRE, BlockModelGenerators.TintState.NOT_TINTED);
        generators.createTrivialCube(AVPBlocks.SILICA_GRAVEL);
        BarsGenerator.generate(generators, AVPBlocks.STEEL_BARS);
        generators.createTrivialCube(AVPBlocks.STEEL_BLOCK);
        BarsGenerator.generate(generators, AVPBlocks.STEEL_CHAIN_FENCE);
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.STEEL_COLUMN,
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        generators.createTrivialCube(AVPBlocks.STEEL_FASTENED_SIDING);
        generators.createTrivialCube(AVPBlocks.STEEL_FASTENED_STANDING);
        generators.createTrivialCube(AVPBlocks.STEEL_GRATE);
        generators.createTrivialCube(AVPBlocks.STEEL_PLATING);
        generators.createTrivialCube(AVPBlocks.STEEL_SIDING);
        generators.createTrivialCube(AVPBlocks.STEEL_STANDING);
        generators.createTrivialCube(AVPBlocks.STEEL_TREAD);
        generators.createTrivialCube(AVPBlocks.TITANIUM_BLOCK);
        BarsGenerator.generate(generators, AVPBlocks.TITANIUM_CHAIN_FENCE);
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.TITANIUM_COLUMN,
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        generators.createTrivialCube(AVPBlocks.TITANIUM_FASTENED_SIDING);
        generators.createTrivialCube(AVPBlocks.TITANIUM_FASTENED_STANDING);
        generators.createTrivialCube(AVPBlocks.TITANIUM_GRATE);
        generators.createTrivialCube(AVPBlocks.TITANIUM_PLATING);
        generators.createTrivialCube(AVPBlocks.TITANIUM_SIDING);
        generators.createTrivialCube(AVPBlocks.TITANIUM_STANDING);
        generators.createTrivialCube(AVPBlocks.TITANIUM_TREAD);
        generators.createTrivialCube(AVPBlocks.URANIUM_BLOCK);
        generators.createTrivialCube(AVPBlocks.ZINC_BLOCK);
        generators.createTrivialCube(AVPBlocks.ZINC_ORE);

        var spawnEggLocation = ModelLocationUtils.decorateItemModelLocation("template_spawn_egg");

        SpawnEggItems.getAll()
            .forEach(spawnEggItem -> generators.delegateItemModel(spawnEggItem, spawnEggLocation));
    }

    public final void createGlassBlocks(BlockModelGenerators generators, Block block, Block block2) {
        TextureMapping textureMapping = TextureMapping.pane(block, block2);
        ResourceLocation resourceLocation = ModelTemplates.STAINED_GLASS_PANE_POST.create(block2, textureMapping, generators.modelOutput);
        ResourceLocation resourceLocation2 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(block2, textureMapping, generators.modelOutput);
        ResourceLocation resourceLocation3 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(
            block2,
            textureMapping,
            generators.modelOutput
        );
        ResourceLocation resourceLocation4 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(
            block2,
            textureMapping,
            generators.modelOutput
        );
        ResourceLocation resourceLocation5 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(
            block2,
            textureMapping,
            generators.modelOutput
        );
        Item item = block2.asItem();
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(block), generators.modelOutput);
        generators.blockStateOutput
            .accept(
                MultiPartGenerator.multiPart(block2)
                    .with(Variant.variant().with(VariantProperties.MODEL, resourceLocation))
                    .with(
                        Condition.condition().term(BlockStateProperties.NORTH, true),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocation2)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.EAST, true),
                        Variant.variant()
                            .with(VariantProperties.MODEL, resourceLocation2)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.SOUTH, true),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocation3)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.WEST, true),
                        Variant.variant()
                            .with(VariantProperties.MODEL, resourceLocation3)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.NORTH, false),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocation4)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.EAST, false),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocation5)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.SOUTH, false),
                        Variant.variant()
                            .with(VariantProperties.MODEL, resourceLocation5)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.WEST, false),
                        Variant.variant()
                            .with(VariantProperties.MODEL, resourceLocation4)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                    )
            );
    }

    private void createConcreteSlab(
        BlockModelGenerators generators,
        DyeColor dyeColor,
        TextureMapping textureMapping,
        ResourceLocation resourceLocation
    ) {
        var slabBlock = AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor);
        var bottom = ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, generators.modelOutput);
        var top = ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, generators.modelOutput);

        generators.blockStateOutput.accept(
            BlockModelGenerators.createSlab(slabBlock, bottom, top, resourceLocation)
        );
    }

    private void createConcreteStairs(BlockModelGenerators generators, DyeColor dyeColor, TextureMapping textureMapping) {
        var stairBlock = AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor);

        var innerResourceLocation = ModelTemplates.STAIRS_INNER.create(stairBlock, textureMapping, generators.modelOutput);
        var straightResourceLocation = ModelTemplates.STAIRS_STRAIGHT.create(stairBlock, textureMapping, generators.modelOutput);
        var outerResourceLocation = ModelTemplates.STAIRS_OUTER.create(stairBlock, textureMapping, generators.modelOutput);

        generators.blockStateOutput.accept(
            BlockModelGenerators.createStairs(stairBlock, innerResourceLocation, straightResourceLocation, outerResourceLocation)
        );
    }

    private void createIndustrialConcreteSlab(
        BlockModelGenerators generators,
        DyeColor dyeColor,
        TextureMapping textureMapping,
        ResourceLocation resourceLocation
    ) {
        var slabBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor);
        var bottom = ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, generators.modelOutput);
        var top = ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, generators.modelOutput);

        generators.blockStateOutput.accept(
            BlockModelGenerators.createSlab(slabBlock, bottom, top, resourceLocation)
        );
    }

    private void createIndustrialConcreteStairs(BlockModelGenerators generators, DyeColor dyeColor, TextureMapping textureMapping) {
        var stairBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor);

        var innerResourceLocation = ModelTemplates.STAIRS_INNER.create(stairBlock, textureMapping, generators.modelOutput);
        var straightResourceLocation = ModelTemplates.STAIRS_STRAIGHT.create(stairBlock, textureMapping, generators.modelOutput);
        var outerResourceLocation = ModelTemplates.STAIRS_OUTER.create(stairBlock, textureMapping, generators.modelOutput);

        generators.blockStateOutput.accept(
            BlockModelGenerators.createStairs(stairBlock, innerResourceLocation, straightResourceLocation, outerResourceLocation)
        );
    }

    private void createIndustrialConcreteWall(
        BlockModelGenerators generators,
        DyeColor dyeColor,
        Block block,
        ResourceLocation topResourceLocation
    ) {
        var wallBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor);

        var wallTextureMapping = TextureMapping.cube(block)
            .put(TextureSlot.TOP, topResourceLocation);
        var wallTopTextureMapping = TextureMapping.cube(block)
            .put(TextureSlot.WALL, topResourceLocation);
        var postResourceLocation = ModelTemplates.WALL_POST.create(
            wallBlock,
            wallTopTextureMapping,
            generators.modelOutput
        );
        var lowSideResourceLocation = AVPModelTemplates.WALL_LOW_SIDE.create(
            wallBlock,
            wallTextureMapping,
            generators.modelOutput
        );
        var tallSideResourceLocation = AVPModelTemplates.WALL_TALL_SIDE.create(
            wallBlock,
            wallTextureMapping,
            generators.modelOutput
        );

        generators.blockStateOutput.accept(
            BlockModelGenerators.createWall(wallBlock, postResourceLocation, lowSideResourceLocation, tallSideResourceLocation)
        );

        var inventoryResourceLocation = AVPModelTemplates.WALL_INVENTORY.create(
            wallBlock,
            wallTextureMapping,
            generators.modelOutput
        );
        generators.delegateItemModel(wallBlock, inventoryResourceLocation);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {}

    @Override
    public @NotNull String getName() {
        return "Block Model Definitions";
    }
}
