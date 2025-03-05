package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.item.AVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class ResinRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createResinRecipes(builder);
    }

    private static void createResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AVPItems.NETHER_RESIN_BALL, AVPBlocks.NETHER_RESIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AVPItems.NETHER_RESIN_BALL.get()))
            .into(5, AVPBlocks.NETHER_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AVPItems.NETHER_RESIN_BALL.get()))
            .into(1, AVPBlocks.NETHER_RESIN_WEB);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, AVPItems.RESIN_BALL, AVPBlocks.RESIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AVPItems.RESIN_BALL.get()))
            .into(5, AVPBlocks.RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AVPItems.RESIN_BALL.get()))
            .into(1, AVPBlocks.RESIN_WEB);
    }
}
