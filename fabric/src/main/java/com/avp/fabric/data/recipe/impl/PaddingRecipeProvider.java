package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;

import com.avp.core.common.block.AVPBlocks;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class PaddingRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createPaddingRecipes(builder);
    }

    private static void createPaddingRecipes(RecipeBuilder builder) {
        AVPBlocks.DYE_COLOR_TO_PADDING.forEach((dyeColor, block) -> {
            builder.shapeless()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .requires(1, DyeItem.byColor(dyeColor))
                .requires(1, Items.LEATHER)
                .requires(1, ItemTags.WOOL)
                .into(4, block);

            var stonecutBuilder = builder.stonecut(block)
                .withCategory(RecipeCategory.BUILDING_BLOCKS);

            var panelBlock = AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor);
            var pipeBlock = AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor);
            stonecutBuilder.into(1, panelBlock);
            stonecutBuilder.into(1, pipeBlock);

            var slab = AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor);
            var stair = AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor);
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, block, slab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, block, stair);

            var panelSlab = AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor);
            var panelStair = AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor);
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, panelBlock, panelSlab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, panelBlock, panelStair);

            var pipeSlab = AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor);
            var pipeStair = AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor);
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, pipeBlock, pipeSlab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, pipeBlock, pipeStair);
        });
    }
}
