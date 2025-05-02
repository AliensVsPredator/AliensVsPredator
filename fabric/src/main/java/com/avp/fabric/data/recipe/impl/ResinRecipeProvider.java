package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class ResinRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createResinRecipes(builder);
    }

    private static void createResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, TempAVPItems.IRRADIATED_RESIN_BALL.get(), AVPBlocks.IRRADIATED_RESIN.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(TempAVPItems.IRRADIATED_RESIN_BALL.get()))
            .into(5, AVPBlocks.IRRADIATED_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(TempAVPItems.IRRADIATED_RESIN_BALL.get()))
            .into(1, AVPBlocks.IRRADIATED_RESIN_WEB);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, TempAVPItems.ABERRANT_RESIN_BALL.get(), AVPBlocks.ABERRANT_RESIN.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(TempAVPItems.ABERRANT_RESIN_BALL.get()))
            .into(5, AVPBlocks.ABERRANT_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(TempAVPItems.ABERRANT_RESIN_BALL.get()))
            .into(1, AVPBlocks.ABERRANT_RESIN_WEB);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, TempAVPItems.NETHER_RESIN_BALL.get(), AVPBlocks.NETHER_RESIN.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(TempAVPItems.NETHER_RESIN_BALL.get()))
            .into(5, AVPBlocks.NETHER_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(TempAVPItems.NETHER_RESIN_BALL.get()))
            .into(1, AVPBlocks.NETHER_RESIN_WEB);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, TempAVPItems.RESIN_BALL.get(), AVPBlocks.RESIN.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(TempAVPItems.RESIN_BALL.get()))
            .into(5, AVPBlocks.RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(TempAVPItems.RESIN_BALL.get()))
            .into(1, AVPBlocks.RESIN_WEB);
    }
}
