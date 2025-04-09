package com.avp.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.block.AVPBlocks;
import com.avp.data.compatibility.gigeresque.GigeresqueConstants;

public class AVPBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public AVPBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var concreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.CONCRETE);

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().forEach(concreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().forEach(concreteTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.FERROALUMINUM)
            .add(
                AVPBlocks.CHISELED_FERROALUMINUM,
                AVPBlocks.CUT_FERROALUMINUM,
                AVPBlocks.CUT_FERROALUMINUM_SLAB,
                AVPBlocks.CUT_FERROALUMINUM_STAIRS,
                AVPBlocks.FERROALUMINUM_BLOCK,
                AVPBlocks.FERROALUMINUM_CHAIN_FENCE,
                AVPBlocks.FERROALUMINUM_COLUMN,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING,
                AVPBlocks.FERROALUMINUM_GRATE,
                AVPBlocks.FERROALUMINUM_PLATING,
                AVPBlocks.FERROALUMINUM_SIDING,
                AVPBlocks.FERROALUMINUM_STANDING,
                AVPBlocks.FERROALUMINUM_TREAD,
                AVPBlocks.FERROALUMINUM_DOOR,
                AVPBlocks.FERROALUMINUM_TRAP_DOOR,
                AVPBlocks.FERROALUMINUM_PRESSURE_PLATE,
                AVPBlocks.FERROALUMINUM_BUTTON,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS,
                AVPBlocks.FERROALUMINUM_GRATE_SLAB,
                AVPBlocks.FERROALUMINUM_GRATE_STAIRS,
                AVPBlocks.FERROALUMINUM_PLATING_SLAB,
                AVPBlocks.FERROALUMINUM_PLATING_STAIRS,
                AVPBlocks.FERROALUMINUM_SIDING_SLAB,
                AVPBlocks.FERROALUMINUM_SIDING_STAIRS,
                AVPBlocks.FERROALUMINUM_SLAB,
                AVPBlocks.FERROALUMINUM_STAIRS,
                AVPBlocks.FERROALUMINUM_STANDING_SLAB,
                AVPBlocks.FERROALUMINUM_STANDING_STAIRS,
                AVPBlocks.FERROALUMINUM_TREAD_SLAB,
                AVPBlocks.FERROALUMINUM_TREAD_STAIRS
            );

        var industrialConcreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_CONCRETE);

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values().forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().forEach(industrialConcreteTagBuilder::add);

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(AVPBlocks.INDUSTRIAL_GLASS);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().forEach(industrialGlassBlockTagBuilder::add);

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(AVPBlocks.INDUSTRIAL_GLASS_PANE);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().forEach(industrialGlassPaneTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_PANE);

        var paddingTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PADDING);

        AVPBlocks.DYE_COLOR_TO_PADDING.values().forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().forEach(paddingTagBuilder::add);

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.values().forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().forEach(paddingTagBuilder::add);

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.values().forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().forEach(paddingTagBuilder::add);

        var plasticTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PLASTIC);

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.values().forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().forEach(plasticTagBuilder::add);

        AVPBlocks.DYE_COLOR_TO_PLASTIC.values().forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().forEach(plasticTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.IRRADIATED_RESIN)
            .add(
                AVPBlocks.IRRADIATED_RESIN,
                AVPBlocks.IRRADIATED_RESIN_NODE,
                AVPBlocks.IRRADIATED_RESIN_VEIN,
                AVPBlocks.IRRADIATED_RESIN_WEB
            );

        getOrCreateTagBuilder(AVPBlockTags.ABERRANT_RESIN)
            .add(
                AVPBlocks.ABERRANT_RESIN,
                AVPBlocks.ABERRANT_RESIN_NODE,
                AVPBlocks.ABERRANT_RESIN_VEIN,
                AVPBlocks.ABERRANT_RESIN_WEB
            );

        getOrCreateTagBuilder(AVPBlockTags.NETHER_RESIN)
            .add(
                AVPBlocks.NETHER_RESIN,
                AVPBlocks.NETHER_RESIN_NODE,
                AVPBlocks.NETHER_RESIN_VEIN,
                AVPBlocks.NETHER_RESIN_WEB
            );

        getOrCreateTagBuilder(AVPBlockTags.NORMAL_RESIN)
            .add(
                AVPBlocks.RESIN,
                AVPBlocks.RESIN_NODE,
                AVPBlocks.RESIN_VEIN,
                AVPBlocks.RESIN_WEB
            );

        getOrCreateTagBuilder(AVPBlockTags.CRAFTED_RESIN)
            .add(
                AVPBlocks.RESIN_BRICKS,
                AVPBlocks.RESIN_O,
                AVPBlocks.RESIN_RIBBED,
                AVPBlocks.RESIN_SMOOTH
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN)
            .addTag(AVPBlockTags.ABERRANT_RESIN)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.CRAFTED_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.RESIN_VEINS)
            .add(
                AVPBlocks.NETHER_RESIN_VEIN,
                AVPBlocks.RESIN_VEIN,
                AVPBlocks.ABERRANT_RESIN_VEIN,
                AVPBlocks.IRRADIATED_RESIN_VEIN
            );

        getOrCreateTagBuilder(AVPBlockTags.STEEL)
            .add(
                AVPBlocks.CHISELED_STEEL,
                AVPBlocks.CUT_STEEL,
                AVPBlocks.CUT_STEEL_SLAB,
                AVPBlocks.CUT_STEEL_STAIRS,
                AVPBlocks.STEEL_BARS,
                AVPBlocks.STEEL_BLOCK,
                AVPBlocks.STEEL_CHAIN_FENCE,
                AVPBlocks.STEEL_COLUMN,
                AVPBlocks.STEEL_FASTENED_SIDING,
                AVPBlocks.STEEL_FASTENED_SIDING_SLAB,
                AVPBlocks.STEEL_FASTENED_SIDING_STAIRS,
                AVPBlocks.STEEL_FASTENED_STANDING,
                AVPBlocks.STEEL_FASTENED_STANDING_SLAB,
                AVPBlocks.STEEL_FASTENED_STANDING_STAIRS,
                AVPBlocks.STEEL_GRATE,
                AVPBlocks.STEEL_GRATE_SLAB,
                AVPBlocks.STEEL_GRATE_STAIRS,
                AVPBlocks.STEEL_PLATING,
                AVPBlocks.STEEL_PLATING_SLAB,
                AVPBlocks.STEEL_PLATING_STAIRS,
                AVPBlocks.STEEL_PRESSURE_PLATE,
                AVPBlocks.STEEL_SIDING,
                AVPBlocks.STEEL_SIDING_SLAB,
                AVPBlocks.STEEL_SIDING_STAIRS,
                AVPBlocks.STEEL_SLAB,
                AVPBlocks.STEEL_STAIRS,
                AVPBlocks.STEEL_STANDING,
                AVPBlocks.STEEL_STANDING_SLAB,
                AVPBlocks.STEEL_STANDING_STAIRS,
                AVPBlocks.STEEL_TREAD,
                AVPBlocks.STEEL_TREAD_SLAB,
                AVPBlocks.STEEL_TREAD_STAIRS,
                AVPBlocks.STEEL_BUTTON,
                AVPBlocks.STEEL_DOOR,
                AVPBlocks.STEEL_TRAP_DOOR
            );

        getOrCreateTagBuilder(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.CHISELED_TITANIUM,
                AVPBlocks.CUT_TITANIUM,
                AVPBlocks.CUT_TITANIUM_SLAB,
                AVPBlocks.CUT_TITANIUM_STAIRS,
                AVPBlocks.TITANIUM_BLOCK,
                AVPBlocks.TITANIUM_CHAIN_FENCE,
                AVPBlocks.TITANIUM_COLUMN,
                AVPBlocks.TITANIUM_FASTENED_SIDING,
                AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB,
                AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS,
                AVPBlocks.TITANIUM_FASTENED_STANDING,
                AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB,
                AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS,
                AVPBlocks.TITANIUM_GRATE,
                AVPBlocks.TITANIUM_GRATE_SLAB,
                AVPBlocks.TITANIUM_GRATE_STAIRS,
                AVPBlocks.TITANIUM_PLATING,
                AVPBlocks.TITANIUM_PLATING_SLAB,
                AVPBlocks.TITANIUM_PLATING_STAIRS,
                AVPBlocks.TITANIUM_PRESSURE_PLATE,
                AVPBlocks.TITANIUM_SIDING,
                AVPBlocks.TITANIUM_SIDING_SLAB,
                AVPBlocks.TITANIUM_SIDING_STAIRS,
                AVPBlocks.TITANIUM_SLAB,
                AVPBlocks.TITANIUM_STAIRS,
                AVPBlocks.TITANIUM_STANDING,
                AVPBlocks.TITANIUM_STANDING_SLAB,
                AVPBlocks.TITANIUM_STANDING_STAIRS,
                AVPBlocks.TITANIUM_TREAD,
                AVPBlocks.TITANIUM_TREAD_SLAB,
                AVPBlocks.TITANIUM_TREAD_STAIRS,
                AVPBlocks.TITANIUM_BUTTON,
                AVPBlocks.TITANIUM_DOOR,
                AVPBlocks.TITANIUM_TRAP_DOOR,
                AVPBlocks.DEEPSLATE_TITANIUM_ORE,
                AVPBlocks.RAW_TITANIUM_BLOCK
            );

        // Acid-immune blocks
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addOptionalTag(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .add(Blocks.AIR)
            .add(Blocks.FIRE)
            .add(Blocks.SOUL_FIRE);

        getOrCreateTagBuilder(AVPBlockTags.NETHER_ACID_IMMUNE)
            .addOptionalTag(BlockTags.INFINIBURN_NETHER)
            .addTag(AVPBlockTags.ACID_IMMUNE);

        getOrCreateTagBuilder(AVPBlockTags.IRRADIATED_ACID_IMMUNE)
            .addTag(AVPBlockTags.IRRADIATED_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .addOptionalTag(BlockTags.FEATURES_CANNOT_REPLACE)
            .add(
                Blocks.BARRIER,
                Blocks.BEDROCK,
                Blocks.END_PORTAL,
                Blocks.END_PORTAL_FRAME,
                Blocks.END_GATEWAY,
                Blocks.COMMAND_BLOCK,
                Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.STRUCTURE_BLOCK,
                Blocks.JIGSAW,
                Blocks.MOVING_PISTON,
                Blocks.LIGHT,
                Blocks.REINFORCED_DEEPSLATE
            );

        getOrCreateTagBuilder(AVPBlockTags.XENOMORPH_IMMUNE)
            .addTag(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PLASTIC);

        getOrCreateTagBuilder(BlockTags.CLIMBABLE)
            .add(
                AVPBlocks.FERROALUMINUM_CHAIN_FENCE,
                AVPBlocks.STEEL_CHAIN_FENCE,
                AVPBlocks.TITANIUM_CHAIN_FENCE
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .add(
                AVPBlocks.BLUEPRINT_BLOCK,
                AVPBlocks.NETHER_RESIN_VEIN,
                AVPBlocks.NETHER_RESIN_WEB,
                AVPBlocks.RESIN_VEIN,
                AVPBlocks.RESIN_WEB,
                AVPBlocks.ABERRANT_RESIN,
                AVPBlocks.ABERRANT_RESIN_WEB,
                AVPBlocks.IRRADIATED_RESIN,
                AVPBlocks.IRRADIATED_RESIN_WEB
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .addTag(AVPBlockTags.CONCRETE)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.INDUSTRIAL_FURNACE,
                AVPBlocks.NUKE_BLOCK,
                AVPBlocks.DESK_TERMINAL_BLOCK,
                AVPBlocks.TRIP_MINE_BLOCK,
                AVPBlocks.REDSTONE_GENERATOR,
                AVPBlocks.RESONATOR_BLOCK,
                AVPBlocks.SENTRY_TURRET,
                AVPBlocks.TRINITITE_BLOCK,
                AVPBlocks.ALUMINUM_BLOCK,
                AVPBlocks.AUTUNITE_BLOCK,
                AVPBlocks.AUTUNITE_ORE,
                AVPBlocks.BAUXITE_ORE,
                AVPBlocks.BRASS_BLOCK,
                AVPBlocks.DEEPSLATE_TITANIUM_ORE,
                AVPBlocks.DEEPSLATE_ZINC_ORE,
                AVPBlocks.GALENA_ORE,
                AVPBlocks.LEAD_BLOCK,
                AVPBlocks.LEAD_CHEST,
                AVPBlocks.AMMO_CHEST,
                AVPBlocks.LITHIUM_BLOCK,
                AVPBlocks.LITHIUM_ORE,
                AVPBlocks.MONAZITE_ORE,
                AVPBlocks.NETHER_RESIN,
                AVPBlocks.NETHER_RESIN_NODE,
                AVPBlocks.IRRADIATED_RESIN,
                AVPBlocks.IRRADIATED_RESIN_NODE,
                AVPBlocks.ABERRANT_RESIN,
                AVPBlocks.ABERRANT_RESIN_NODE,
                AVPBlocks.RAW_BAUXITE_BLOCK,
                AVPBlocks.RAW_GALENA_BLOCK,
                AVPBlocks.RAW_MONAZITE_BLOCK,
                AVPBlocks.RAW_SILICA_BLOCK,
                AVPBlocks.RAW_ZINC_BLOCK,
                AVPBlocks.RESIN,
                AVPBlocks.RESIN_NODE,
                AVPBlocks.RESIN_BRICKS,
                AVPBlocks.RESIN_O,
                AVPBlocks.RESIN_RIBBED,
                AVPBlocks.RESIN_SMOOTH,
                AVPBlocks.URANIUM_BLOCK,
                AVPBlocks.ZINC_BLOCK,
                AVPBlocks.ZINC_ORE
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(
                AVPBlocks.SILICA_GRAVEL,
                AVPBlocks.ASH_BLOCK
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .add(
                AVPBlocks.BLUEPRINT_BLOCK,
                AVPBlocks.BAUXITE_ORE,
                AVPBlocks.GALENA_ORE,
                AVPBlocks.RAW_BAUXITE_BLOCK
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.INDUSTRIAL_FURNACE,
                AVPBlocks.NUKE_BLOCK,
                AVPBlocks.SENTRY_TURRET,
                AVPBlocks.DESK_TERMINAL_BLOCK,
                AVPBlocks.REDSTONE_GENERATOR,
                AVPBlocks.TRIP_MINE_BLOCK,
                AVPBlocks.RESONATOR_BLOCK,
                AVPBlocks.TRINITITE_BLOCK,
                AVPBlocks.DEEPSLATE_TITANIUM_ORE,
                AVPBlocks.RAW_TITANIUM_BLOCK,
                AVPBlocks.URANIUM_BLOCK,
                AVPBlocks.LEAD_CHEST,
                AVPBlocks.AMMO_CHEST
            );

        var slabTagProvider = getOrCreateTagBuilder(BlockTags.SLABS);

        slabTagProvider.add(
            AVPBlocks.CUT_FERROALUMINUM_SLAB,
            AVPBlocks.CUT_STEEL_SLAB,
            AVPBlocks.CUT_TITANIUM_SLAB,
            AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB,
            AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB,
            AVPBlocks.FERROALUMINUM_GRATE_SLAB,
            AVPBlocks.FERROALUMINUM_PLATING_SLAB,
            AVPBlocks.FERROALUMINUM_SIDING_SLAB,
            AVPBlocks.FERROALUMINUM_SLAB,
            AVPBlocks.FERROALUMINUM_STANDING_SLAB,
            AVPBlocks.FERROALUMINUM_TREAD_SLAB,
            AVPBlocks.STEEL_FASTENED_SIDING_SLAB,
            AVPBlocks.STEEL_FASTENED_STANDING_SLAB,
            AVPBlocks.STEEL_GRATE_SLAB,
            AVPBlocks.STEEL_PLATING_SLAB,
            AVPBlocks.STEEL_SIDING_SLAB,
            AVPBlocks.STEEL_SLAB,
            AVPBlocks.STEEL_STANDING_SLAB,
            AVPBlocks.STEEL_TREAD_SLAB,
            AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB,
            AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB,
            AVPBlocks.TITANIUM_GRATE_SLAB,
            AVPBlocks.TITANIUM_PLATING_SLAB,
            AVPBlocks.TITANIUM_SIDING_SLAB,
            AVPBlocks.TITANIUM_SLAB,
            AVPBlocks.TITANIUM_STANDING_SLAB,
            AVPBlocks.TITANIUM_TREAD_SLAB
        );

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().forEach(slabTagProvider::add);

        var pressurePlateTagProvider = getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES);

        pressurePlateTagProvider.add(
            AVPBlocks.FERROALUMINUM_PRESSURE_PLATE,
            AVPBlocks.STEEL_PRESSURE_PLATE,
            AVPBlocks.TITANIUM_PRESSURE_PLATE
        );

        var doorTagProvider = getOrCreateTagBuilder(BlockTags.DOORS);

        doorTagProvider.add(
            AVPBlocks.FERROALUMINUM_DOOR,
            AVPBlocks.STEEL_DOOR,
            AVPBlocks.TITANIUM_DOOR
        );

        var trapdoorTagProvider = getOrCreateTagBuilder(BlockTags.TRAPDOORS);

        trapdoorTagProvider.add(
            AVPBlocks.FERROALUMINUM_TRAP_DOOR,
            AVPBlocks.STEEL_TRAP_DOOR,
            AVPBlocks.TITANIUM_TRAP_DOOR
        );

        var buttonTagProvider = getOrCreateTagBuilder(BlockTags.BUTTONS);

        buttonTagProvider.add(
            AVPBlocks.FERROALUMINUM_BUTTON,
            AVPBlocks.STEEL_BUTTON,
            AVPBlocks.TITANIUM_BUTTON
        );

        var fenceTagProvider = getOrCreateTagBuilder(BlockTags.FENCES);

        fenceTagProvider.add(
            AVPBlocks.FERROALUMINUM_CHAIN_FENCE,
            AVPBlocks.STEEL_CHAIN_FENCE,
            AVPBlocks.TITANIUM_CHAIN_FENCE
        );

        var stairsTagProvider = getOrCreateTagBuilder(BlockTags.STAIRS);

        stairsTagProvider.add(
            AVPBlocks.CUT_FERROALUMINUM_STAIRS,
            AVPBlocks.CUT_STEEL_STAIRS,
            AVPBlocks.CUT_TITANIUM_STAIRS,
            AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS,
            AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS,
            AVPBlocks.FERROALUMINUM_GRATE_STAIRS,
            AVPBlocks.FERROALUMINUM_PLATING_STAIRS,
            AVPBlocks.FERROALUMINUM_SIDING_STAIRS,
            AVPBlocks.FERROALUMINUM_STAIRS,
            AVPBlocks.FERROALUMINUM_STANDING_STAIRS,
            AVPBlocks.FERROALUMINUM_TREAD_STAIRS,
            AVPBlocks.STEEL_FASTENED_SIDING_STAIRS,
            AVPBlocks.STEEL_FASTENED_STANDING_STAIRS,
            AVPBlocks.STEEL_GRATE_STAIRS,
            AVPBlocks.STEEL_PLATING_STAIRS,
            AVPBlocks.STEEL_SIDING_STAIRS,
            AVPBlocks.STEEL_STAIRS,
            AVPBlocks.STEEL_STANDING_STAIRS,
            AVPBlocks.STEEL_TREAD_STAIRS,
            AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS,
            AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS,
            AVPBlocks.TITANIUM_GRATE_STAIRS,
            AVPBlocks.TITANIUM_PLATING_STAIRS,
            AVPBlocks.TITANIUM_SIDING_STAIRS,
            AVPBlocks.TITANIUM_STAIRS,
            AVPBlocks.TITANIUM_STANDING_STAIRS,
            AVPBlocks.TITANIUM_TREAD_STAIRS
        );

        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().forEach(stairsTagProvider::add);

        var wallTagBuilder = getOrCreateTagBuilder(BlockTags.WALLS);

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().forEach(wallTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.MARINE_SPAWN_BLOCKS).add(
            Blocks.DIRT_PATH,
            AVPBlocks.FERROALUMINUM_TREAD,
            AVPBlocks.TITANIUM_TREAD,
            AVPBlocks.STEEL_TREAD,
            AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(DyeColor.BLACK)
        );

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_BLOCK_TAG);
    }
}
