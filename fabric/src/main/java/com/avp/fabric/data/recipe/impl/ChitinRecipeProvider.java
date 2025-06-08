package com.avp.fabric.data.recipe.impl;

import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.block.AlienChitinBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class ChitinRecipeProvider {

    private static final ChitinSet ABERRANT_SET = new ChitinSet(
        AlienItems.ABERRANT_CHITIN,
        AlienItems.PLATED_ABERRANT_CHITIN,
        AlienChitinBlocks.ABERRANT_CHITIN_BLOCK,
        AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_SLAB,
        AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_STAIRS,
        AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_WALL,
        AlienChitinBlocks.ABERRANT_CHITIN_BRICKS,
        AlienChitinBlocks.ABERRANT_CHITIN_BRICK_SLAB,
        AlienChitinBlocks.ABERRANT_CHITIN_BRICK_STAIRS,
        AlienChitinBlocks.ABERRANT_CHITIN_BRICK_WALL,
        AlienChitinBlocks.CHISELED_ABERRANT_CHITIN_BRICKS,
        AlienChitinBlocks.CHISELED_ABERRANT_CHITIN_BRICKS_EMBRYO,
        AlienChitinBlocks.POLISHED_ABERRANT_CHITIN,
        AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_SLAB,
        AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_STAIRS,
        AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_WALL
    );

    private static final ChitinSet NETHER_SET = new ChitinSet(
        AlienItems.NETHER_CHITIN,
        AlienItems.PLATED_NETHER_CHITIN,
        AlienChitinBlocks.NETHER_CHITIN_BLOCK,
        AlienChitinBlocks.NETHER_CHITIN_BLOCK_SLAB,
        AlienChitinBlocks.NETHER_CHITIN_BLOCK_STAIRS,
        AlienChitinBlocks.NETHER_CHITIN_BLOCK_WALL,
        AlienChitinBlocks.NETHER_CHITIN_BRICKS,
        AlienChitinBlocks.NETHER_CHITIN_BRICK_SLAB,
        AlienChitinBlocks.NETHER_CHITIN_BRICK_STAIRS,
        AlienChitinBlocks.NETHER_CHITIN_BRICK_WALL,
        AlienChitinBlocks.CHISELED_NETHER_CHITIN_BRICKS,
        AlienChitinBlocks.CHISELED_NETHER_CHITIN_BRICKS_EMBRYO,
        AlienChitinBlocks.POLISHED_NETHER_CHITIN,
        AlienChitinBlocks.POLISHED_NETHER_CHITIN_SLAB,
        AlienChitinBlocks.POLISHED_NETHER_CHITIN_STAIRS,
        AlienChitinBlocks.POLISHED_NETHER_CHITIN_WALL
    );

    private static final ChitinSet NORMAL_SET = new ChitinSet(
        AlienItems.CHITIN,
        AlienItems.PLATED_CHITIN,
        AlienChitinBlocks.CHITIN_BLOCK,
        AlienChitinBlocks.CHITIN_BLOCK_SLAB,
        AlienChitinBlocks.CHITIN_BLOCK_STAIRS,
        AlienChitinBlocks.CHITIN_BLOCK_WALL,
        AlienChitinBlocks.CHITIN_BRICKS,
        AlienChitinBlocks.CHITIN_BRICK_SLAB,
        AlienChitinBlocks.CHITIN_BRICK_STAIRS,
        AlienChitinBlocks.CHITIN_BRICK_WALL,
        AlienChitinBlocks.CHISELED_CHITIN_BRICKS,
        AlienChitinBlocks.CHISELED_CHITIN_BRICKS_EMBRYO,
        AlienChitinBlocks.POLISHED_CHITIN,
        AlienChitinBlocks.POLISHED_CHITIN_SLAB,
        AlienChitinBlocks.POLISHED_CHITIN_STAIRS,
        AlienChitinBlocks.POLISHED_CHITIN_WALL
    );

    public static void provide(RecipeBuilder builder) {
        createChitinRecipes(builder);
    }

    private static void createChitinRecipes(RecipeBuilder builder) {
        createChitinRecipesFromSet(builder, ABERRANT_SET);
        createChitinRecipesFromSet(builder, NETHER_SET);
        createChitinRecipesFromSet(builder, NORMAL_SET);
    }

    private static void createChitinRecipesFromSet(RecipeBuilder builder, ChitinSet set) {
        builder.shapeless()
            .requires(1, set.platedChitinItem)
            .into(3, set.chitinItem);

        RecipeUtil.createCompressedBlockRecipes2x2(builder, set.chitinItem.get(), set.chitinBlock.get());

        // Base chitin block

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.chitinBlockSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.chitinBlockStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.chitinBlockWall.get());

        builder.stonecut(set.chitinBlock)
            .into(1, set.polished);

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.polishedSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.polishedStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.polishedWall.get());

        builder.stonecut(set.chitinBlock)
            .into(1, set.bricks);

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.brickSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.brickStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.chitinBlock.get(), set.brickWall.get());

        builder.stonecut(set.chitinBlock)
            .into(1, set.chiseledBricks);
        builder.stonecut(set.chitinBlock)
            .into(1, set.chiseledBricksEmbryo);

        // Brick chitin block

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.bricks.get(), set.brickSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.bricks.get(), set.brickStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.bricks.get(), set.brickWall.get());

        builder.stonecut(set.bricks)
            .into(1, set.chiseledBricks);
        builder.stonecut(set.bricks)
            .into(1, set.chiseledBricksEmbryo);

        // Polished chitin block

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.polished.get(), set.polishedSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.polished.get(), set.polishedStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.polished.get(), set.polishedWall.get());

        builder.stonecut(set.polished)
            .into(1, set.bricks);

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.polished.get(), set.brickSlab.get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.polished.get(), set.brickStairs.get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.polished.get(), set.brickWall.get());

        builder.stonecut(set.polished)
            .into(1, set.chiseledBricks);
        builder.stonecut(set.polished)
            .into(1, set.chiseledBricksEmbryo);
    }

    private record ChitinSet(
        AVPDeferredHolder<Item> chitinItem,
        AVPDeferredHolder<Item> platedChitinItem,
        AVPDeferredHolder<Block> chitinBlock,
        AVPDeferredHolder<Block> chitinBlockSlab,
        AVPDeferredHolder<Block> chitinBlockStairs,
        AVPDeferredHolder<Block> chitinBlockWall,
        AVPDeferredHolder<Block> bricks,
        AVPDeferredHolder<Block> brickSlab,
        AVPDeferredHolder<Block> brickStairs,
        AVPDeferredHolder<Block> brickWall,
        AVPDeferredHolder<Block> chiseledBricks,
        AVPDeferredHolder<Block> chiseledBricksEmbryo,
        AVPDeferredHolder<Block> polished,
        AVPDeferredHolder<Block> polishedSlab,
        AVPDeferredHolder<Block> polishedStairs,
        AVPDeferredHolder<Block> polishedWall
    ) {}
}
