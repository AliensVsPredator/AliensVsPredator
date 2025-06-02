package com.avp.fabric.data.recipe.impl;

import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienItems;
import net.minecraft.data.recipes.RecipeCategory;

import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class ResinRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createResinRecipes(builder);
    }

    private static void createResinRecipes(RecipeBuilder builder) {
        createAberrantResinRecipes(builder);
        createIrradiatedResinRecipes(builder);
        createNetherResinRecipes(builder);
        createNormalResinRecipes(builder);
    }

    private static void createAberrantResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AlienItems.ABERRANT_RESIN_BALL.get(), AlienBlocks.ABERRANT_RESIN.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(
            builder,
            AlienBlocks.ABERRANT_RESIN.get(),
            AlienBlocks.ABERRANT_RESIN_SLAB.get()
        );
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(
            builder,
            AlienBlocks.ABERRANT_RESIN.get(),
            AlienBlocks.ABERRANT_RESIN_STAIRS.get()
        );

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AlienItems.ABERRANT_RESIN_BALL.get()))
            .into(5, AlienBlocks.ABERRANT_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AlienItems.ABERRANT_RESIN_BALL.get()))
            .into(1, AlienBlocks.ABERRANT_RESIN_WEB);
    }

    private static void createIrradiatedResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AlienItems.IRRADIATED_RESIN_BALL.get(), AlienBlocks.IRRADIATED_RESIN.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(
            builder,
            AlienBlocks.IRRADIATED_RESIN.get(),
            AlienBlocks.IRRADIATED_RESIN_SLAB.get()
        );
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(
            builder,
            AlienBlocks.IRRADIATED_RESIN.get(),
            AlienBlocks.IRRADIATED_RESIN_STAIRS.get()
        );

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AlienItems.IRRADIATED_RESIN_BALL.get()))
            .into(5, AlienBlocks.IRRADIATED_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AlienItems.IRRADIATED_RESIN_BALL.get()))
            .into(1, AlienBlocks.IRRADIATED_RESIN_WEB);
    }

    private static void createNetherResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AlienItems.NETHER_RESIN_BALL.get(), AlienBlocks.NETHER_RESIN.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, AlienBlocks.NETHER_RESIN.get(), AlienBlocks.NETHER_RESIN_SLAB.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(
            builder,
            AlienBlocks.NETHER_RESIN.get(),
            AlienBlocks.NETHER_RESIN_STAIRS.get()
        );

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AlienItems.NETHER_RESIN_BALL.get()))
            .into(5, AlienBlocks.NETHER_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AlienItems.NETHER_RESIN_BALL.get()))
            .into(1, AlienBlocks.NETHER_RESIN_WEB);
    }

    private static void createNormalResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AlienItems.RESIN_BALL.get(), AlienBlocks.RESIN.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, AlienBlocks.RESIN.get(), AlienBlocks.RESIN_SLAB.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, AlienBlocks.RESIN.get(), AlienBlocks.RESIN_STAIRS.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AlienItems.RESIN_BALL.get()))
            .into(5, AlienBlocks.RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AlienItems.RESIN_BALL.get()))
            .into(1, AlienBlocks.RESIN_WEB);
    }
}
