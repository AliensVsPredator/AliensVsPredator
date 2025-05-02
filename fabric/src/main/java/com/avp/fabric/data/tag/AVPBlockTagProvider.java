package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.block.AVPBlocks;
import com.avp.fabric.data.compatibility.common.CommonConstants;
import com.avp.fabric.data.compatibility.gigeresque.GigeresqueConstants;

public class AVPBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public AVPBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var concreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.CONCRETE);

        // TODO: Use a stream concat here.
        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(concreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(concreteTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .addOptionalTag(BlockTags.FEATURES_CANNOT_REPLACE)
            .add(
                Blocks.BARRIER,
                Blocks.BEDROCK,
                Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.COMMAND_BLOCK,
                Blocks.END_GATEWAY,
                Blocks.END_PORTAL,
                Blocks.END_PORTAL_FRAME,
                Blocks.JIGSAW,
                Blocks.LIGHT,
                Blocks.MOVING_PISTON,
                Blocks.REINFORCED_DEEPSLATE,
                Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.STRUCTURE_BLOCK
            );

        getOrCreateTagBuilder(AVPBlockTags.FERROALUMINUM)
            .add(
                AVPBlocks.CHISELED_FERROALUMINUM.get(),
                AVPBlocks.CUT_FERROALUMINUM.get(),
                AVPBlocks.CUT_FERROALUMINUM_SLAB.get(),
                AVPBlocks.CUT_FERROALUMINUM_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_BLOCK.get(),
                AVPBlocks.FERROALUMINUM_BUTTON.get(),
                AVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                AVPBlocks.FERROALUMINUM_COLUMN.get(),
                AVPBlocks.FERROALUMINUM_DOOR.get(),
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING.get(),
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get(),
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING.get(),
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get(),
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_GRATE.get(),
                AVPBlocks.FERROALUMINUM_GRATE_SLAB.get(),
                AVPBlocks.FERROALUMINUM_GRATE_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_PLATING.get(),
                AVPBlocks.FERROALUMINUM_PLATING_SLAB.get(),
                AVPBlocks.FERROALUMINUM_PLATING_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_PRESSURE_PLATE.get(),
                AVPBlocks.FERROALUMINUM_SIDING.get(),
                AVPBlocks.FERROALUMINUM_SIDING_SLAB.get(),
                AVPBlocks.FERROALUMINUM_SIDING_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_SLAB.get(),
                AVPBlocks.FERROALUMINUM_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_STANDING.get(),
                AVPBlocks.FERROALUMINUM_STANDING_SLAB.get(),
                AVPBlocks.FERROALUMINUM_STANDING_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_TRAP_DOOR.get(),
                AVPBlocks.FERROALUMINUM_TREAD.get(),
                AVPBlocks.FERROALUMINUM_TREAD_SLAB.get(),
                AVPBlocks.FERROALUMINUM_TREAD_STAIRS.get()
            );

        var industrialConcreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_CONCRETE);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values()
            .stream()
            .map(Supplier::get)
            .forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(AVPBlocks.INDUSTRIAL_GLASS.get());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().stream().map(Supplier::get).forEach(industrialGlassBlockTagBuilder::add);

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(AVPBlocks.INDUSTRIAL_GLASS_PANE.get());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().stream().map(Supplier::get).forEach(industrialGlassPaneTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_PANE)
            .add(
                AVPBlocks.INDUSTRIAL_GLASS_DOOR.get(),
                AVPBlocks.INDUSTRIAL_GLASS_SLAB.get(),
                AVPBlocks.INDUSTRIAL_GLASS_STAIRS.get(),
                AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get()
            );

        var paddingTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PADDING);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_PADDING.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);

        var plasticTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PLASTIC);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);

        AVPBlocks.DYE_COLOR_TO_PLASTIC.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.IRRADIATED_RESIN)
            .add(
                AVPBlocks.IRRADIATED_RESIN.get(),
                AVPBlocks.IRRADIATED_RESIN_NODE.get(),
                AVPBlocks.IRRADIATED_RESIN_VEIN.get(),
                AVPBlocks.IRRADIATED_RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.ABERRANT_RESIN)
            .add(
                AVPBlocks.ABERRANT_RESIN.get(),
                AVPBlocks.ABERRANT_RESIN_NODE.get(),
                AVPBlocks.ABERRANT_RESIN_VEIN.get(),
                AVPBlocks.ABERRANT_RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.NETHER_RESIN)
            .add(
                AVPBlocks.NETHER_RESIN.get(),
                AVPBlocks.NETHER_RESIN_NODE.get(),
                AVPBlocks.NETHER_RESIN_VEIN.get(),
                AVPBlocks.NETHER_RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.NORMAL_RESIN)
            .add(
                AVPBlocks.RESIN.get(),
                AVPBlocks.RESIN_NODE.get(),
                AVPBlocks.RESIN_VEIN.get(),
                AVPBlocks.RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.CRAFTED_RESIN)
            .add(
                AVPBlocks.RESIN_BRICKS.get(),
                AVPBlocks.RESIN_O.get(),
                AVPBlocks.RESIN_RIBBED.get(),
                AVPBlocks.RESIN_SMOOTH.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN)
            .addTag(AVPBlockTags.ABERRANT_RESIN)
            .addTag(AVPBlockTags.CRAFTED_RESIN)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.RESIN_VEINS)
            .add(
                AVPBlocks.ABERRANT_RESIN_VEIN.get(),
                AVPBlocks.IRRADIATED_RESIN_VEIN.get(),
                AVPBlocks.NETHER_RESIN_VEIN.get(),
                AVPBlocks.RESIN_VEIN.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN_WEBS)
            .add(
                AVPBlocks.ABERRANT_RESIN_WEB.get(),
                AVPBlocks.IRRADIATED_RESIN_WEB.get(),
                AVPBlocks.NETHER_RESIN_WEB.get(),
                AVPBlocks.RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.STEEL)
            .add(
                AVPBlocks.CHISELED_STEEL.get(),
                AVPBlocks.CUT_STEEL.get(),
                AVPBlocks.CUT_STEEL_SLAB.get(),
                AVPBlocks.CUT_STEEL_STAIRS.get(),
                AVPBlocks.STEEL_BARS.get(),
                AVPBlocks.STEEL_BLOCK.get(),
                AVPBlocks.STEEL_BUTTON.get(),
                AVPBlocks.STEEL_CHAIN_FENCE.get(),
                AVPBlocks.STEEL_COLUMN.get(),
                AVPBlocks.STEEL_DOOR.get(),
                AVPBlocks.STEEL_FASTENED_SIDING.get(),
                AVPBlocks.STEEL_FASTENED_SIDING_SLAB.get(),
                AVPBlocks.STEEL_FASTENED_SIDING_STAIRS.get(),
                AVPBlocks.STEEL_FASTENED_STANDING.get(),
                AVPBlocks.STEEL_FASTENED_STANDING_SLAB.get(),
                AVPBlocks.STEEL_FASTENED_STANDING_STAIRS.get(),
                AVPBlocks.STEEL_GRATE.get(),
                AVPBlocks.STEEL_GRATE_SLAB.get(),
                AVPBlocks.STEEL_GRATE_STAIRS.get(),
                AVPBlocks.STEEL_PLATING.get(),
                AVPBlocks.STEEL_PLATING_SLAB.get(),
                AVPBlocks.STEEL_PLATING_STAIRS.get(),
                AVPBlocks.STEEL_PRESSURE_PLATE.get(),
                AVPBlocks.STEEL_SIDING.get(),
                AVPBlocks.STEEL_SIDING_SLAB.get(),
                AVPBlocks.STEEL_SIDING_STAIRS.get(),
                AVPBlocks.STEEL_SLAB.get(),
                AVPBlocks.STEEL_STAIRS.get(),
                AVPBlocks.STEEL_STANDING.get(),
                AVPBlocks.STEEL_STANDING_SLAB.get(),
                AVPBlocks.STEEL_STANDING_STAIRS.get(),
                AVPBlocks.STEEL_TRAP_DOOR.get(),
                AVPBlocks.STEEL_TREAD.get(),
                AVPBlocks.STEEL_TREAD_SLAB.get(),
                AVPBlocks.STEEL_TREAD_STAIRS.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.CHISELED_TITANIUM.get(),
                AVPBlocks.CUT_TITANIUM.get(),
                AVPBlocks.CUT_TITANIUM_SLAB.get(),
                AVPBlocks.CUT_TITANIUM_STAIRS.get(),
                AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.RAW_TITANIUM_BLOCK.get(),
                AVPBlocks.TITANIUM_BLOCK.get(),
                AVPBlocks.TITANIUM_BUTTON.get(),
                AVPBlocks.TITANIUM_CHAIN_FENCE.get(),
                AVPBlocks.TITANIUM_COLUMN.get(),
                AVPBlocks.TITANIUM_DOOR.get(),
                AVPBlocks.TITANIUM_FASTENED_SIDING.get(),
                AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB.get(),
                AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get(),
                AVPBlocks.TITANIUM_FASTENED_STANDING.get(),
                AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB.get(),
                AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get(),
                AVPBlocks.TITANIUM_GRATE.get(),
                AVPBlocks.TITANIUM_GRATE_SLAB.get(),
                AVPBlocks.TITANIUM_GRATE_STAIRS.get(),
                AVPBlocks.TITANIUM_PLATING.get(),
                AVPBlocks.TITANIUM_PLATING_SLAB.get(),
                AVPBlocks.TITANIUM_PLATING_STAIRS.get(),
                AVPBlocks.TITANIUM_PRESSURE_PLATE.get(),
                AVPBlocks.TITANIUM_SIDING.get(),
                AVPBlocks.TITANIUM_SIDING_SLAB.get(),
                AVPBlocks.TITANIUM_SIDING_STAIRS.get(),
                AVPBlocks.TITANIUM_SLAB.get(),
                AVPBlocks.TITANIUM_STAIRS.get(),
                AVPBlocks.TITANIUM_STANDING.get(),
                AVPBlocks.TITANIUM_STANDING_SLAB.get(),
                AVPBlocks.TITANIUM_STANDING_STAIRS.get(),
                AVPBlocks.TITANIUM_TRAP_DOOR.get(),
                AVPBlocks.TITANIUM_TREAD.get(),
                AVPBlocks.TITANIUM_TREAD_SLAB.get(),
                AVPBlocks.TITANIUM_TREAD_STAIRS.get()
            );

        // Acid-immune blocks
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .addTag(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .add(Blocks.AIR)
            .add(Blocks.FIRE)
            .add(Blocks.SOUL_FIRE);

        getOrCreateTagBuilder(AVPBlockTags.NETHER_ACID_IMMUNE)
            .addOptionalTag(BlockTags.INFINIBURN_NETHER)
            .addTag(AVPBlockTags.ACID_IMMUNE);

        getOrCreateTagBuilder(AVPBlockTags.IRRADIATED_ACID_IMMUNE)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.SHOULD_NOT_BE_DESTROYED);

        getOrCreateTagBuilder(AVPBlockTags.XENOMORPH_IMMUNE)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.SHOULD_NOT_BE_DESTROYED);

        getOrCreateTagBuilder(BlockTags.CLIMBABLE)
            .add(
                AVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                AVPBlocks.STEEL_CHAIN_FENCE.get(),
                AVPBlocks.TITANIUM_CHAIN_FENCE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .addTag(AVPBlockTags.RESIN_VEINS)
            .addTag(AVPBlockTags.RESIN_WEBS)
            .add(
                AVPBlocks.ABERRANT_RESIN.get(),
                AVPBlocks.BLUEPRINT_BLOCK.get(),
                AVPBlocks.IRRADIATED_RESIN.get()
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
                AVPBlocks.ABERRANT_RESIN.get(),
                AVPBlocks.ABERRANT_RESIN_NODE.get(),
                AVPBlocks.ALUMINUM_BLOCK.get(),
                AVPBlocks.AMMO_CHEST.get(),
                AVPBlocks.AUTUNITE_BLOCK.get(),
                AVPBlocks.AUTUNITE_ORE.get(),
                AVPBlocks.BAUXITE_ORE.get(),
                AVPBlocks.BRASS_BLOCK.get(),
                AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.DEEPSLATE_ZINC_ORE.get(),
                AVPBlocks.DESK_TERMINAL_BLOCK.get(),
                AVPBlocks.GALENA_ORE.get(),
                AVPBlocks.INDUSTRIAL_FURNACE.get(),
                AVPBlocks.IRRADIATED_RESIN.get(),
                AVPBlocks.IRRADIATED_RESIN_NODE.get(),
                AVPBlocks.LEAD_BLOCK.get(),
                AVPBlocks.LEAD_CHEST.get(),
                AVPBlocks.LITHIUM_BLOCK.get(),
                AVPBlocks.LITHIUM_ORE.get(),
                AVPBlocks.MONAZITE_ORE.get(),
                AVPBlocks.NETHER_RESIN.get(),
                AVPBlocks.NETHER_RESIN_NODE.get(),
                AVPBlocks.NUKE_BLOCK.get(),
                AVPBlocks.RAW_BAUXITE_BLOCK.get(),
                AVPBlocks.RAW_GALENA_BLOCK.get(),
                AVPBlocks.RAW_MONAZITE_BLOCK.get(),
                AVPBlocks.RAW_SILICA_BLOCK.get(),
                AVPBlocks.RAW_ZINC_BLOCK.get(),
                AVPBlocks.REDSTONE_GENERATOR.get(),
                AVPBlocks.RESIN.get(),
                AVPBlocks.RESIN_NODE.get(),
                AVPBlocks.RESIN_BRICKS.get(),
                AVPBlocks.RESIN_O.get(),
                AVPBlocks.RESIN_RIBBED.get(),
                AVPBlocks.RESIN_SMOOTH.get(),
                AVPBlocks.RESONATOR_BLOCK.get(),
                AVPBlocks.SENTRY_TURRET.get(),
                AVPBlocks.TRINITITE_BLOCK.get(),
                AVPBlocks.TRIP_MINE_BLOCK.get(),
                AVPBlocks.URANIUM_BLOCK.get(),
                AVPBlocks.ZINC_BLOCK.get(),
                AVPBlocks.ZINC_ORE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(
                AVPBlocks.ASH_BLOCK.get(),
                AVPBlocks.SILICA_GRAVEL.get()
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .add(
                AVPBlocks.BAUXITE_ORE.get(),
                AVPBlocks.BLUEPRINT_BLOCK.get(),
                AVPBlocks.GALENA_ORE.get(),
                AVPBlocks.RAW_BAUXITE_BLOCK.get()
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.AMMO_CHEST.get(),
                AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.DESK_TERMINAL_BLOCK.get(),
                AVPBlocks.INDUSTRIAL_FURNACE.get(),
                AVPBlocks.LEAD_CHEST.get(),
                AVPBlocks.NUKE_BLOCK.get(),
                AVPBlocks.RAW_TITANIUM_BLOCK.get(),
                AVPBlocks.REDSTONE_GENERATOR.get(),
                AVPBlocks.RESONATOR_BLOCK.get(),
                AVPBlocks.SENTRY_TURRET.get(),
                AVPBlocks.TRINITITE_BLOCK.get(),
                AVPBlocks.TRIP_MINE_BLOCK.get(),
                AVPBlocks.URANIUM_BLOCK.get()
            );

        var slabTagProvider = getOrCreateTagBuilder(BlockTags.SLABS);

        slabTagProvider.add(
            AVPBlocks.INDUSTRIAL_GLASS_SLAB.get(),
            AVPBlocks.CUT_FERROALUMINUM_SLAB.get(),
            AVPBlocks.CUT_STEEL_SLAB.get(),
            AVPBlocks.CUT_TITANIUM_SLAB.get(),
            AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get(),
            AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get(),
            AVPBlocks.FERROALUMINUM_GRATE_SLAB.get(),
            AVPBlocks.FERROALUMINUM_PLATING_SLAB.get(),
            AVPBlocks.FERROALUMINUM_SIDING_SLAB.get(),
            AVPBlocks.FERROALUMINUM_SLAB.get(),
            AVPBlocks.FERROALUMINUM_STANDING_SLAB.get(),
            AVPBlocks.FERROALUMINUM_TREAD_SLAB.get(),
            AVPBlocks.STEEL_FASTENED_SIDING_SLAB.get(),
            AVPBlocks.STEEL_FASTENED_STANDING_SLAB.get(),
            AVPBlocks.STEEL_GRATE_SLAB.get(),
            AVPBlocks.STEEL_PLATING_SLAB.get(),
            AVPBlocks.STEEL_SIDING_SLAB.get(),
            AVPBlocks.STEEL_SLAB.get(),
            AVPBlocks.STEEL_STANDING_SLAB.get(),
            AVPBlocks.STEEL_TREAD_SLAB.get(),
            AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB.get(),
            AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB.get(),
            AVPBlocks.TITANIUM_GRATE_SLAB.get(),
            AVPBlocks.TITANIUM_PLATING_SLAB.get(),
            AVPBlocks.TITANIUM_SIDING_SLAB.get(),
            AVPBlocks.TITANIUM_SLAB.get(),
            AVPBlocks.TITANIUM_STANDING_SLAB.get(),
            AVPBlocks.TITANIUM_TREAD_SLAB.get()
        );

        // TODO: Use a stream concat here.
        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);

        var pressurePlateTagProvider = getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES);

        pressurePlateTagProvider.add(
            AVPBlocks.FERROALUMINUM_PRESSURE_PLATE.get(),
            AVPBlocks.STEEL_PRESSURE_PLATE.get(),
            AVPBlocks.TITANIUM_PRESSURE_PLATE.get()
        );

        var doorTagProvider = getOrCreateTagBuilder(BlockTags.DOORS);

        doorTagProvider.add(
            AVPBlocks.INDUSTRIAL_GLASS_DOOR.get(),
            AVPBlocks.FERROALUMINUM_DOOR.get(),
            AVPBlocks.STEEL_DOOR.get(),
            AVPBlocks.TITANIUM_DOOR.get()
        );

        var trapdoorTagProvider = getOrCreateTagBuilder(BlockTags.TRAPDOORS);

        trapdoorTagProvider.add(
            AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get(),
            AVPBlocks.FERROALUMINUM_TRAP_DOOR.get(),
            AVPBlocks.STEEL_TRAP_DOOR.get(),
            AVPBlocks.TITANIUM_TRAP_DOOR.get()
        );

        var buttonTagProvider = getOrCreateTagBuilder(BlockTags.BUTTONS);

        buttonTagProvider.add(
            AVPBlocks.FERROALUMINUM_BUTTON.get(),
            AVPBlocks.STEEL_BUTTON.get(),
            AVPBlocks.TITANIUM_BUTTON.get()
        );

        var fenceTagProvider = getOrCreateTagBuilder(BlockTags.FENCES);

        fenceTagProvider.add(
            AVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
            AVPBlocks.STEEL_CHAIN_FENCE.get(),
            AVPBlocks.TITANIUM_CHAIN_FENCE.get()
        );

        var stairsTagProvider = getOrCreateTagBuilder(BlockTags.STAIRS);

        stairsTagProvider.add(
            AVPBlocks.INDUSTRIAL_GLASS_STAIRS.get(),
            AVPBlocks.CUT_FERROALUMINUM_STAIRS.get(),
            AVPBlocks.CUT_STEEL_STAIRS.get(),
            AVPBlocks.CUT_TITANIUM_STAIRS.get(),
            AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get(),
            AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get(),
            AVPBlocks.FERROALUMINUM_GRATE_STAIRS.get(),
            AVPBlocks.FERROALUMINUM_PLATING_STAIRS.get(),
            AVPBlocks.FERROALUMINUM_SIDING_STAIRS.get(),
            AVPBlocks.FERROALUMINUM_STAIRS.get(),
            AVPBlocks.FERROALUMINUM_STANDING_STAIRS.get(),
            AVPBlocks.FERROALUMINUM_TREAD_STAIRS.get(),
            AVPBlocks.STEEL_FASTENED_SIDING_STAIRS.get(),
            AVPBlocks.STEEL_FASTENED_STANDING_STAIRS.get(),
            AVPBlocks.STEEL_GRATE_STAIRS.get(),
            AVPBlocks.STEEL_PLATING_STAIRS.get(),
            AVPBlocks.STEEL_SIDING_STAIRS.get(),
            AVPBlocks.STEEL_STAIRS.get(),
            AVPBlocks.STEEL_STANDING_STAIRS.get(),
            AVPBlocks.STEEL_TREAD_STAIRS.get(),
            AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get(),
            AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get(),
            AVPBlocks.TITANIUM_GRATE_STAIRS.get(),
            AVPBlocks.TITANIUM_PLATING_STAIRS.get(),
            AVPBlocks.TITANIUM_SIDING_STAIRS.get(),
            AVPBlocks.TITANIUM_STAIRS.get(),
            AVPBlocks.TITANIUM_STANDING_STAIRS.get(),
            AVPBlocks.TITANIUM_TREAD_STAIRS.get()
        );

        // TODO: Use a stream concat here.
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);

        var wallTagBuilder = getOrCreateTagBuilder(BlockTags.WALLS);

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(wallTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.MARINE_SPAWN_BLOCKS).add(
            AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(DyeColor.BLACK).get(),
            AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(DyeColor.LIGHT_GRAY).get(),
            AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(DyeColor.GRAY).get(),
            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(DyeColor.BLACK).get(),
            Blocks.CYAN_TERRACOTTA,
            Blocks.MOSS_BLOCK,
            AVPBlocks.FERROALUMINUM_TREAD.get(),
            AVPBlocks.STEEL_TREAD.get(),
            Blocks.GRAVEL,
            AVPBlocks.STEEL_GRATE.get(),
            AVPBlocks.TITANIUM_TREAD.get()
        );

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_BLOCK_TAG);

        getOrCreateTagBuilder(CommonConstants.CHESTS)
            .setReplace(false)
            .add(
                AVPBlocks.AMMO_CHEST.get(),
                AVPBlocks.LEAD_CHEST.get()
            );

        getOrCreateTagBuilder(CommonConstants.ORES_BLOCKS)
            .setReplace(false)
            .add(
                AVPBlocks.AUTUNITE_ORE.get(),
                AVPBlocks.BAUXITE_ORE.get(),
                AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.DEEPSLATE_ZINC_ORE.get(),
                AVPBlocks.GALENA_ORE.get(),
                AVPBlocks.LITHIUM_ORE.get(),
                AVPBlocks.MONAZITE_ORE.get(),
                AVPBlocks.ZINC_ORE.get()
            );
    }
}
