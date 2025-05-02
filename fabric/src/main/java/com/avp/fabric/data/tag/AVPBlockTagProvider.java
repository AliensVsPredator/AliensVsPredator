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
                TempAVPBlocks.CHISELED_FERROALUMINUM.get(),
                TempAVPBlocks.CUT_FERROALUMINUM.get(),
                TempAVPBlocks.CUT_FERROALUMINUM_SLAB.get(),
                TempAVPBlocks.CUT_FERROALUMINUM_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_BLOCK.get(),
                TempAVPBlocks.FERROALUMINUM_BUTTON.get(),
                TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                TempAVPBlocks.FERROALUMINUM_COLUMN.get(),
                TempAVPBlocks.FERROALUMINUM_DOOR.get(),
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
                TempAVPBlocks.FERROALUMINUM_PRESSURE_PLATE.get(),
                TempAVPBlocks.FERROALUMINUM_SIDING.get(),
                TempAVPBlocks.FERROALUMINUM_SIDING_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_STANDING.get(),
                TempAVPBlocks.FERROALUMINUM_STANDING_SLAB.get(),
                TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS.get(),
                TempAVPBlocks.FERROALUMINUM_TRAP_DOOR.get(),
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

        industrialGlassBlockTagBuilder.add(TempAVPBlocks.INDUSTRIAL_GLASS.get());
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().stream().map(Supplier::get).forEach(industrialGlassBlockTagBuilder::add);

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(TempAVPBlocks.INDUSTRIAL_GLASS_PANE.get());
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().stream().map(Supplier::get).forEach(industrialGlassPaneTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_PANE)
            .add(
                TempAVPBlocks.INDUSTRIAL_GLASS_DOOR.get(),
                TempAVPBlocks.INDUSTRIAL_GLASS_SLAB.get(),
                TempAVPBlocks.INDUSTRIAL_GLASS_STAIRS.get(),
                TempAVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get()
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
                TempAVPBlocks.IRRADIATED_RESIN.get(),
                TempAVPBlocks.IRRADIATED_RESIN_NODE.get(),
                TempAVPBlocks.IRRADIATED_RESIN_VEIN.get(),
                TempAVPBlocks.IRRADIATED_RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.ABERRANT_RESIN)
            .add(
                TempAVPBlocks.ABERRANT_RESIN.get(),
                TempAVPBlocks.ABERRANT_RESIN_NODE.get(),
                TempAVPBlocks.ABERRANT_RESIN_VEIN.get(),
                TempAVPBlocks.ABERRANT_RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.NETHER_RESIN)
            .add(
                TempAVPBlocks.NETHER_RESIN.get(),
                TempAVPBlocks.NETHER_RESIN_NODE.get(),
                TempAVPBlocks.NETHER_RESIN_VEIN.get(),
                TempAVPBlocks.NETHER_RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.NORMAL_RESIN)
            .add(
                TempAVPBlocks.RESIN.get(),
                TempAVPBlocks.RESIN_NODE.get(),
                TempAVPBlocks.RESIN_VEIN.get(),
                TempAVPBlocks.RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.CRAFTED_RESIN)
            .add(
                TempAVPBlocks.RESIN_BRICKS.get(),
                TempAVPBlocks.RESIN_O.get(),
                TempAVPBlocks.RESIN_RIBBED.get(),
                TempAVPBlocks.RESIN_SMOOTH.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN)
            .addTag(AVPBlockTags.ABERRANT_RESIN)
            .addTag(AVPBlockTags.CRAFTED_RESIN)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.RESIN_VEINS)
            .add(
                TempAVPBlocks.ABERRANT_RESIN_VEIN.get(),
                TempAVPBlocks.IRRADIATED_RESIN_VEIN.get(),
                TempAVPBlocks.NETHER_RESIN_VEIN.get(),
                TempAVPBlocks.RESIN_VEIN.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN_WEBS)
            .add(
                TempAVPBlocks.ABERRANT_RESIN_WEB.get(),
                TempAVPBlocks.IRRADIATED_RESIN_WEB.get(),
                TempAVPBlocks.NETHER_RESIN_WEB.get(),
                TempAVPBlocks.RESIN_WEB.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.STEEL)
            .add(
                TempAVPBlocks.CHISELED_STEEL.get(),
                TempAVPBlocks.CUT_STEEL.get(),
                TempAVPBlocks.CUT_STEEL_SLAB.get(),
                TempAVPBlocks.CUT_STEEL_STAIRS.get(),
                TempAVPBlocks.STEEL_BARS.get(),
                TempAVPBlocks.STEEL_BLOCK.get(),
                TempAVPBlocks.STEEL_BUTTON.get(),
                TempAVPBlocks.STEEL_CHAIN_FENCE.get(),
                TempAVPBlocks.STEEL_COLUMN.get(),
                TempAVPBlocks.STEEL_DOOR.get(),
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
                TempAVPBlocks.STEEL_TRAP_DOOR.get(),
                TempAVPBlocks.STEEL_TREAD.get(),
                TempAVPBlocks.STEEL_TREAD_SLAB.get(),
                TempAVPBlocks.STEEL_TREAD_STAIRS.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.TITANIUM)
            .add(
                TempAVPBlocks.CHISELED_TITANIUM.get(),
                TempAVPBlocks.CUT_TITANIUM.get(),
                TempAVPBlocks.CUT_TITANIUM_SLAB.get(),
                TempAVPBlocks.CUT_TITANIUM_STAIRS.get(),
                TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                TempAVPBlocks.RAW_TITANIUM_BLOCK.get(),
                TempAVPBlocks.TITANIUM_BLOCK.get(),
                TempAVPBlocks.TITANIUM_BUTTON.get(),
                TempAVPBlocks.TITANIUM_CHAIN_FENCE.get(),
                TempAVPBlocks.TITANIUM_COLUMN.get(),
                TempAVPBlocks.TITANIUM_DOOR.get(),
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
                TempAVPBlocks.TITANIUM_PRESSURE_PLATE.get(),
                TempAVPBlocks.TITANIUM_SIDING.get(),
                TempAVPBlocks.TITANIUM_SIDING_SLAB.get(),
                TempAVPBlocks.TITANIUM_SIDING_STAIRS.get(),
                TempAVPBlocks.TITANIUM_SLAB.get(),
                TempAVPBlocks.TITANIUM_STAIRS.get(),
                TempAVPBlocks.TITANIUM_STANDING.get(),
                TempAVPBlocks.TITANIUM_STANDING_SLAB.get(),
                TempAVPBlocks.TITANIUM_STANDING_STAIRS.get(),
                TempAVPBlocks.TITANIUM_TRAP_DOOR.get(),
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
                TempAVPBlocks.ABERRANT_RESIN.get(),
                TempAVPBlocks.BLUEPRINT_BLOCK.get(),
                TempAVPBlocks.IRRADIATED_RESIN.get()
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
                TempAVPBlocks.ABERRANT_RESIN.get(),
                TempAVPBlocks.ABERRANT_RESIN_NODE.get(),
                TempAVPBlocks.ALUMINUM_BLOCK.get(),
                TempAVPBlocks.AMMO_CHEST.get(),
                TempAVPBlocks.AUTUNITE_BLOCK.get(),
                TempAVPBlocks.AUTUNITE_ORE.get(),
                TempAVPBlocks.BAUXITE_ORE.get(),
                TempAVPBlocks.BRASS_BLOCK.get(),
                TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                TempAVPBlocks.DEEPSLATE_ZINC_ORE.get(),
                TempAVPBlocks.DESK_TERMINAL_BLOCK.get(),
                TempAVPBlocks.GALENA_ORE.get(),
                TempAVPBlocks.INDUSTRIAL_FURNACE.get(),
                TempAVPBlocks.IRRADIATED_RESIN.get(),
                TempAVPBlocks.IRRADIATED_RESIN_NODE.get(),
                TempAVPBlocks.LEAD_BLOCK.get(),
                TempAVPBlocks.LEAD_CHEST.get(),
                TempAVPBlocks.LITHIUM_BLOCK.get(),
                TempAVPBlocks.LITHIUM_ORE.get(),
                TempAVPBlocks.MONAZITE_ORE.get(),
                TempAVPBlocks.NETHER_RESIN.get(),
                TempAVPBlocks.NETHER_RESIN_NODE.get(),
                TempAVPBlocks.NUKE_BLOCK.get(),
                TempAVPBlocks.RAW_BAUXITE_BLOCK.get(),
                TempAVPBlocks.RAW_GALENA_BLOCK.get(),
                TempAVPBlocks.RAW_MONAZITE_BLOCK.get(),
                TempAVPBlocks.RAW_SILICA_BLOCK.get(),
                TempAVPBlocks.RAW_ZINC_BLOCK.get(),
                TempAVPBlocks.REDSTONE_GENERATOR.get(),
                TempAVPBlocks.RESIN.get(),
                TempAVPBlocks.RESIN_NODE.get(),
                TempAVPBlocks.RESIN_BRICKS.get(),
                TempAVPBlocks.RESIN_O.get(),
                TempAVPBlocks.RESIN_RIBBED.get(),
                TempAVPBlocks.RESIN_SMOOTH.get(),
                TempAVPBlocks.RESONATOR_BLOCK.get(),
                TempAVPBlocks.SENTRY_TURRET.get(),
                TempAVPBlocks.TRINITITE_BLOCK.get(),
                TempAVPBlocks.TRIP_MINE_BLOCK.get(),
                TempAVPBlocks.URANIUM_BLOCK.get(),
                TempAVPBlocks.ZINC_BLOCK.get(),
                TempAVPBlocks.ZINC_ORE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(
                TempAVPBlocks.ASH_BLOCK.get(),
                TempAVPBlocks.SILICA_GRAVEL.get()
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .add(
                TempAVPBlocks.BAUXITE_ORE.get(),
                TempAVPBlocks.BLUEPRINT_BLOCK.get(),
                TempAVPBlocks.GALENA_ORE.get(),
                TempAVPBlocks.RAW_BAUXITE_BLOCK.get()
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(
                TempAVPBlocks.AMMO_CHEST.get(),
                TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                TempAVPBlocks.DESK_TERMINAL_BLOCK.get(),
                TempAVPBlocks.INDUSTRIAL_FURNACE.get(),
                TempAVPBlocks.LEAD_CHEST.get(),
                TempAVPBlocks.NUKE_BLOCK.get(),
                TempAVPBlocks.RAW_TITANIUM_BLOCK.get(),
                TempAVPBlocks.REDSTONE_GENERATOR.get(),
                TempAVPBlocks.RESONATOR_BLOCK.get(),
                TempAVPBlocks.SENTRY_TURRET.get(),
                TempAVPBlocks.TRINITITE_BLOCK.get(),
                TempAVPBlocks.TRIP_MINE_BLOCK.get(),
                TempAVPBlocks.URANIUM_BLOCK.get()
            );

        var slabTagProvider = getOrCreateTagBuilder(BlockTags.SLABS);

        slabTagProvider.add(
            TempAVPBlocks.INDUSTRIAL_GLASS_SLAB.get(),
            TempAVPBlocks.CUT_FERROALUMINUM_SLAB.get(),
            TempAVPBlocks.CUT_STEEL_SLAB.get(),
            TempAVPBlocks.CUT_TITANIUM_SLAB.get(),
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
            TempAVPBlocks.FERROALUMINUM_PRESSURE_PLATE.get(),
            TempAVPBlocks.STEEL_PRESSURE_PLATE.get(),
            TempAVPBlocks.TITANIUM_PRESSURE_PLATE.get()
        );

        var doorTagProvider = getOrCreateTagBuilder(BlockTags.DOORS);

        doorTagProvider.add(
            TempAVPBlocks.INDUSTRIAL_GLASS_DOOR.get(),
            TempAVPBlocks.FERROALUMINUM_DOOR.get(),
            TempAVPBlocks.STEEL_DOOR.get(),
            TempAVPBlocks.TITANIUM_DOOR.get()
        );

        var trapdoorTagProvider = getOrCreateTagBuilder(BlockTags.TRAPDOORS);

        trapdoorTagProvider.add(
            TempAVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get(),
            TempAVPBlocks.FERROALUMINUM_TRAP_DOOR.get(),
            TempAVPBlocks.STEEL_TRAP_DOOR.get(),
            TempAVPBlocks.TITANIUM_TRAP_DOOR.get()
        );

        var buttonTagProvider = getOrCreateTagBuilder(BlockTags.BUTTONS);

        buttonTagProvider.add(
            TempAVPBlocks.FERROALUMINUM_BUTTON.get(),
            TempAVPBlocks.STEEL_BUTTON.get(),
            TempAVPBlocks.TITANIUM_BUTTON.get()
        );

        var fenceTagProvider = getOrCreateTagBuilder(BlockTags.FENCES);

        fenceTagProvider.add(
            TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
            TempAVPBlocks.STEEL_CHAIN_FENCE.get(),
            TempAVPBlocks.TITANIUM_CHAIN_FENCE.get()
        );

        var stairsTagProvider = getOrCreateTagBuilder(BlockTags.STAIRS);

        stairsTagProvider.add(
            TempAVPBlocks.INDUSTRIAL_GLASS_STAIRS.get(),
            TempAVPBlocks.CUT_FERROALUMINUM_STAIRS.get(),
            TempAVPBlocks.CUT_STEEL_STAIRS.get(),
            TempAVPBlocks.CUT_TITANIUM_STAIRS.get(),
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
                TempAVPBlocks.AMMO_CHEST.get(),
                TempAVPBlocks.LEAD_CHEST.get()
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
