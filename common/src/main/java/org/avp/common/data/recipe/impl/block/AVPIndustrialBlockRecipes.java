package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.RecipeUtils;
import org.avp.common.data.tag.AVPItemTags;
import org.avp.common.registry.block.AVPIndustrialBlockRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPIndustrialBlockRecipes {

    public static void addIndustrialBlockRecipes(RecipeOutput recipeOutput) {
        addBrickRecipes(recipeOutput);
        addMetalPanel0Recipes(recipeOutput);
        addMetalPanel1Recipes(recipeOutput);
        addMetalPanel2Recipes(recipeOutput);
        addIndustrialWallRecipes(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlockRegistry.INSTANCE.lamp)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', Items.REDSTONE_LAMP)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .save(recipeOutput);
    }

    private static void addBrickRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlockRegistry.INSTANCE.brick.base())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.brick.base(),
            AVPIndustrialBlockRegistry.INSTANCE.brick.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.brick.base(),
            AVPIndustrialBlockRegistry.INSTANCE.brick.stairs()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.brick.base(),
            AVPIndustrialBlockRegistry.INSTANCE.floorGrill,
            4
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.brick.base(),
            AVPIndustrialBlockRegistry.INSTANCE.vent
        );
    }

    private static void addMetalPanel0Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlockRegistry.INSTANCE.metalPanel0.base())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("BA")
            .pattern("BA")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel0.base(),
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel0.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel0.base(),
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel0.stairs()
        );
    }

    private static void addMetalPanel1Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlockRegistry.INSTANCE.metalPanel1.base())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("AA")
            .pattern("BB")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel1.base(),
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel1.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel1.base(),
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel1.stairs()
        );
    }

    private static void addMetalPanel2Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlockRegistry.INSTANCE.metalPanel2.base())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("BA")
            .pattern("AB")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel2.base(),
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel2.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel2.base(),
            AVPIndustrialBlockRegistry.INSTANCE.metalPanel2.stairs()
        );
    }

    private static void addIndustrialWallRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlockRegistry.INSTANCE.wall.base(), 8)
            .defineAndUnlockIfHas('A', AVPItemTags.CONCRETE)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.ingotSteel)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);
        AVPMetalRecipeHelper.addStandardCutterRecipes(recipeOutput, BLHolder.empty(), AVPIndustrialBlockRegistry.INSTANCE.wall);
    }

    private AVPIndustrialBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
