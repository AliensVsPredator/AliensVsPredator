package com.avp.fabric.data.recipe.impl;

import com.avp.common.block.TempAVPBlocks;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;

import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class PaddingRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createPaddingRecipes(builder);
    }

    private static void createPaddingRecipes(RecipeBuilder builder) {
        TempAVPBlocks.DYE_COLOR_TO_PADDING.forEach((dyeColor, blockSupplier) -> {
            var block = blockSupplier.get();

            builder.shapeless()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .requires(1, DyeItem.byColor(dyeColor))
                .requires(1, Items.LEATHER)
                .requires(1, ItemTags.WOOL)
                .into(4, block);

            var stonecutBuilder = builder.stonecut(block)
                .withCategory(RecipeCategory.BUILDING_BLOCKS);

            var panelBlock = TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).get();
            var pipeBlock = TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).get();
            stonecutBuilder.into(1, panelBlock);
            stonecutBuilder.into(1, pipeBlock);

            var slab = TempAVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor).get();
            var stair = TempAVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, block, slab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, block, stair);

            var panelSlab = TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor).get();
            var panelStair = TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, panelBlock, panelSlab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, panelBlock, panelStair);

            var pipeSlab = TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor).get();
            var pipeStair = TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, pipeBlock, pipeSlab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, pipeBlock, pipeStair);
        });
    }
}
