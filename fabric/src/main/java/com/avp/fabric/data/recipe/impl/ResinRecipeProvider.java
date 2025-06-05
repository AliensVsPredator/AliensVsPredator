package com.avp.fabric.data.recipe.impl;

import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.block.AlienResinBlocks;
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
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AlienItems.ABERRANT_RESIN_BALL.get(), AlienResinBlocks.ABERRANT_RESIN.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(
            builder,
            AlienResinBlocks.ABERRANT_RESIN.get(),
            AlienResinBlocks.ABERRANT_RESIN_SLAB.get()
        );
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(
            builder,
            AlienResinBlocks.ABERRANT_RESIN.get(),
            AlienResinBlocks.ABERRANT_RESIN_STAIRS.get()
        );

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AlienItems.ABERRANT_RESIN_BALL.get()))
            .into(5, AlienResinBlocks.ABERRANT_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AlienItems.ABERRANT_RESIN_BALL.get()))
            .into(1, AlienResinBlocks.ABERRANT_RESIN_WEB);
    }

    private static void createIrradiatedResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(
            builder,
            AlienItems.IRRADIATED_RESIN_BALL.get(),
            AlienResinBlocks.IRRADIATED_RESIN.get()
        );

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(
            builder,
            AlienResinBlocks.IRRADIATED_RESIN.get(),
            AlienResinBlocks.IRRADIATED_RESIN_SLAB.get()
        );
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(
            builder,
            AlienResinBlocks.IRRADIATED_RESIN.get(),
            AlienResinBlocks.IRRADIATED_RESIN_STAIRS.get()
        );

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AlienItems.IRRADIATED_RESIN_BALL.get()))
            .into(5, AlienResinBlocks.IRRADIATED_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AlienItems.IRRADIATED_RESIN_BALL.get()))
            .into(1, AlienResinBlocks.IRRADIATED_RESIN_WEB);
    }

    private static void createNetherResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AlienItems.NETHER_RESIN_BALL.get(), AlienResinBlocks.NETHER_RESIN.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(
            builder,
            AlienResinBlocks.NETHER_RESIN.get(),
            AlienResinBlocks.NETHER_RESIN_SLAB.get()
        );
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(
            builder,
            AlienResinBlocks.NETHER_RESIN.get(),
            AlienResinBlocks.NETHER_RESIN_STAIRS.get()
        );

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AlienItems.NETHER_RESIN_BALL.get()))
            .into(5, AlienResinBlocks.NETHER_RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AlienItems.NETHER_RESIN_BALL.get()))
            .into(1, AlienResinBlocks.NETHER_RESIN_WEB);
    }

    private static void createNormalResinRecipes(RecipeBuilder builder) {
        RecipeUtil.createCompressedBlockRecipes2x2(builder, AlienItems.RESIN_BALL.get(), AlienResinBlocks.RESIN.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, AlienResinBlocks.RESIN.get(), AlienResinBlocks.RESIN_SLAB.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, AlienResinBlocks.RESIN.get(), AlienResinBlocks.RESIN_STAIRS.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(AlienItems.RESIN_BALL.get()))
            .into(5, AlienResinBlocks.RESIN_VEIN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(AlienItems.RESIN_BALL.get()))
            .into(1, AlienResinBlocks.RESIN_WEB);

        // Crafted resin recipes
        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(
            builder,
            AlienResinBlocks.RESIN_BRICKS.get(),
            AlienResinBlocks.RESIN_BRICK_SLAB.get()
        );
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(
            builder,
            AlienResinBlocks.RESIN_BRICKS.get(),
            AlienResinBlocks.RESIN_BRICK_STAIRS.get()
        );
    }
}
