package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.block.AVPTempleBlocks;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.RecipeUtils;

public final class AVPTempleBlockRecipes {

    public static void addTempleBlockRecipes(RecipeOutput recipeOutput) {
        addTempleStoneRecipes(recipeOutput);
        addTempleBrickSingleRecipes(recipeOutput);
        addTempleBrickRecipes(recipeOutput);
        addTempleTileRecipes(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.FLOOR, AVPTempleBlocks.INSTANCE.WALL_BASE);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPTempleBlocks.INSTANCE.SKULLS)
            .defineAndUnlockIfHas('A', Items.SKELETON_SKULL)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);
    }

    private static void addTempleBrickSingleRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.FLOOR, AVPTempleBlocks.INSTANCE.BRICK_SINGLE);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.BRICK_SINGLE, AVPTempleBlocks.INSTANCE.BRICK_SINGLE_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.BRICK_SINGLE, AVPTempleBlocks.INSTANCE.BRICK_SINGLE_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.BRICK_SINGLE, AVPTempleBlocks.INSTANCE.BRICK_SINGLE_WALL);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.BRICK_SINGLE, AVPTempleBlocks.INSTANCE.WALL_BASE);
    }

    private static void addTempleBrickRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.FLOOR, AVPTempleBlocks.INSTANCE.BRICK);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.BRICK, AVPTempleBlocks.INSTANCE.BRICK_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.BRICK, AVPTempleBlocks.INSTANCE.BRICK_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.BRICK, AVPTempleBlocks.INSTANCE.BRICK_WALL);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.BRICK, AVPTempleBlocks.INSTANCE.WALL_BASE);
    }

    private static void addTempleTileRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.FLOOR, AVPTempleBlocks.INSTANCE.TILE);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.TILE, AVPTempleBlocks.INSTANCE.TILE_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.TILE, AVPTempleBlocks.INSTANCE.TILE_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.TILE, AVPTempleBlocks.INSTANCE.TILE_WALL);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.TILE, AVPTempleBlocks.INSTANCE.WALL_BASE);
    }

    private static void addTempleStoneRecipes(RecipeOutput recipeOutput) {
        var templeStoneOutput = AVPTempleBlocks.INSTANCE.FLOOR.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.DEEPSLATE), RecipeCategory.MISC, templeStoneOutput, 0.0F, 100)
            .unlockedBy("has_deepslate", AVPRecipeProvider.has(Items.DEEPSLATE))
            .save(recipeOutput, AVPConstants.MOD_ID + ":temple_stone_from_blasting_deepslate");

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.FLOOR, AVPTempleBlocks.INSTANCE.FLOOR_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.FLOOR, AVPTempleBlocks.INSTANCE.FLOOR_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.FLOOR, AVPTempleBlocks.INSTANCE.FLOOR_WALL);
    }

    private AVPTempleBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
