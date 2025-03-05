package com.avp.fabric.data.model;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.block.BlockProperties;
import com.avp.core.common.item.SpawnEggItems;
import com.avp.fabric.data.model.generator.BarsGenerator;
import com.avp.fabric.data.model.generator.MultiFaceGenerator;

import java.util.function.Supplier;

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
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
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

        generators.createTrivialCube(AVPBlocks.INDUSTRIAL_GLASS.get());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach((dyeColor, blockSupplier) -> generators.createTrivialCube(blockSupplier.get()));
        createGlassBlocks(generators, AVPBlocks.INDUSTRIAL_GLASS, AVPBlocks.INDUSTRIAL_GLASS_PANE);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (dyeColor, blockSupplier) -> createGlassBlocks(generators, AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor), blockSupplier)
        );

        generators.createRotatedVariantBlock(AVPBlocks.NETHER_RESIN.get());
        generators.createRotatedVariantBlock(AVPBlocks.NETHER_RESIN_NODE.get());
        MultiFaceGenerator.generate(generators, AVPBlocks.NETHER_RESIN_VEIN.get());
        generators.createCrossBlock(AVPBlocks.NETHER_RESIN_WEB.get(), BlockModelGenerators.TintState.NOT_TINTED);

        generators.createRotatedVariantBlock(AVPBlocks.RESIN.get());
        generators.createRotatedVariantBlock(AVPBlocks.RESIN_NODE.get());
        MultiFaceGenerator.generate(generators, AVPBlocks.RESIN_VEIN.get());
        generators.createCrossBlock(AVPBlocks.RESIN_WEB.get(), BlockModelGenerators.TintState.NOT_TINTED);

        AVPBlocks.DYE_COLOR_TO_PADDING.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor).get();
                var stairBlock = AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor).get();
                var stairBlock = AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor).get();
                var stairBlock = AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor).get();
                var stairBlock = AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor).get();
                var stairBlock = AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        generators.createTrivialCube(AVPBlocks.ALUMINUM_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.AUTUNITE_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.AUTUNITE_ORE.get());
        generators.createTrivialCube(AVPBlocks.BAUXITE_ORE.get());
        generators.createTrivialCube(AVPBlocks.BRASS_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.CHISELED_FERROALUMINUM.get());
        generators.createTrivialCube(AVPBlocks.CHISELED_STEEL.get());
        generators.createTrivialCube(AVPBlocks.CHISELED_TITANIUM.get());

        generators.family(AVPBlocks.CUT_FERROALUMINUM.get())
            .slab(AVPBlocks.CUT_FERROALUMINUM_SLAB.get())
            .stairs(AVPBlocks.CUT_FERROALUMINUM_STAIRS.get());

        generators.family(AVPBlocks.CUT_STEEL.get())
            .slab(AVPBlocks.CUT_STEEL_SLAB.get())
            .stairs(AVPBlocks.CUT_STEEL_STAIRS.get());

        generators.family(AVPBlocks.CUT_TITANIUM.get())
            .slab(AVPBlocks.CUT_TITANIUM_SLAB.get())
            .stairs(AVPBlocks.CUT_TITANIUM_STAIRS.get());

        generators.createTrivialCube(AVPBlocks.DEEPSLATE_TITANIUM_ORE.get());
        generators.createTrivialCube(AVPBlocks.DEEPSLATE_ZINC_ORE.get());
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_BLOCK.get());
        BarsGenerator.generate(generators, AVPBlocks.FERROALUMINUM_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.FERROALUMINUM_COLUMN.get(),
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_FASTENED_SIDING.get());
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_FASTENED_STANDING.get());
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_GRATE.get());
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_PLATING.get());
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_SIDING.get());
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_STANDING.get());
        generators.createTrivialCube(AVPBlocks.FERROALUMINUM_TREAD.get());
        generators.createTrivialCube(AVPBlocks.GALENA_ORE.get());
        generators.createTrivialCube(AVPBlocks.LEAD_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.LITHIUM_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.LITHIUM_ORE.get());
        generators.createTrivialCube(AVPBlocks.MONAZITE_ORE.get());
        generators.createTrivialCube(AVPBlocks.RAW_BAUXITE_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.RAW_GALENA_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.RAW_MONAZITE_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.RAW_SILICA_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.RAW_TITANIUM_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.RAW_ZINC_BLOCK.get());
        generators.createCrossBlock(AVPBlocks.RAZOR_WIRE.get(), BlockModelGenerators.TintState.NOT_TINTED);
        generators.createTrivialCube(AVPBlocks.SILICA_GRAVEL.get());
        BarsGenerator.generate(generators, AVPBlocks.STEEL_BARS.get());
        generators.createTrivialCube(AVPBlocks.STEEL_BLOCK.get());
        BarsGenerator.generate(generators, AVPBlocks.STEEL_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.STEEL_COLUMN.get(),
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        generators.createTrivialCube(AVPBlocks.STEEL_FASTENED_SIDING.get());
        generators.createTrivialCube(AVPBlocks.STEEL_FASTENED_STANDING.get());
        generators.createTrivialCube(AVPBlocks.STEEL_GRATE.get());
        generators.createTrivialCube(AVPBlocks.STEEL_PLATING.get());
        generators.createTrivialCube(AVPBlocks.STEEL_SIDING.get());
        generators.createTrivialCube(AVPBlocks.STEEL_STANDING.get());
        generators.createTrivialCube(AVPBlocks.STEEL_TREAD.get());
        generators.createTrivialCube(AVPBlocks.TITANIUM_BLOCK.get());
        BarsGenerator.generate(generators, AVPBlocks.TITANIUM_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.TITANIUM_COLUMN.get(),
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        generators.createTrivialCube(AVPBlocks.TITANIUM_FASTENED_SIDING.get());
        generators.createTrivialCube(AVPBlocks.TITANIUM_FASTENED_STANDING.get());
        generators.createTrivialCube(AVPBlocks.TITANIUM_GRATE.get());
        generators.createTrivialCube(AVPBlocks.TITANIUM_PLATING.get());
        generators.createTrivialCube(AVPBlocks.TITANIUM_SIDING.get());
        generators.createTrivialCube(AVPBlocks.TITANIUM_STANDING.get());
        generators.createTrivialCube(AVPBlocks.TITANIUM_TREAD.get());
        generators.createTrivialCube(AVPBlocks.URANIUM_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.ZINC_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.ZINC_ORE.get());

        var spawnEggLocation = ModelLocationUtils.decorateItemModelLocation("template_spawn_egg");

        SpawnEggItems.getAll()
            .forEach(spawnEggItemSupplier -> generators.delegateItemModel(spawnEggItemSupplier.get(), spawnEggLocation));
    }

    public final void createGlassBlocks(BlockModelGenerators generators, Supplier<Block> blockSupplier, Supplier<Block> block2Supplier) {
        var block = blockSupplier.get();
        var block2 = block2Supplier.get();

        var textureMapping = TextureMapping.pane(block, block2);
        var resourceLocation = ModelTemplates.STAINED_GLASS_PANE_POST.create(block2, textureMapping, generators.modelOutput);
        var resourceLocation2 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(block2, textureMapping, generators.modelOutput);
        var resourceLocation3 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(
            block2,
            textureMapping,
            generators.modelOutput
        );
        var resourceLocation4 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(
            block2,
            textureMapping,
            generators.modelOutput
        );
        var resourceLocation5 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(
            block2,
            textureMapping,
            generators.modelOutput
        );
        var item = block2.asItem();
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
        var slabBlock = AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor).get();
        var bottom = ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, generators.modelOutput);
        var top = ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, generators.modelOutput);

        generators.blockStateOutput.accept(
            BlockModelGenerators.createSlab(slabBlock, bottom, top, resourceLocation)
        );
    }

    private void createConcreteStairs(BlockModelGenerators generators, DyeColor dyeColor, TextureMapping textureMapping) {
        var stairBlock = AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor).get();

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
        var slabBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor).get();
        var bottom = ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, generators.modelOutput);
        var top = ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, generators.modelOutput);

        generators.blockStateOutput.accept(
            BlockModelGenerators.createSlab(slabBlock, bottom, top, resourceLocation)
        );
    }

    private void createIndustrialConcreteStairs(BlockModelGenerators generators, DyeColor dyeColor, TextureMapping textureMapping) {
        var stairBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor).get();

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
        var wallBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor).get();

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
