package com.avp.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;
import com.avp.data.recipe.RecipeTemplates;
import com.avp.data.recipe.builder.RecipeBuilder;
import com.avp.data.recipe.util.RecipeUtil;

public class ResinRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createResinRecipes(builder);
    }

    private static void createResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AVPItems.NETHER_RESIN_BALL, AVPBlocks.NETHER_RESIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AVPItems.NETHER_RESIN_BALL))
            .into(5, AVPBlocks.NETHER_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AVPItems.NETHER_RESIN_BALL))
            .into(1, AVPBlocks.NETHER_RESIN_WEB);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, AVPItems.RESIN_BALL, AVPBlocks.RESIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AVPItems.RESIN_BALL))
            .into(5, AVPBlocks.RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AVPItems.RESIN_BALL))
            .into(1, AVPBlocks.RESIN_WEB);
    }
}
