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
import com.avp.common.block.TempAVPBlocks;
import com.avp.fabric.common.block.AVPBlocks;
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
        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(concreteTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(concreteTagBuilder::add);

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
                AVPBlocks.CHISELED_FERROALUMINUM,
                AVPBlocks.CUT_FERROALUMINUM,
                AVPBlocks.CUT_FERROALUMINUM_SLAB,
                AVPBlocks.CUT_FERROALUMINUM_STAIRS,
                TempAVPBlocks.FERROALUMINUM_BLOCK.get(),
                AVPBlocks.FERROALUMINUM_BUTTON,
                TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                TempAVPBlocks.FERROALUMINUM_COLUMN.get(),
                AVPBlocks.FERROALUMINUM_DOOR,
                TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING.get(),
                TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING.get(),
                TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_GRATE.get(),
                TempAVPBlocks.FERROALUMINUM_GRATE_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_PLATING.get(),
                TempAVPBlocks.FERROALUMINUM_PLATING_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_PLATING_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_PRESSURE_PLATE,
                TempAVPBlocks.FERROALUMINUM_SIDING.get(),
                TempAVPBlocks.FERROALUMINUM_SIDING_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_STANDING.get(),
                TempAVPBlocks.FERROALUMINUM_STANDING_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_TRAP_DOOR,
                TempAVPBlocks.FERROALUMINUM_TREAD.get(),
                TempAVPBlocks.FERROALUMINUM_TREAD_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS.get()
            );

        var industrialConcreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_CONCRETE);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values()
            .stream()
            .map(Supplier::get)
            .forEach(industrialConcreteTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(AVPBlocks.INDUSTRIAL_GLASS);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().stream().map(Supplier::get).forEach(industrialGlassBlockTagBuilder::add);

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(AVPBlocks.INDUSTRIAL_GLASS_PANE);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().stream().map(Supplier::get).forEach(industrialGlassPaneTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_PANE)
            .add(
                AVPBlocks.INDUSTRIAL_GLASS_DOOR,
                AVPBlocks.INDUSTRIAL_GLASS_SLAB,
                AVPBlocks.INDUSTRIAL_GLASS_STAIRS,
                AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR
            );

        var paddingTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PADDING);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_PADDING.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);

        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);

        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(paddingTagBuilder::add);

        var plasticTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PLASTIC);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);

        TempAVPBlocks.DYE_COLOR_TO_PLASTIC.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);

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
            .addTag(AVPBlockTags.ABERRANT_RESIN)
            .addTag(AVPBlockTags.CRAFTED_RESIN)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.RESIN_VEINS)
            .add(
                AVPBlocks.ABERRANT_RESIN_VEIN,
                AVPBlocks.IRRADIATED_RESIN_VEIN,
                AVPBlocks.NETHER_RESIN_VEIN,
                AVPBlocks.RESIN_VEIN
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN_WEBS)
            .add(
                AVPBlocks.ABERRANT_RESIN_WEB,
                AVPBlocks.IRRADIATED_RESIN_WEB,
                AVPBlocks.NETHER_RESIN_WEB,
                AVPBlocks.RESIN_WEB
            );

        getOrCreateTagBuilder(AVPBlockTags.STEEL)
            .add(
                AVPBlocks.CHISELED_STEEL,
                AVPBlocks.CUT_STEEL,
                AVPBlocks.CUT_STEEL_SLAB,
                AVPBlocks.CUT_STEEL_STAIRS,
                TempAVPBlocks.STEEL_BARS.get(),
                TempAVPBlocks.STEEL_BLOCK.get(),
                AVPBlocks.STEEL_BUTTON,
                TempAVPBlocks.STEEL_CHAIN_FENCE.get(),
                TempAVPBlocks.STEEL_COLUMN.get(),
                AVPBlocks.STEEL_DOOR,
                TempAVPBlocks.STEEL_FASTENED_SIDING.get(),
                TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB.get(),
                TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS.get(),
                TempAVPBlocks.STEEL_FASTENED_STANDING.get(),
                TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB.get(),
                TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS.get(),
                TempAVPBlocks.STEEL_GRATE.get(),
                TempAVPBlocks.STEEL_GRATE_SLAB.get(),
                TempAVPBlocks.STEEL_GRATE_STAIRS.get(),
                TempAVPBlocks.STEEL_PLATING.get(),
                TempAVPBlocks.STEEL_PLATING_SLAB.get(),
                TempAVPBlocks.STEEL_PLATING_STAIRS.get(),
                TempAVPBlocks.STEEL_PRESSURE_PLATE.get(),
                TempAVPBlocks.STEEL_SIDING.get(),
                TempAVPBlocks.STEEL_SIDING_SLAB.get(),
                TempAVPBlocks.STEEL_SIDING_STAIRS.get(),
                TempAVPBlocks.STEEL_SLAB.get(),
                TempAVPBlocks.STEEL_STAIRS.get(),
                TempAVPBlocks.STEEL_STANDING.get(),
                TempAVPBlocks.STEEL_STANDING_SLAB.get(),
                TempAVPBlocks.STEEL_STANDING_STAIRS.get(),
                AVPBlocks.STEEL_TRAP_DOOR,
                TempAVPBlocks.STEEL_TREAD.get(),
                TempAVPBlocks.STEEL_TREAD_SLAB.get(),
                TempAVPBlocks.STEEL_TREAD_STAIRS.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.CHISELED_TITANIUM,
                AVPBlocks.CUT_TITANIUM,
                AVPBlocks.CUT_TITANIUM_SLAB,
                AVPBlocks.CUT_TITANIUM_STAIRS,
                TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.RAW_TITANIUM_BLOCK,
                TempAVPBlocks.TITANIUM_BLOCK.get(),
                AVPBlocks.TITANIUM_BUTTON,
                TempAVPBlocks.TITANIUM_CHAIN_FENCE.get(),
                TempAVPBlocks.TITANIUM_COLUMN.get(),
                AVPBlocks.TITANIUM_DOOR,
                TempAVPBlocks.TITANIUM_FASTENED_SIDING.get(),
                TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB.get(),
                TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get(),
                TempAVPBlocks.TITANIUM_FASTENED_STANDING.get(),
                TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB.get(),
                TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get(),
                TempAVPBlocks.TITANIUM_GRATE.get(),
                TempAVPBlocks.TITANIUM_GRATE_SLAB.get(),
                TempAVPBlocks.TITANIUM_GRATE_STAIRS.get(),
                TempAVPBlocks.TITANIUM_PLATING.get(),
                TempAVPBlocks.TITANIUM_PLATING_SLAB.get(),
                TempAVPBlocks.TITANIUM_PLATING_STAIRS.get(),
                AVPBlocks.TITANIUM_PRESSURE_PLATE,
                TempAVPBlocks.TITANIUM_SIDING.get(),
                TempAVPBlocks.TITANIUM_SIDING_SLAB.get(),
                TempAVPBlocks.TITANIUM_SIDING_STAIRS.get(),
                TempAVPBlocks.TITANIUM_SLAB.get(),
                TempAVPBlocks.TITANIUM_STAIRS.get(),
                TempAVPBlocks.TITANIUM_STANDING.get(),
                TempAVPBlocks.TITANIUM_STANDING_SLAB.get(),
                TempAVPBlocks.TITANIUM_STANDING_STAIRS.get(),
                AVPBlocks.TITANIUM_TRAP_DOOR,
                TempAVPBlocks.TITANIUM_TREAD.get(),
                TempAVPBlocks.TITANIUM_TREAD_SLAB.get(),
                TempAVPBlocks.TITANIUM_TREAD_STAIRS.get()
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
                TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                TempAVPBlocks.STEEL_CHAIN_FENCE.get(),
                TempAVPBlocks.TITANIUM_CHAIN_FENCE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .addTag(AVPBlockTags.RESIN_VEINS)
            .addTag(AVPBlockTags.RESIN_WEBS)
            .add(
                AVPBlocks.ABERRANT_RESIN,
                AVPBlocks.BLUEPRINT_BLOCK,
                AVPBlocks.IRRADIATED_RESIN
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
                AVPBlocks.ABERRANT_RESIN,
                AVPBlocks.ABERRANT_RESIN_NODE,
                AVPBlocks.ALUMINUM_BLOCK,
                AVPBlocks.AMMO_CHEST,
                AVPBlocks.AUTUNITE_BLOCK,
                TempAVPBlocks.AUTUNITE_ORE.get(),
                TempAVPBlocks.BAUXITE_ORE.get(),
                AVPBlocks.BRASS_BLOCK,
                TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                TempAVPBlocks.DEEPSLATE_ZINC_ORE.get(),
                AVPBlocks.DESK_TERMINAL_BLOCK,
                TempAVPBlocks.GALENA_ORE.get(),
                AVPBlocks.INDUSTRIAL_FURNACE,
                AVPBlocks.IRRADIATED_RESIN,
                AVPBlocks.IRRADIATED_RESIN_NODE,
                AVPBlocks.LEAD_BLOCK,
                AVPBlocks.LEAD_CHEST,
                AVPBlocks.LITHIUM_BLOCK,
                TempAVPBlocks.LITHIUM_ORE.get(),
                TempAVPBlocks.MONAZITE_ORE.get(),
                AVPBlocks.NETHER_RESIN,
                AVPBlocks.NETHER_RESIN_NODE,
                AVPBlocks.NUKE_BLOCK,
                AVPBlocks.RAW_BAUXITE_BLOCK,
                AVPBlocks.RAW_GALENA_BLOCK,
                AVPBlocks.RAW_MONAZITE_BLOCK,
                AVPBlocks.RAW_SILICA_BLOCK,
                AVPBlocks.RAW_ZINC_BLOCK,
                AVPBlocks.REDSTONE_GENERATOR,
                AVPBlocks.RESIN,
                AVPBlocks.RESIN_NODE,
                AVPBlocks.RESIN_BRICKS,
                AVPBlocks.RESIN_O,
                AVPBlocks.RESIN_RIBBED,
                AVPBlocks.RESIN_SMOOTH,
                AVPBlocks.RESONATOR_BLOCK,
                AVPBlocks.SENTRY_TURRET,
                AVPBlocks.TRINITITE_BLOCK,
                AVPBlocks.TRIP_MINE_BLOCK,
                TempAVPBlocks.URANIUM_BLOCK.get(),
                TempAVPBlocks.ZINC_BLOCK.get(),
                TempAVPBlocks.ZINC_ORE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(
                AVPBlocks.ASH_BLOCK,
                AVPBlocks.SILICA_GRAVEL
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .add(
                TempAVPBlocks.BAUXITE_ORE.get(),
                AVPBlocks.BLUEPRINT_BLOCK,
                TempAVPBlocks.GALENA_ORE.get(),
                AVPBlocks.RAW_BAUXITE_BLOCK
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.AMMO_CHEST,
                TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.DESK_TERMINAL_BLOCK,
                AVPBlocks.INDUSTRIAL_FURNACE,
                AVPBlocks.LEAD_CHEST,
                AVPBlocks.NUKE_BLOCK,
                AVPBlocks.RAW_TITANIUM_BLOCK,
                AVPBlocks.REDSTONE_GENERATOR,
                AVPBlocks.RESONATOR_BLOCK,
                AVPBlocks.SENTRY_TURRET,
                AVPBlocks.TRINITITE_BLOCK,
                AVPBlocks.TRIP_MINE_BLOCK,
                TempAVPBlocks.URANIUM_BLOCK.get()
            );

        var slabTagProvider = getOrCreateTagBuilder(BlockTags.SLABS);

        slabTagProvider.add(
            AVPBlocks.INDUSTRIAL_GLASS_SLAB,
            AVPBlocks.CUT_FERROALUMINUM_SLAB,
            AVPBlocks.CUT_STEEL_SLAB,
            AVPBlocks.CUT_TITANIUM_SLAB,
            TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get(),
            TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get(),
            TempAVPBlocks.FERROALUMINUM_GRATE_SLAB.get(),
            TempAVPBlocks.FERROALUMINUM_PLATING_SLAB.get(),
            TempAVPBlocks.FERROALUMINUM_SIDING_SLAB.get(),
            TempAVPBlocks.FERROALUMINUM_SLAB.get(),
            TempAVPBlocks.FERROALUMINUM_STANDING_SLAB.get(),
            TempAVPBlocks.FERROALUMINUM_TREAD_SLAB.get(),
            TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB.get(),
            TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB.get(),
            TempAVPBlocks.STEEL_GRATE_SLAB.get(),
            TempAVPBlocks.STEEL_PLATING_SLAB.get(),
            TempAVPBlocks.STEEL_SIDING_SLAB.get(),
            TempAVPBlocks.STEEL_SLAB.get(),
            TempAVPBlocks.STEEL_STANDING_SLAB.get(),
            TempAVPBlocks.STEEL_TREAD_SLAB.get(),
            TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB.get(),
            TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB.get(),
            TempAVPBlocks.TITANIUM_GRATE_SLAB.get(),
            TempAVPBlocks.TITANIUM_PLATING_SLAB.get(),
            TempAVPBlocks.TITANIUM_SIDING_SLAB.get(),
            TempAVPBlocks.TITANIUM_SLAB.get(),
            TempAVPBlocks.TITANIUM_STANDING_SLAB.get(),
            TempAVPBlocks.TITANIUM_TREAD_SLAB.get()
        );

        // TODO: Use a stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);

        var pressurePlateTagProvider = getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES);

        pressurePlateTagProvider.add(
            AVPBlocks.FERROALUMINUM_PRESSURE_PLATE,
            TempAVPBlocks.STEEL_PRESSURE_PLATE.get(),
            AVPBlocks.TITANIUM_PRESSURE_PLATE
        );

        var doorTagProvider = getOrCreateTagBuilder(BlockTags.DOORS);

        doorTagProvider.add(
            AVPBlocks.INDUSTRIAL_GLASS_DOOR,
            AVPBlocks.FERROALUMINUM_DOOR,
            AVPBlocks.STEEL_DOOR,
            AVPBlocks.TITANIUM_DOOR
        );

        var trapdoorTagProvider = getOrCreateTagBuilder(BlockTags.TRAPDOORS);

        trapdoorTagProvider.add(
            AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR,
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
            TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
            TempAVPBlocks.STEEL_CHAIN_FENCE.get(),
            TempAVPBlocks.TITANIUM_CHAIN_FENCE.get()
        );

        var stairsTagProvider = getOrCreateTagBuilder(BlockTags.STAIRS);

        stairsTagProvider.add(
            AVPBlocks.INDUSTRIAL_GLASS_STAIRS,
            AVPBlocks.CUT_FERROALUMINUM_STAIRS,
            AVPBlocks.CUT_STEEL_STAIRS,
            AVPBlocks.CUT_TITANIUM_STAIRS,
            TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get(),
            TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get(),
            TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS.get(),
            TempAVPBlocks.FERROALUMINUM_PLATING_STAIRS.get(),
            TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS.get(),
            TempAVPBlocks.FERROALUMINUM_STAIRS.get(),
            TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS.get(),
            TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS.get(),
            TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS.get(),
            TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS.get(),
            TempAVPBlocks.STEEL_GRATE_STAIRS.get(),
            TempAVPBlocks.STEEL_PLATING_STAIRS.get(),
            TempAVPBlocks.STEEL_SIDING_STAIRS.get(),
            TempAVPBlocks.STEEL_STAIRS.get(),
            TempAVPBlocks.STEEL_STANDING_STAIRS.get(),
            TempAVPBlocks.STEEL_TREAD_STAIRS.get(),
            TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get(),
            TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get(),
            TempAVPBlocks.TITANIUM_GRATE_STAIRS.get(),
            TempAVPBlocks.TITANIUM_PLATING_STAIRS.get(),
            TempAVPBlocks.TITANIUM_SIDING_STAIRS.get(),
            TempAVPBlocks.TITANIUM_STAIRS.get(),
            TempAVPBlocks.TITANIUM_STANDING_STAIRS.get(),
            TempAVPBlocks.TITANIUM_TREAD_STAIRS.get()
        );

        // TODO: Use a stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);

        var wallTagBuilder = getOrCreateTagBuilder(BlockTags.WALLS);

        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(wallTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.MARINE_SPAWN_BLOCKS).add(
            TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(DyeColor.BLACK).get(),
            TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(DyeColor.LIGHT_GRAY).get(),
            TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(DyeColor.GRAY).get(),
            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(DyeColor.BLACK).get(),
            Blocks.CYAN_TERRACOTTA,
            Blocks.MOSS_BLOCK,
            TempAVPBlocks.FERROALUMINUM_TREAD.get(),
            TempAVPBlocks.STEEL_TREAD.get(),
            Blocks.GRAVEL,
            TempAVPBlocks.STEEL_GRATE.get(),
            TempAVPBlocks.TITANIUM_TREAD.get()
        );

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_BLOCK_TAG);

        getOrCreateTagBuilder(CommonConstants.CHESTS)
            .setReplace(false)
            .add(
                AVPBlocks.AMMO_CHEST,
                AVPBlocks.LEAD_CHEST
            );

        getOrCreateTagBuilder(CommonConstants.ORES_BLOCKS)
            .setReplace(false)
            .add(
                TempAVPBlocks.AUTUNITE_ORE.get(),
                TempAVPBlocks.BAUXITE_ORE.get(),
                TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                TempAVPBlocks.DEEPSLATE_ZINC_ORE.get(),
                TempAVPBlocks.GALENA_ORE.get(),
                TempAVPBlocks.LITHIUM_ORE.get(),
                TempAVPBlocks.MONAZITE_ORE.get(),
                TempAVPBlocks.ZINC_ORE.get()
            );
    }
}
