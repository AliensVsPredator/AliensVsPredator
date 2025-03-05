package com.avp.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
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
                AVPBlocks.FERROALUMINUM_TREAD
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

        getOrCreateTagBuilder(AVPBlockTags.RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

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
                AVPBlocks.STEEL_FASTENED_STANDING,
                AVPBlocks.STEEL_GRATE,
                AVPBlocks.STEEL_PLATING,
                AVPBlocks.STEEL_SIDING,
                AVPBlocks.STEEL_STANDING,
                AVPBlocks.STEEL_TREAD
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
                AVPBlocks.TITANIUM_FASTENED_STANDING,
                AVPBlocks.TITANIUM_GRATE,
                AVPBlocks.TITANIUM_PLATING,
                AVPBlocks.TITANIUM_SIDING,
                AVPBlocks.TITANIUM_STANDING,
                AVPBlocks.TITANIUM_TREAD
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
                AVPBlocks.FERROALUMINUM_CHAIN_FENCE,
                AVPBlocks.STEEL_CHAIN_FENCE,
                AVPBlocks.TITANIUM_CHAIN_FENCE
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .add(
                AVPBlocks.NETHER_RESIN_VEIN,
                AVPBlocks.NETHER_RESIN_WEB,
                AVPBlocks.RESIN_VEIN,
                AVPBlocks.RESIN_WEB
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
                AVPBlocks.ALUMINUM_BLOCK,
                AVPBlocks.AUTUNITE_BLOCK,
                AVPBlocks.AUTUNITE_ORE,
                AVPBlocks.BAUXITE_ORE,
                AVPBlocks.BRASS_BLOCK,
                AVPBlocks.DEEPSLATE_TITANIUM_ORE,
                AVPBlocks.DEEPSLATE_ZINC_ORE,
                AVPBlocks.GALENA_ORE,
                AVPBlocks.LEAD_BLOCK,
                AVPBlocks.LITHIUM_BLOCK,
                AVPBlocks.LITHIUM_ORE,
                AVPBlocks.MONAZITE_ORE,
                AVPBlocks.NETHER_RESIN,
                AVPBlocks.NETHER_RESIN_NODE,
                AVPBlocks.RAW_BAUXITE_BLOCK,
                AVPBlocks.RAW_GALENA_BLOCK,
                AVPBlocks.RAW_MONAZITE_BLOCK,
                AVPBlocks.RAW_SILICA_BLOCK,
                AVPBlocks.RAW_ZINC_BLOCK,
                AVPBlocks.RESIN,
                AVPBlocks.RESIN_NODE,
                AVPBlocks.URANIUM_BLOCK,
                AVPBlocks.ZINC_BLOCK,
                AVPBlocks.ZINC_ORE
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(
                AVPBlocks.SILICA_GRAVEL
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .add(
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
                AVPBlocks.DEEPSLATE_TITANIUM_ORE,
                AVPBlocks.RAW_TITANIUM_BLOCK,
                AVPBlocks.URANIUM_BLOCK
            );

        var slabTagProvider = getOrCreateTagBuilder(BlockTags.SLABS);

        slabTagProvider.add(
            AVPBlocks.CUT_FERROALUMINUM_SLAB,
            AVPBlocks.CUT_STEEL_SLAB,
            AVPBlocks.CUT_TITANIUM_SLAB
        );

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().forEach(slabTagProvider::add);

        var stairsTagProvider = getOrCreateTagBuilder(BlockTags.STAIRS);

        stairsTagProvider.add(
            AVPBlocks.CUT_FERROALUMINUM_STAIRS,
            AVPBlocks.CUT_STEEL_STAIRS,
            AVPBlocks.CUT_TITANIUM_STAIRS
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

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_BLOCK_TAG);
    }
}
