package com.avp.fabric.data.model;

import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.block.AlienChitinBlocks;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.init.item.AVPSpawnEggItems;
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
                createSlab(generators, block, AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor).get());
                createStairs(generators, block, AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor).get());
            }
        );

        HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var topResourceLocation = TextureMapping.getBlockTexture(block, "_top");

                var baseTextureMapping = TextureMapping.cube(block)
                    .put(TextureSlot.END, topResourceLocation);

                generators.createTrivialBlock(block, baseTextureMapping, ModelTemplates.CUBE_COLUMN);

                createSlab(generators, block, HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor).get());

                createStairs(generators, block, HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor).get());

                createWallCustomTop(
                    generators,
                    block,
                    HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor).get(),
                    topResourceLocation
                );
            }
        );

        createIndustrialGlassSlab(generators);
        generators.family(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS.get())
            .door(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_DOOR.get())
            .stairs(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS.get())
            .trapdoor(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get());
        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            (dyeColor, blockSupplier) -> generators.createTrivialCube(blockSupplier.get())
        );
        createGlassBlocks(
            generators,
            HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS.get(),
            HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_PANE.get()
        );
        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (dyeColor, blockSupplier) -> createGlassBlocks(
                generators,
                HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor).get(),
                blockSupplier.get()
            )
        );

        generators.createRotatedVariantBlock(AlienResinBlocks.IRRADIATED_RESIN.get());
        createSlab(generators, AlienResinBlocks.IRRADIATED_RESIN.get(), AlienResinBlocks.IRRADIATED_RESIN_SLAB.get());
        createStairs(generators, AlienResinBlocks.IRRADIATED_RESIN.get(), AlienResinBlocks.IRRADIATED_RESIN_STAIRS.get());
        generators.createRotatedVariantBlock(AlienResinBlocks.IRRADIATED_RESIN_NODE.get());
        MultiFaceGenerator.generate(generators, AlienResinBlocks.IRRADIATED_RESIN_VEIN.get());
        generators.createCrossBlock(AlienResinBlocks.IRRADIATED_RESIN_WEB.get(), BlockModelGenerators.TintState.NOT_TINTED);

        generators.createRotatedVariantBlock(AlienResinBlocks.ABERRANT_RESIN.get());
        createSlab(generators, AlienResinBlocks.ABERRANT_RESIN.get(), AlienResinBlocks.ABERRANT_RESIN_SLAB.get());
        createStairs(generators, AlienResinBlocks.ABERRANT_RESIN.get(), AlienResinBlocks.ABERRANT_RESIN_STAIRS.get());
        generators.createRotatedVariantBlock(AlienResinBlocks.ABERRANT_RESIN_NODE.get());
        MultiFaceGenerator.generate(generators, AlienResinBlocks.ABERRANT_RESIN_VEIN.get());
        generators.createCrossBlock(AlienResinBlocks.ABERRANT_RESIN_WEB.get(), BlockModelGenerators.TintState.NOT_TINTED);

        generators.family(AlienChitinBlocks.ABERRANT_CHITIN_BLOCK.get())
            .slab(AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_SLAB.get())
            .stairs(AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_STAIRS.get())
            .wall(AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_WALL.get());
        generators.family(AlienChitinBlocks.ABERRANT_CHITIN_BRICKS.get())
            .slab(AlienChitinBlocks.ABERRANT_CHITIN_BRICK_SLAB.get())
            .stairs(AlienChitinBlocks.ABERRANT_CHITIN_BRICK_STAIRS.get())
            .wall(AlienChitinBlocks.ABERRANT_CHITIN_BRICK_WALL.get());
        createBottomTopBlock(
            generators,
            AlienChitinBlocks.CHISELED_ABERRANT_CHITIN_BRICKS.get(),
            AlienChitinBlocks.POLISHED_ABERRANT_CHITIN.get()
        );
        createBottomTopBlock(
            generators,
            AlienChitinBlocks.CHISELED_ABERRANT_CHITIN_BRICKS_EMBRYO.get(),
            AlienChitinBlocks.POLISHED_ABERRANT_CHITIN.get()
        );
        generators.family(AlienChitinBlocks.POLISHED_ABERRANT_CHITIN.get())
            .slab(AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_SLAB.get())
            .stairs(AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_STAIRS.get())
            .wall(AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_WALL.get());

        generators.createRotatedVariantBlock(AlienResinBlocks.NETHER_RESIN.get());
        createSlab(generators, AlienResinBlocks.NETHER_RESIN.get(), AlienResinBlocks.NETHER_RESIN_SLAB.get());
        createStairs(generators, AlienResinBlocks.NETHER_RESIN.get(), AlienResinBlocks.NETHER_RESIN_STAIRS.get());
        generators.createRotatedVariantBlock(AlienResinBlocks.NETHER_RESIN_NODE.get());
        MultiFaceGenerator.generate(generators, AlienResinBlocks.NETHER_RESIN_VEIN.get());
        generators.createCrossBlock(AlienResinBlocks.NETHER_RESIN_WEB.get(), BlockModelGenerators.TintState.NOT_TINTED);

        generators.family(AlienChitinBlocks.NETHER_CHITIN_BLOCK.get())
            .slab(AlienChitinBlocks.NETHER_CHITIN_BLOCK_SLAB.get())
            .stairs(AlienChitinBlocks.NETHER_CHITIN_BLOCK_STAIRS.get())
            .wall(AlienChitinBlocks.NETHER_CHITIN_BLOCK_WALL.get());
        generators.family(AlienChitinBlocks.NETHER_CHITIN_BRICKS.get())
            .slab(AlienChitinBlocks.NETHER_CHITIN_BRICK_SLAB.get())
            .stairs(AlienChitinBlocks.NETHER_CHITIN_BRICK_STAIRS.get())
            .wall(AlienChitinBlocks.NETHER_CHITIN_BRICK_WALL.get());
        createBottomTopBlock(
            generators,
            AlienChitinBlocks.CHISELED_NETHER_CHITIN_BRICKS.get(),
            AlienChitinBlocks.POLISHED_NETHER_CHITIN.get()
        );
        createBottomTopBlock(
            generators,
            AlienChitinBlocks.CHISELED_NETHER_CHITIN_BRICKS_EMBRYO.get(),
            AlienChitinBlocks.POLISHED_NETHER_CHITIN.get()
        );
        generators.family(AlienChitinBlocks.POLISHED_NETHER_CHITIN.get())
            .slab(AlienChitinBlocks.POLISHED_NETHER_CHITIN_SLAB.get())
            .stairs(AlienChitinBlocks.POLISHED_NETHER_CHITIN_STAIRS.get())
            .wall(AlienChitinBlocks.POLISHED_NETHER_CHITIN_WALL.get());

        generators.createRotatedVariantBlock(AlienResinBlocks.RESIN.get());
        createSlab(generators, AlienResinBlocks.RESIN.get(), AlienResinBlocks.RESIN_SLAB.get());
        createStairs(generators, AlienResinBlocks.RESIN.get(), AlienResinBlocks.RESIN_STAIRS.get());
        generators.createRotatedVariantBlock(AlienResinBlocks.RESIN_NODE.get());
        MultiFaceGenerator.generate(generators, AlienResinBlocks.RESIN_VEIN.get());
        generators.createCrossBlock(AlienResinBlocks.RESIN_WEB.get(), BlockModelGenerators.TintState.NOT_TINTED);

        generators.family(AlienChitinBlocks.CHITIN_BLOCK.get())
            .slab(AlienChitinBlocks.CHITIN_BLOCK_SLAB.get())
            .stairs(AlienChitinBlocks.CHITIN_BLOCK_STAIRS.get())
            .wall(AlienChitinBlocks.CHITIN_BLOCK_WALL.get());
        generators.family(AlienChitinBlocks.CHITIN_BRICKS.get())
            .slab(AlienChitinBlocks.CHITIN_BRICK_SLAB.get())
            .stairs(AlienChitinBlocks.CHITIN_BRICK_STAIRS.get())
            .wall(AlienChitinBlocks.CHITIN_BRICK_WALL.get());
        createBottomTopBlock(generators, AlienChitinBlocks.CHISELED_CHITIN_BRICKS.get(), AlienChitinBlocks.POLISHED_CHITIN.get());
        createBottomTopBlock(generators, AlienChitinBlocks.CHISELED_CHITIN_BRICKS_EMBRYO.get(), AlienChitinBlocks.POLISHED_CHITIN.get());
        generators.family(AlienChitinBlocks.POLISHED_CHITIN.get())
            .slab(AlienChitinBlocks.POLISHED_CHITIN_SLAB.get())
            .stairs(AlienChitinBlocks.POLISHED_CHITIN_STAIRS.get())
            .wall(AlienChitinBlocks.POLISHED_CHITIN_WALL.get());

        HumanPaddingBlocks.DYE_COLOR_TO_PADDING.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = HumanPaddingBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor).get();
                var stairBlock = HumanPaddingBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor).get();
                var stairBlock = HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor).get();
                var stairBlock = HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor).get();
                var stairBlock = HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (dyeColor, blockSupplier) -> {
                var block = blockSupplier.get();
                var slabBlock = HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor).get();
                var stairBlock = HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor).get();

                generators.family(block)
                    .slab(slabBlock)
                    .stairs(stairBlock);
            }
        );

        generators.createTrivialCube(AlienBlocks.ROYAL_JELLY_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.ALUMINUM_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.TRINITITE_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.AUTUNITE_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.AUTUNITE_ORE.get());
        generators.createTrivialCube(CoreBlocks.BAUXITE_ORE.get());
        generators.createTrivialCube(CoreBlocks.BRASS_BLOCK.get());
        generators.createTrivialCube(HumanFerroaluminumBlocks.CHISELED_FERROALUMINUM.get());
        generators.createTrivialCube(HumanSteelBlocks.CHISELED_STEEL.get());
        generators.createTrivialCube(HumanTitaniumBlocks.CHISELED_TITANIUM.get());

        generators.family(AlienResinBlocks.ABERRANT_RESIN_BRICKS.get())
            .slab(AlienResinBlocks.ABERRANT_RESIN_BRICK_SLAB.get())
            .stairs(AlienResinBlocks.ABERRANT_RESIN_BRICK_STAIRS.get())
            .wall(AlienResinBlocks.ABERRANT_RESIN_BRICK_WALL.get());
        generators.createTrivialCube(AlienResinBlocks.ABERRANT_RESIN_VENT.get());

        generators.family(AlienResinBlocks.IRRADIATED_RESIN_BRICKS.get())
            .slab(AlienResinBlocks.IRRADIATED_RESIN_BRICK_SLAB.get())
            .stairs(AlienResinBlocks.IRRADIATED_RESIN_BRICK_STAIRS.get())
            .wall(AlienResinBlocks.IRRADIATED_RESIN_BRICK_WALL.get());
        generators.createTrivialCube(AlienResinBlocks.IRRADIATED_RESIN_VENT.get());

        generators.family(AlienResinBlocks.NETHER_RESIN_BRICKS.get())
            .slab(AlienResinBlocks.NETHER_RESIN_BRICK_SLAB.get())
            .stairs(AlienResinBlocks.NETHER_RESIN_BRICK_STAIRS.get())
            .wall(AlienResinBlocks.NETHER_RESIN_BRICK_WALL.get());
        generators.createTrivialCube(AlienResinBlocks.NETHER_RESIN_VENT.get());

        generators.family(AlienResinBlocks.RESIN_BRICKS.get())
            .slab(AlienResinBlocks.RESIN_BRICK_SLAB.get())
            .stairs(AlienResinBlocks.RESIN_BRICK_STAIRS.get())
            .wall(AlienResinBlocks.RESIN_BRICK_WALL.get());
        generators.createTrivialCube(AlienResinBlocks.RESIN_VENT.get());

        createRotatedPillar(generators, AlienResinBlocks.RIBBED_ABERRANT_RESIN.get(), TexturedModel.CUBE);
        createRotatedPillar(generators, AlienResinBlocks.RIBBED_IRRADIATED_RESIN.get(), TexturedModel.CUBE);
        createRotatedPillar(generators, AlienResinBlocks.RIBBED_NETHER_RESIN.get(), TexturedModel.CUBE);
        createRotatedPillar(generators, AlienResinBlocks.RIBBED_RESIN.get(), TexturedModel.CUBE);

        generators.createTrivialCube(AlienResinBlocks.SMOOTH_ABERRANT_RESIN.get());
        generators.createTrivialCube(AlienResinBlocks.SMOOTH_IRRADIATED_RESIN.get());
        generators.createTrivialCube(AlienResinBlocks.SMOOTH_NETHER_RESIN.get());
        generators.createTrivialCube(AlienResinBlocks.SMOOTH_RESIN.get());

        generators.family(HumanFerroaluminumBlocks.CUT_FERROALUMINUM.get())
            .slab(HumanFerroaluminumBlocks.CUT_FERROALUMINUM_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.CUT_FERROALUMINUM_STAIRS.get());

        generators.family(HumanSteelBlocks.CUT_STEEL.get())
            .slab(HumanSteelBlocks.CUT_STEEL_SLAB.get())
            .stairs(HumanSteelBlocks.CUT_STEEL_STAIRS.get());

        generators.family(HumanTitaniumBlocks.CUT_TITANIUM.get())
            .slab(HumanTitaniumBlocks.CUT_TITANIUM_SLAB.get())
            .stairs(HumanTitaniumBlocks.CUT_TITANIUM_STAIRS.get());

        generators.createTrivialCube(CoreBlocks.DEEPSLATE_TITANIUM_ORE.get());
        generators.createTrivialCube(CoreBlocks.DEEPSLATE_ZINC_ORE.get());
        BarsGenerator.generate(generators, HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            HumanFerroaluminumBlocks.FERROALUMINUM_COLUMN.get(),
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
        generators.createTrivialCube(CoreBlocks.GALENA_ORE.get());
        generators.createTrivialCube(CoreBlocks.LEAD_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.LITHIUM_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.LITHIUM_ORE.get());
        generators.createTrivialCube(CoreBlocks.MONAZITE_ORE.get());
        generators.createTrivialCube(CoreBlocks.RAW_BAUXITE_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.RAW_GALENA_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.RAW_MONAZITE_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.RAW_TITANIUM_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.RAW_ZINC_BLOCK.get());
        generators.createCrossBlock(AVPBlocks.RAZOR_WIRE.get(), BlockModelGenerators.TintState.NOT_TINTED);
        generators.createTrivialCube(CoreBlocks.SILICA_GRAVEL.get());
        generators.createTrivialCube(CoreBlocks.SILICON_BLOCK.get());
        BarsGenerator.generate(generators, HumanSteelBlocks.STEEL_BARS.get());
        BarsGenerator.generate(generators, HumanSteelBlocks.STEEL_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            HumanSteelBlocks.STEEL_COLUMN.get(),
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
        BarsGenerator.generate(generators, HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE.get());
        generators.createRotatedPillarWithHorizontalVariant(
            HumanTitaniumBlocks.TITANIUM_COLUMN.get(),
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
        generators.createTrivialCube(CoreBlocks.URANIUM_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.ZINC_BLOCK.get());
        generators.createTrivialCube(CoreBlocks.ZINC_ORE.get());

        generators.family(HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK.get())
            .slab(HumanFerroaluminumBlocks.FERROALUMINUM_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.FERROALUMINUM_STAIRS.get())
            .pressurePlate(HumanFerroaluminumBlocks.FERROALUMINUM_PRESSURE_PLATE.get())
            .button(HumanFerroaluminumBlocks.FERROALUMINUM_BUTTON.get())
            .door(HumanFerroaluminumBlocks.FERROALUMINUM_DOOR.get())
            .trapdoor(HumanFerroaluminumBlocks.FERROALUMINUM_TRAP_DOOR.get());

        generators.family(HumanSteelBlocks.STEEL_BLOCK.get())
            .slab(HumanSteelBlocks.STEEL_SLAB.get())
            .stairs(HumanSteelBlocks.STEEL_STAIRS.get())
            .pressurePlate(HumanSteelBlocks.STEEL_PRESSURE_PLATE.get())
            .button(HumanSteelBlocks.STEEL_BUTTON.get())
            .door(HumanSteelBlocks.STEEL_DOOR.get())
            .trapdoor(HumanSteelBlocks.STEEL_TRAP_DOOR.get());
        generators.family(HumanTitaniumBlocks.TITANIUM_BLOCK.get())
            .slab(HumanTitaniumBlocks.TITANIUM_SLAB.get())
            .stairs(HumanTitaniumBlocks.TITANIUM_STAIRS.get())
            .pressurePlate(HumanTitaniumBlocks.TITANIUM_PRESSURE_PLATE.get())
            .button(HumanTitaniumBlocks.TITANIUM_BUTTON.get())
            .door(HumanTitaniumBlocks.TITANIUM_DOOR.get())
            .trapdoor(HumanTitaniumBlocks.TITANIUM_TRAP_DOOR.get());

        generators.family(HumanFerroaluminumBlocks.FERROALUMINUM_SIDING.get())
            .slab(HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_STAIRS.get());

        generators.family(HumanSteelBlocks.STEEL_SIDING.get())
            .slab(HumanSteelBlocks.STEEL_SIDING_SLAB.get())
            .stairs(HumanSteelBlocks.STEEL_SIDING_STAIRS.get());

        generators.family(HumanTitaniumBlocks.TITANIUM_SIDING.get())
            .slab(HumanTitaniumBlocks.TITANIUM_SIDING_SLAB.get())
            .stairs(HumanTitaniumBlocks.TITANIUM_SIDING_STAIRS.get());

        generators.family(HumanFerroaluminumBlocks.FERROALUMINUM_STANDING.get())
            .slab(HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_STAIRS.get());

        generators.family(HumanSteelBlocks.STEEL_STANDING.get())
            .slab(HumanSteelBlocks.STEEL_STANDING_SLAB.get())
            .stairs(HumanSteelBlocks.STEEL_STANDING_STAIRS.get());

        generators.family(HumanTitaniumBlocks.TITANIUM_STANDING.get())
            .slab(HumanTitaniumBlocks.TITANIUM_STANDING_SLAB.get())
            .stairs(HumanTitaniumBlocks.TITANIUM_STANDING_STAIRS.get());

        generators.family(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING.get())
            .slab(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get());

        generators.family(HumanSteelBlocks.STEEL_FASTENED_SIDING.get())
            .slab(HumanSteelBlocks.STEEL_FASTENED_SIDING_SLAB.get())
            .stairs(HumanSteelBlocks.STEEL_FASTENED_SIDING_STAIRS.get());

        generators.family(HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING.get())
            .slab(HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_SLAB.get())
            .stairs(HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get());

        generators.family(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING.get())
            .slab(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get());

        generators.family(HumanSteelBlocks.STEEL_FASTENED_STANDING.get())
            .slab(HumanSteelBlocks.STEEL_FASTENED_STANDING_SLAB.get())
            .stairs(HumanSteelBlocks.STEEL_FASTENED_STANDING_STAIRS.get());

        generators.family(HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING.get())
            .slab(HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_SLAB.get())
            .stairs(HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get());

        generators.family(HumanFerroaluminumBlocks.FERROALUMINUM_PLATING.get())
            .slab(HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_STAIRS.get());

        generators.family(HumanSteelBlocks.STEEL_PLATING.get())
            .slab(HumanSteelBlocks.STEEL_PLATING_SLAB.get())
            .stairs(HumanSteelBlocks.STEEL_PLATING_STAIRS.get());

        generators.family(HumanTitaniumBlocks.TITANIUM_PLATING.get())
            .slab(HumanTitaniumBlocks.TITANIUM_PLATING_SLAB.get())
            .stairs(HumanTitaniumBlocks.TITANIUM_PLATING_STAIRS.get());

        generators.family(HumanFerroaluminumBlocks.FERROALUMINUM_TREAD.get())
            .slab(HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_STAIRS.get());

        generators.family(HumanSteelBlocks.STEEL_TREAD.get())
            .slab(HumanSteelBlocks.STEEL_TREAD_SLAB.get())
            .stairs(HumanSteelBlocks.STEEL_TREAD_STAIRS.get());

        generators.family(HumanTitaniumBlocks.TITANIUM_TREAD.get())
            .slab(HumanTitaniumBlocks.TITANIUM_TREAD_SLAB.get())
            .stairs(HumanTitaniumBlocks.TITANIUM_TREAD_STAIRS.get());

        generators.family(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE.get())
            .slab(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_SLAB.get())
            .stairs(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_STAIRS.get());

        generators.family(HumanSteelBlocks.STEEL_GRATE.get())
            .slab(HumanSteelBlocks.STEEL_GRATE_SLAB.get())
            .stairs(HumanSteelBlocks.STEEL_GRATE_STAIRS.get());

        generators.family(HumanTitaniumBlocks.TITANIUM_GRATE.get())
            .slab(HumanTitaniumBlocks.TITANIUM_GRATE_SLAB.get())
            .stairs(HumanTitaniumBlocks.TITANIUM_GRATE_STAIRS.get());

        var spawnEggLocation = ModelLocationUtils.decorateItemModelLocation("template_spawn_egg");

        AVPSpawnEggItems.getAll()
            .forEach(spawnEggItem -> generators.delegateItemModel(spawnEggItem.get(), spawnEggLocation));
    }

    private void createBottomTopBlock(BlockModelGenerators generators, Block block, Block yBlock) {
        var yResourceLocation = ModelLocationUtils.getModelLocation(yBlock);
        var chiseledTextureResourceLocation = BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/").withSuffix("_side");

        var textureMapping = TextureMapping.cube(block)
            .put(TextureSlot.BOTTOM, yResourceLocation)
            .put(TextureSlot.SIDE, chiseledTextureResourceLocation)
            .put(TextureSlot.TOP, yResourceLocation);

        generators.createTrivialBlock(block, textureMapping, ModelTemplates.CUBE_BOTTOM_TOP);
    }

    private void createRotatedPillar(BlockModelGenerators generators, Block rotatedPillarBlock, TexturedModel.Provider modelProvider) {
        var resourceLocation = modelProvider.create(rotatedPillarBlock, generators.modelOutput);
        generators.blockStateOutput.accept(
            BlockModelGenerators.createRotatedPillarWithHorizontalVariant(rotatedPillarBlock, resourceLocation, resourceLocation)
        );
    }

    private void createGlassBlocks(BlockModelGenerators generators, Block block, Block block2) {
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

    private void createSlab(
        BlockModelGenerators generators,
        Block baseBlock,
        Block slabBlock
    ) {
        var resourceLocation = ModelLocationUtils.getModelLocation(baseBlock);
        var textureMapping = TextureMapping.cube(baseBlock)
            .put(TextureSlot.BOTTOM, resourceLocation)
            .put(TextureSlot.TOP, resourceLocation);

        var bottom = ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, generators.modelOutput);
        var top = ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, generators.modelOutput);

        generators.blockStateOutput.accept(
            BlockModelGenerators.createSlab(slabBlock, bottom, top, resourceLocation)
        );
    }

    private void createStairs(BlockModelGenerators generators, Block baseBlock, Block stairsBlock) {
        var resourceLocation = ModelLocationUtils.getModelLocation(baseBlock);

        var textureMapping = TextureMapping.cube(baseBlock)
            .put(TextureSlot.BOTTOM, resourceLocation)
            .put(TextureSlot.TOP, resourceLocation);

        var innerResourceLocation = ModelTemplates.STAIRS_INNER.create(stairsBlock, textureMapping, generators.modelOutput);
        var straightResourceLocation = ModelTemplates.STAIRS_STRAIGHT.create(stairsBlock, textureMapping, generators.modelOutput);
        var outerResourceLocation = ModelTemplates.STAIRS_OUTER.create(stairsBlock, textureMapping, generators.modelOutput);

        generators.blockStateOutput.accept(
            BlockModelGenerators.createStairs(stairsBlock, innerResourceLocation, straightResourceLocation, outerResourceLocation)
        );
    }

    private void createWall(
        BlockModelGenerators generators,
        Block block,
        Block wallBlock
    ) {
        createWallCustomTop(generators, block, wallBlock, TextureMapping.getBlockTexture(block));
    }

    private void createWallCustomTop(
        BlockModelGenerators generators,
        Block block,
        Block wallBlock,
        ResourceLocation topResourceLocation
    ) {
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
        var block = HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS.get();
        var slabBlock = HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB.get();
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
