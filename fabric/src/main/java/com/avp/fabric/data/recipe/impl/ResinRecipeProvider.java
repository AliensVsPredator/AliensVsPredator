package com.avp.fabric.data.recipe.impl;

import com.alien.common.gameplay.block.resin.vein.ResinVeinBlock;
import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.block.AlienResinBlocks;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class ResinRecipeProvider {

    private static final ResinSet ABERRANT_SET = new ResinSet(
        AlienItems.ABERRANT_RESIN_BALL,
        AlienResinBlocks.ABERRANT_RESIN,
        AlienResinBlocks.ABERRANT_RESIN_SLAB,
        AlienResinBlocks.ABERRANT_RESIN_STAIRS,
        AlienResinBlocks.ABERRANT_RESIN_BRICKS,
        AlienResinBlocks.ABERRANT_RESIN_BRICK_SLAB,
        AlienResinBlocks.ABERRANT_RESIN_BRICK_STAIRS,
        AlienResinBlocks.ABERRANT_RESIN_BRICK_WALL,
        AlienResinBlocks.SMOOTH_ABERRANT_RESIN,
        AlienResinBlocks.SMOOTH_ABERRANT_RESIN_SLAB,
        AlienResinBlocks.SMOOTH_ABERRANT_RESIN_STAIRS,
        AlienResinBlocks.SMOOTH_ABERRANT_RESIN_WALL,
        AlienResinBlocks.ABERRANT_RESIN_VEIN,
        AlienResinBlocks.ABERRANT_RESIN_WEB
    );

    private static final ResinSet IRRADIATED_SET = new ResinSet(
        AlienItems.IRRADIATED_RESIN_BALL,
        AlienResinBlocks.IRRADIATED_RESIN,
        AlienResinBlocks.IRRADIATED_RESIN_SLAB,
        AlienResinBlocks.IRRADIATED_RESIN_STAIRS,
        AlienResinBlocks.IRRADIATED_RESIN_BRICKS,
        AlienResinBlocks.IRRADIATED_RESIN_BRICK_SLAB,
        AlienResinBlocks.IRRADIATED_RESIN_BRICK_STAIRS,
        AlienResinBlocks.IRRADIATED_RESIN_BRICK_WALL,
        AlienResinBlocks.SMOOTH_IRRADIATED_RESIN,
        AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_SLAB,
        AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_STAIRS,
        AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_WALL,
        AlienResinBlocks.IRRADIATED_RESIN_VEIN,
        AlienResinBlocks.IRRADIATED_RESIN_WEB
    );

    private static final ResinSet NETHER_SET = new ResinSet(
        AlienItems.NETHER_RESIN_BALL,
        AlienResinBlocks.NETHER_RESIN,
        AlienResinBlocks.NETHER_RESIN_SLAB,
        AlienResinBlocks.NETHER_RESIN_STAIRS,
        AlienResinBlocks.NETHER_RESIN_BRICKS,
        AlienResinBlocks.NETHER_RESIN_BRICK_SLAB,
        AlienResinBlocks.NETHER_RESIN_BRICK_STAIRS,
        AlienResinBlocks.NETHER_RESIN_BRICK_WALL,
        AlienResinBlocks.SMOOTH_NETHER_RESIN,
        AlienResinBlocks.SMOOTH_NETHER_RESIN_SLAB,
        AlienResinBlocks.SMOOTH_NETHER_RESIN_STAIRS,
        AlienResinBlocks.SMOOTH_NETHER_RESIN_WALL,
        AlienResinBlocks.NETHER_RESIN_VEIN,
        AlienResinBlocks.NETHER_RESIN_WEB
    );

    private static final ResinSet NORMAL_SET = new ResinSet(
        AlienItems.RESIN_BALL,
        AlienResinBlocks.RESIN,
        AlienResinBlocks.RESIN_SLAB,
        AlienResinBlocks.RESIN_STAIRS,
        AlienResinBlocks.RESIN_BRICKS,
        AlienResinBlocks.RESIN_BRICK_SLAB,
        AlienResinBlocks.RESIN_BRICK_STAIRS,
        AlienResinBlocks.RESIN_BRICK_WALL,
        AlienResinBlocks.SMOOTH_RESIN,
        AlienResinBlocks.SMOOTH_RESIN_SLAB,
        AlienResinBlocks.SMOOTH_RESIN_STAIRS,
        AlienResinBlocks.SMOOTH_RESIN_WALL,
        AlienResinBlocks.RESIN_VEIN,
        AlienResinBlocks.RESIN_WEB
    );

    public static void provide(RecipeBuilder builder) {
        createResinRecipes(builder);
    }

    private static void createResinRecipes(RecipeBuilder builder) {
        createResinRecipesFromSet(builder, ABERRANT_SET);
        createResinRecipesFromSet(builder, IRRADIATED_SET);
        createResinRecipesFromSet(builder, NETHER_SET);
        createResinRecipesFromSet(builder, NORMAL_SET);
    }

    private static void createResinRecipesFromSet(RecipeBuilder builder, ResinSet set) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PLUS_CROSS.apply(set.resinBallItem.get()))
            .into(5, set.vein);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.X_CROSS.apply(set.resinBallItem.get()))
            .into(1, set.web);

        // Resin block
        RecipeUtil.createCompressedBlockRecipes2x2(builder, set.resinBallItem.get(), set.resinBlock.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.resinBlock.get(), set.resinBlockSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.resinBlock.get(), set.resinBlockStairs.get());

        builder.stonecut(set.resinBlock)
            .into(1, set.brick);

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.resinBlock.get(), set.brickSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.resinBlock.get(), set.brickStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.resinBlock.get(), set.brickWall.get());

        builder.stonecut(set.resinBlock)
            .into(1, set.smooth);

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.resinBlock.get(), set.smoothSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.resinBlock.get(), set.smoothStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.resinBlock.get(), set.smoothWall.get());

        // Brick resin block

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.brick.get(), set.brickSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.brick.get(), set.brickStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.brick.get(), set.brickWall.get());

        // Smooth resin block

        builder.stonecut(set.smooth)
            .into(1, set.brick);

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.smooth.get(), set.smoothSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.smooth.get(), set.smoothStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.smooth.get(), set.smoothWall.get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.smooth.get(), set.brickSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.smooth.get(), set.brickStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.smooth.get(), set.brickWall.get());
    }

    private record ResinSet(
        AVPDeferredHolder<Item> resinBallItem,
        AVPDeferredHolder<Block> resinBlock,
        AVPDeferredHolder<Block> resinBlockSlab,
        AVPDeferredHolder<Block> resinBlockStairs,
        AVPDeferredHolder<Block> brick,
        AVPDeferredHolder<Block> brickSlab,
        AVPDeferredHolder<Block> brickStairs,
        AVPDeferredHolder<Block> brickWall,
        AVPDeferredHolder<Block> smooth,
        AVPDeferredHolder<Block> smoothSlab,
        AVPDeferredHolder<Block> smoothStairs,
        AVPDeferredHolder<Block> smoothWall,
        AVPDeferredHolder<ResinVeinBlock> vein,
        AVPDeferredHolder<Block> web
    ) {}
}
