package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.avp.core.common.block.AVPBlockTags;
import com.avp.core.common.block.AVPBlocks;
import com.avp.fabric.data.compatibility.gigeresque.GigeresqueConstants;

public class AVPBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public AVPBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var concreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.CONCRETE);

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(concreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(concreteTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.FERROALUMINUM)
            .add(
                AVPBlocks.CHISELED_FERROALUMINUM.get(),
                AVPBlocks.CUT_FERROALUMINUM.get(),
                AVPBlocks.CUT_FERROALUMINUM_SLAB.get(),
                AVPBlocks.CUT_FERROALUMINUM_STAIRS.get(),
                AVPBlocks.FERROALUMINUM_BLOCK.get(),
                AVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                AVPBlocks.FERROALUMINUM_COLUMN.get(),
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING.get(),
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING.get(),
                AVPBlocks.FERROALUMINUM_GRATE.get(),
                AVPBlocks.FERROALUMINUM_PLATING.get(),
                AVPBlocks.FERROALUMINUM_SIDING.get(),
                AVPBlocks.FERROALUMINUM_STANDING.get(),
                AVPBlocks.FERROALUMINUM_TREAD.get()
            );

        var industrialConcreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_CONCRETE);

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(industrialConcreteTagBuilder::add);

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(AVPBlocks.INDUSTRIAL_GLASS.get());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().stream().map(Supplier::get).forEach(industrialGlassBlockTagBuilder::add);

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(AVPBlocks.INDUSTRIAL_GLASS_PANE.get());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().stream().map(Supplier::get).forEach(industrialGlassPaneTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_PANE);

        var paddingTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PADDING);

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

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);

        AVPBlocks.DYE_COLOR_TO_PLASTIC.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(plasticTagBuilder::add);

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

        getOrCreateTagBuilder(AVPBlockTags.RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.STEEL)
            .add(
                AVPBlocks.CHISELED_STEEL.get(),
                AVPBlocks.CUT_STEEL.get(),
                AVPBlocks.CUT_STEEL_SLAB.get(),
                AVPBlocks.CUT_STEEL_STAIRS.get(),
                AVPBlocks.STEEL_BARS.get(),
                AVPBlocks.STEEL_BLOCK.get(),
                AVPBlocks.STEEL_CHAIN_FENCE.get(),
                AVPBlocks.STEEL_COLUMN.get(),
                AVPBlocks.STEEL_FASTENED_SIDING.get(),
                AVPBlocks.STEEL_FASTENED_STANDING.get(),
                AVPBlocks.STEEL_GRATE.get(),
                AVPBlocks.STEEL_PLATING.get(),
                AVPBlocks.STEEL_SIDING.get(),
                AVPBlocks.STEEL_STANDING.get(),
                AVPBlocks.STEEL_TREAD.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.CHISELED_TITANIUM.get(),
                AVPBlocks.CUT_TITANIUM.get(),
                AVPBlocks.CUT_TITANIUM_SLAB.get(),
                AVPBlocks.CUT_TITANIUM_STAIRS.get(),
                AVPBlocks.TITANIUM_BLOCK.get(),
                AVPBlocks.TITANIUM_CHAIN_FENCE.get(),
                AVPBlocks.TITANIUM_COLUMN.get(),
                AVPBlocks.TITANIUM_FASTENED_SIDING.get(),
                AVPBlocks.TITANIUM_FASTENED_STANDING.get(),
                AVPBlocks.TITANIUM_GRATE.get(),
                AVPBlocks.TITANIUM_PLATING.get(),
                AVPBlocks.TITANIUM_SIDING.get(),
                AVPBlocks.TITANIUM_STANDING.get(),
                AVPBlocks.TITANIUM_TREAD.get()
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
            .addOptionalTag(BlockTags.INFINIBURN_NETHER);

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
                AVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                AVPBlocks.STEEL_CHAIN_FENCE.get(),
                AVPBlocks.TITANIUM_CHAIN_FENCE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .add(
                AVPBlocks.NETHER_RESIN_VEIN.get(),
                AVPBlocks.NETHER_RESIN_WEB.get(),
                AVPBlocks.RESIN_VEIN.get(),
                AVPBlocks.RESIN_WEB.get()
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
                AVPBlocks.ALUMINUM_BLOCK.get(),
                AVPBlocks.AUTUNITE_BLOCK.get(),
                AVPBlocks.AUTUNITE_ORE.get(),
                AVPBlocks.BAUXITE_ORE.get(),
                AVPBlocks.BRASS_BLOCK.get(),
                AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.DEEPSLATE_ZINC_ORE.get(),
                AVPBlocks.GALENA_ORE.get(),
                AVPBlocks.LEAD_BLOCK.get(),
                AVPBlocks.LITHIUM_BLOCK.get(),
                AVPBlocks.LITHIUM_ORE.get(),
                AVPBlocks.MONAZITE_ORE.get(),
                AVPBlocks.NETHER_RESIN.get(),
                AVPBlocks.NETHER_RESIN_NODE.get(),
                AVPBlocks.RAW_BAUXITE_BLOCK.get(),
                AVPBlocks.RAW_GALENA_BLOCK.get(),
                AVPBlocks.RAW_MONAZITE_BLOCK.get(),
                AVPBlocks.RAW_SILICA_BLOCK.get(),
                AVPBlocks.RAW_ZINC_BLOCK.get(),
                AVPBlocks.RESIN.get(),
                AVPBlocks.RESIN_NODE.get(),
                AVPBlocks.URANIUM_BLOCK.get(),
                AVPBlocks.ZINC_BLOCK.get(),
                AVPBlocks.ZINC_ORE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(
                AVPBlocks.SILICA_GRAVEL.get()
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .add(
                AVPBlocks.BAUXITE_ORE.get(),
                AVPBlocks.GALENA_ORE.get(),
                AVPBlocks.RAW_BAUXITE_BLOCK.get()
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.RAW_TITANIUM_BLOCK.get(),
                AVPBlocks.URANIUM_BLOCK.get()
            );

        var slabTagProvider = getOrCreateTagBuilder(BlockTags.SLABS);

        slabTagProvider.add(
            AVPBlocks.CUT_FERROALUMINUM_SLAB.get(),
            AVPBlocks.CUT_STEEL_SLAB.get(),
            AVPBlocks.CUT_TITANIUM_SLAB.get()
        );

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);

        var stairsTagProvider = getOrCreateTagBuilder(BlockTags.STAIRS);

        stairsTagProvider.add(
            AVPBlocks.CUT_FERROALUMINUM_STAIRS.get(),
            AVPBlocks.CUT_STEEL_STAIRS.get(),
            AVPBlocks.CUT_TITANIUM_STAIRS.get()
        );

        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);

        var wallTagBuilder = getOrCreateTagBuilder(BlockTags.WALLS);

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(wallTagBuilder::add);

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_BLOCK_TAG);
    }
}
