package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.block.AVPIndustrialBlocks;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.RecipeUtils;
import org.avp.common.item.AVPItems;

public final class AVPIndustrialBlockRecipes {

    public static void addIndustrialBlockRecipes(RecipeOutput recipeOutput) {
        addBrickRecipes(recipeOutput);
        addMetalPanel0Recipes(recipeOutput);
        addMetalPanel1Recipes(recipeOutput);
        addMetalPanel2Recipes(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.INSTANCE.lamp)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', Items.REDSTONE_LAMP)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .save(recipeOutput);
    }

    private static void addBrickRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.INSTANCE.brick.base())
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.INSTANCE.brick.base(), AVPIndustrialBlocks.INSTANCE.brick.slab());
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.INSTANCE.brick.base(), AVPIndustrialBlocks.INSTANCE.brick.stairs());
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.INSTANCE.brick.base(), AVPIndustrialBlocks.INSTANCE.floorGrill, 4);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.INSTANCE.brick.base(), AVPIndustrialBlocks.INSTANCE.vent);
    }

    private static void addMetalPanel0Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.INSTANCE.metalPanel0.base())
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("BA")
            .pattern("BA")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlocks.INSTANCE.metalPanel0.base(),
            AVPIndustrialBlocks.INSTANCE.metalPanel0.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlocks.INSTANCE.metalPanel0.base(),
            AVPIndustrialBlocks.INSTANCE.metalPanel0.stairs()
        );
    }

    private static void addMetalPanel1Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.INSTANCE.metalPanel1.base())
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("AA")
            .pattern("BB")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlocks.INSTANCE.metalPanel1.base(),
            AVPIndustrialBlocks.INSTANCE.metalPanel1.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlocks.INSTANCE.metalPanel1.base(),
            AVPIndustrialBlocks.INSTANCE.metalPanel1.stairs()
        );
    }

    private static void addMetalPanel2Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.INSTANCE.metalPanel2.base())
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("BA")
            .pattern("AB")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlocks.INSTANCE.metalPanel2.base(),
            AVPIndustrialBlocks.INSTANCE.metalPanel2.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlocks.INSTANCE.metalPanel2.base(),
            AVPIndustrialBlocks.INSTANCE.metalPanel2.stairs()
        );
    }

    private AVPIndustrialBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
