package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import org.avp.common.block.AVPIndustrialBlocks;
import org.avp.common.item.AVPItems;

/**
 * @author Boston Vanseghi
 */
public final class AVPIndustrialBlockRecipes {

    public static void addIndustrialBlockRecipes(RecipeOutput recipeOutput) {
        addBrickRecipes(recipeOutput);
        addMetalPanel0Recipes(recipeOutput);
        addMetalPanel1Recipes(recipeOutput);
        addMetalPanel2Recipes(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.LAMP.get())
            .define('A', AVPItems.INGOT_ALUMINUM.get())
            .define('B', Items.REDSTONE_LAMP)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .unlockedBy("has_aluminum_ingot", AVPRecipeProvider.has(AVPItems.INGOT_ALUMINUM.get()))
            .save(recipeOutput);
    }

    private static void addBrickRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.BRICK.get())
            .define('A', AVPItems.INGOT_ALUMINUM.get())
            .pattern("AA")
            .pattern("AA")
            .unlockedBy("has_aluminum_ingot", AVPRecipeProvider.has(AVPItems.INGOT_ALUMINUM.get()))
            .save(recipeOutput);

        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.BRICK.get(),
            AVPIndustrialBlocks.BRICK_SLAB.get(),
            8
        );
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.BRICK.get(),
            AVPIndustrialBlocks.BRICK_STAIRS.get(),
            4
        );
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.BRICK.get(),
            AVPIndustrialBlocks.FLOOR_GRILL.get(),
            4
        );
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.BRICK.get(),
            AVPIndustrialBlocks.VENT.get()
        );
    }

    private static void addMetalPanel0Recipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.METAL_PANEL_0.get())
            .define('A', AVPItems.INGOT_ALUMINUM.get())
            .define('B', Items.IRON_INGOT)
            .pattern("BA")
            .pattern("BA")
            .unlockedBy("has_aluminum_ingot", AVPRecipeProvider.has(AVPItems.INGOT_ALUMINUM.get()))
            .save(recipeOutput);

        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.METAL_PANEL_0.get(),
            AVPIndustrialBlocks.METAL_PANEL_0_SLAB.get(),
            8
        );
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.METAL_PANEL_0.get(),
            AVPIndustrialBlocks.METAL_PANEL_0_STAIRS.get(),
            4
        );
    }

    private static void addMetalPanel1Recipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.METAL_PANEL_1.get())
            .define('A', AVPItems.INGOT_ALUMINUM.get())
            .define('B', Items.IRON_INGOT)
            .pattern("AA")
            .pattern("BB")
            .unlockedBy("has_aluminum_ingot", AVPRecipeProvider.has(AVPItems.INGOT_ALUMINUM.get()))
            .save(recipeOutput);

        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.METAL_PANEL_1.get(),
            AVPIndustrialBlocks.METAL_PANEL_1_SLAB.get(),
            8
        );
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.METAL_PANEL_1.get(),
            AVPIndustrialBlocks.METAL_PANEL_1_STAIRS.get(),
            4
        );
    }

    private static void addMetalPanel2Recipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.METAL_PANEL_2.get())
            .define('A', AVPItems.INGOT_ALUMINUM.get())
            .define('B', Items.IRON_INGOT)
            .pattern("BA")
            .pattern("AB")
            .unlockedBy("has_aluminum_ingot", AVPRecipeProvider.has(AVPItems.INGOT_ALUMINUM.get()))
            .save(recipeOutput);

        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.METAL_PANEL_2.get(),
            AVPIndustrialBlocks.METAL_PANEL_2_SLAB.get(),
            8
        );
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPIndustrialBlocks.METAL_PANEL_2.get(),
            AVPIndustrialBlocks.METAL_PANEL_2_STAIRS.get(),
            4
        );
    }

    private AVPIndustrialBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
