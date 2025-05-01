package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class ResinRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createResinRecipes(builder);
    }

    private static void createResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, TempAVPItems.IRRADIATED_RESIN_BALL.get(), TempAVPBlocks.IRRADIATED_RESIN.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(TempAVPItems.IRRADIATED_RESIN_BALL.get()))
            .into(5, TempAVPBlocks.IRRADIATED_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(TempAVPItems.IRRADIATED_RESIN_BALL.get()))
            .into(1, TempAVPBlocks.IRRADIATED_RESIN_WEB);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, AVPItems.ABERRANT_RESIN_BALL, TempAVPBlocks.ABERRANT_RESIN.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AVPItems.ABERRANT_RESIN_BALL))
            .into(5, TempAVPBlocks.ABERRANT_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AVPItems.ABERRANT_RESIN_BALL))
            .into(1, TempAVPBlocks.ABERRANT_RESIN_WEB);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, AVPItems.NETHER_RESIN_BALL, TempAVPBlocks.NETHER_RESIN.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AVPItems.NETHER_RESIN_BALL))
            .into(5, TempAVPBlocks.NETHER_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AVPItems.NETHER_RESIN_BALL))
            .into(1, TempAVPBlocks.NETHER_RESIN_WEB);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, TempAVPItems.RESIN_BALL.get(), TempAVPBlocks.RESIN.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(TempAVPItems.RESIN_BALL.get()))
            .into(5, TempAVPBlocks.RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(TempAVPItems.RESIN_BALL.get()))
            .into(1, TempAVPBlocks.RESIN_WEB);
    }
}
