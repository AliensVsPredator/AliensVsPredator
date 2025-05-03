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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.BlockProperties;
import com.avp.common.item.AVPSpawnEggItems;
import com.avp.fabric.data.model.generator.BarsGenerator;
import com.avp.fabric.data.model.generator.MultiFaceGenerator;

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

        createIndustrialGlassSlab(generators);
        generators.family(AVPBlocks.INDUSTRIAL_GLASS.get())
            .door(AVPBlocks.INDUSTRIAL_GLASS_DOOR.get())
            .stairs(AVPBlocks.INDUSTRIAL_GLASS_STAIRS.get())
            .trapdoor(AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach((dyeColor, blockSupplier) -> generators.createTrivialCube(blockSupplier.get()));
        createGlassBlocks(generators, AVPBlocks.INDUSTRIAL_GLASS.get(), AVPBlocks.INDUSTRIAL_GLASS_PANE.get());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (dyeColor, blockSupplier) -> createGlassBlocks(
                generators,
                AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor).get(),
                blockSupplier.get()
            )
        );

        generators.createRotatedVariantBlock(AVPBlocks.IRRADIATED_RESIN.get());
        generators.createRotatedVariantBlock(AVPBlocks.IRRADIATED_RESIN_NODE.get());
        MultiFaceGenerator.generate(generators, AVPBlocks.IRRADIATED_RESIN_VEIN.get());
        generators.createCrossBlock(AVPBlocks.IRRADIATED_RESIN_WEB.get(), BlockModelGenerators.TintState.NOT_TINTED);

        generators.createRotatedVariantBlock(AVPBlocks.ABERRANT_RESIN.get());
        generators.createRotatedVariantBlock(AVPBlocks.ABERRANT_RESIN_NODE.get());
        MultiFaceGenerator.generate(generators, AVPBlocks.ABERRANT_RESIN_VEIN.get());
        generators.createCrossBlock(AVPBlocks.ABERRANT_RESIN_WEB.get(), BlockModelGenerators.TintState.NOT_TINTED);

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

        generators.createTrivialCube(AVPBlocks.ROYAL_JELLY_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.ALUMINUM_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.TRINITITE_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.AUTUNITE_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.AUTUNITE_ORE.get());
        generators.createTrivialCube(AVPBlocks.BAUXITE_ORE.get());
        generators.createTrivialCube(AVPBlocks.BRASS_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.CHISELED_FERROALUMINUM.get());
        generators.createTrivialCube(AVPBlocks.CHISELED_STEEL.get());
        generators.createTrivialCube(AVPBlocks.CHISELED_TITANIUM.get());
        generators.createTrivialCube(AVPBlocks.RESIN_BRICKS.get());
        generators.createTrivialCube(AVPBlocks.RESIN_O.get());
        generators.createTrivialCube(AVPBlocks.RESIN_RIBBED.get());
        generators.createTrivialCube(AVPBlocks.RESIN_SMOOTH.get());

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
        BarsGenerator.generate(generators, AVPBlocks.FERROALUMINUM_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.FERROALUMINUM_COLUMN.get(),
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        // generators.createTrivialCube(AVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        // generators.createTrivialCube(AVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        // generators.createTrivialCube(AVPBlocks.FERROALUMINUM_GRATE);
        // generators.createTrivialCube(AVPBlocks.FERROALUMINUM_PLATING);
        // generators.createTrivialCube(AVPBlocks.FERROALUMINUM_SIDING);
        // generators.createTrivialCube(AVPBlocks.FERROALUMINUM_STANDING);
        // generators.createTrivialCube(AVPBlocks.FERROALUMINUM_TREAD);
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
        BarsGenerator.generate(generators, AVPBlocks.STEEL_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.STEEL_COLUMN.get(),
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        // generators.createTrivialCube(AVPBlocks.STEEL_FASTENED_SIDING);
        // generators.createTrivialCube(AVPBlocks.STEEL_FASTENED_STANDING);
        // generators.createTrivialCube(AVPBlocks.STEEL_GRATE);
        // generators.createTrivialCube(AVPBlocks.STEEL_PLATING);
        // generators.createTrivialCube(AVPBlocks.STEEL_SIDING);
        // generators.createTrivialCube(AVPBlocks.STEEL_STANDING);
        // generators.createTrivialCube(AVPBlocks.STEEL_TREAD);
        BarsGenerator.generate(generators, AVPBlocks.TITANIUM_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            AVPBlocks.TITANIUM_COLUMN.get(),
            TexturedModel.COLUMN_ALT,
            TexturedModel.COLUMN_HORIZONTAL_ALT
        );
        // generators.createTrivialCube(AVPBlocks.TITANIUM_FASTENED_SIDING);
        // generators.createTrivialCube(AVPBlocks.TITANIUM_FASTENED_STANDING);
        // generators.createTrivialCube(AVPBlocks.TITANIUM_GRATE);
        // generators.createTrivialCube(AVPBlocks.TITANIUM_PLATING);
        // generators.createTrivialCube(AVPBlocks.TITANIUM_SIDING);
        // generators.createTrivialCube(AVPBlocks.TITANIUM_STANDING);
        // generators.createTrivialCube(AVPBlocks.TITANIUM_TREAD);
        generators.createTrivialCube(AVPBlocks.URANIUM_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.ZINC_BLOCK.get());
        generators.createTrivialCube(AVPBlocks.ZINC_ORE.get());

        generators.family(AVPBlocks.FERROALUMINUM_BLOCK.get())
            .slab(AVPBlocks.FERROALUMINUM_SLAB.get())
            .stairs(AVPBlocks.FERROALUMINUM_STAIRS.get())
            .pressurePlate(AVPBlocks.FERROALUMINUM_PRESSURE_PLATE.get())
            .button(AVPBlocks.FERROALUMINUM_BUTTON.get())
            .door(AVPBlocks.FERROALUMINUM_DOOR.get())
            .trapdoor(AVPBlocks.FERROALUMINUM_TRAP_DOOR.get());

        generators.family(AVPBlocks.STEEL_BLOCK.get())
            .slab(AVPBlocks.STEEL_SLAB.get())
            .stairs(AVPBlocks.STEEL_STAIRS.get())
            .pressurePlate(AVPBlocks.STEEL_PRESSURE_PLATE.get())
            .button(AVPBlocks.STEEL_BUTTON.get())
            .door(AVPBlocks.STEEL_DOOR.get())
            .trapdoor(AVPBlocks.STEEL_TRAP_DOOR.get());
        generators.family(AVPBlocks.TITANIUM_BLOCK.get())
            .slab(AVPBlocks.TITANIUM_SLAB.get())
            .stairs(AVPBlocks.TITANIUM_STAIRS.get())
            .pressurePlate(AVPBlocks.TITANIUM_PRESSURE_PLATE.get())
            .button(AVPBlocks.TITANIUM_BUTTON.get())
            .door(AVPBlocks.TITANIUM_DOOR.get())
            .trapdoor(AVPBlocks.TITANIUM_TRAP_DOOR.get());

        generators.family(AVPBlocks.FERROALUMINUM_SIDING.get())
            .slab(AVPBlocks.FERROALUMINUM_SIDING_SLAB.get())
            .stairs(AVPBlocks.FERROALUMINUM_SIDING_STAIRS.get());

        generators.family(AVPBlocks.STEEL_SIDING.get())
            .slab(AVPBlocks.STEEL_SIDING_SLAB.get())
            .stairs(AVPBlocks.STEEL_SIDING_STAIRS.get());

        generators.family(AVPBlocks.TITANIUM_SIDING.get())
            .slab(AVPBlocks.TITANIUM_SIDING_SLAB.get())
            .stairs(AVPBlocks.TITANIUM_SIDING_STAIRS.get());

        generators.family(AVPBlocks.FERROALUMINUM_STANDING.get())
            .slab(AVPBlocks.FERROALUMINUM_STANDING_SLAB.get())
            .stairs(AVPBlocks.FERROALUMINUM_STANDING_STAIRS.get());

        generators.family(AVPBlocks.STEEL_STANDING.get())
            .slab(AVPBlocks.STEEL_STANDING_SLAB.get())
            .stairs(AVPBlocks.STEEL_STANDING_STAIRS.get());

        generators.family(AVPBlocks.TITANIUM_STANDING.get())
            .slab(AVPBlocks.TITANIUM_STANDING_SLAB.get())
            .stairs(AVPBlocks.TITANIUM_STANDING_STAIRS.get());

        generators.family(AVPBlocks.FERROALUMINUM_FASTENED_SIDING.get())
            .slab(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get())
            .stairs(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get());

        generators.family(AVPBlocks.STEEL_FASTENED_SIDING.get())
            .slab(AVPBlocks.STEEL_FASTENED_SIDING_SLAB.get())
            .stairs(AVPBlocks.STEEL_FASTENED_SIDING_STAIRS.get());

        generators.family(AVPBlocks.TITANIUM_FASTENED_SIDING.get())
            .slab(AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB.get())
            .stairs(AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get());

        generators.family(AVPBlocks.FERROALUMINUM_FASTENED_STANDING.get())
            .slab(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get())
            .stairs(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get());

        generators.family(AVPBlocks.STEEL_FASTENED_STANDING.get())
            .slab(AVPBlocks.STEEL_FASTENED_STANDING_SLAB.get())
            .stairs(AVPBlocks.STEEL_FASTENED_STANDING_STAIRS.get());

        generators.family(AVPBlocks.TITANIUM_FASTENED_STANDING.get())
            .slab(AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB.get())
            .stairs(AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get());

        generators.family(AVPBlocks.FERROALUMINUM_PLATING.get())
            .slab(AVPBlocks.FERROALUMINUM_PLATING_SLAB.get())
            .stairs(AVPBlocks.FERROALUMINUM_PLATING_STAIRS.get());

        generators.family(AVPBlocks.STEEL_PLATING.get())
            .slab(AVPBlocks.STEEL_PLATING_SLAB.get())
            .stairs(AVPBlocks.STEEL_PLATING_STAIRS.get());

        generators.family(AVPBlocks.TITANIUM_PLATING.get())
            .slab(AVPBlocks.TITANIUM_PLATING_SLAB.get())
            .stairs(AVPBlocks.TITANIUM_PLATING_STAIRS.get());

        generators.family(AVPBlocks.FERROALUMINUM_TREAD.get())
            .slab(AVPBlocks.FERROALUMINUM_TREAD_SLAB.get())
            .stairs(AVPBlocks.FERROALUMINUM_TREAD_STAIRS.get());

        generators.family(AVPBlocks.STEEL_TREAD.get())
            .slab(AVPBlocks.STEEL_TREAD_SLAB.get())
            .stairs(AVPBlocks.STEEL_TREAD_STAIRS.get());

        generators.family(AVPBlocks.TITANIUM_TREAD.get())
            .slab(AVPBlocks.TITANIUM_TREAD_SLAB.get())
            .stairs(AVPBlocks.TITANIUM_TREAD_STAIRS.get());

        generators.family(AVPBlocks.FERROALUMINUM_GRATE.get())
            .slab(AVPBlocks.FERROALUMINUM_GRATE_SLAB.get())
            .stairs(AVPBlocks.FERROALUMINUM_GRATE_STAIRS.get());

        generators.family(AVPBlocks.STEEL_GRATE.get())
            .slab(AVPBlocks.STEEL_GRATE_SLAB.get())
            .stairs(AVPBlocks.STEEL_GRATE_STAIRS.get());

        generators.family(AVPBlocks.TITANIUM_GRATE.get())
            .slab(AVPBlocks.TITANIUM_GRATE_SLAB.get())
            .stairs(AVPBlocks.TITANIUM_GRATE_STAIRS.get());

        var spawnEggLocation = ModelLocationUtils.decorateItemModelLocation("template_spawn_egg");

        AVPSpawnEggItems.getAll()
            .forEach(spawnEggItem -> generators.delegateItemModel(spawnEggItem.get(), spawnEggLocation));
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

    private void createIndustrialGlassSlab(BlockModelGenerators generators) {
        var block = AVPBlocks.INDUSTRIAL_GLASS.get();
        var slabBlock = AVPBlocks.INDUSTRIAL_GLASS_SLAB.get();
        var textureMapping = TextureMapping.cube(block);
        var textureMapping2 = TextureMapping.column(
            TextureMapping.getBlockTexture(slabBlock, "_side"),
            textureMapping.get(TextureSlot.TOP)
        );
        var bottomResourceLocation = ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping2, generators.modelOutput);
        var topResourceLocation = ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping2, generators.modelOutput);
        var columnResourceLocation = ModelTemplates.CUBE_COLUMN.createWithOverride(
            slabBlock,
            "_double",
            textureMapping2,
            generators.modelOutput
        );

        generators.blockStateOutput.accept(
            BlockModelGenerators.createSlab(slabBlock, bottomResourceLocation, topResourceLocation, columnResourceLocation)
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {}

    @Override
    public @NotNull String getName() {
        return "Block Model Definitions";
    }
}
