package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.avp.common.AVPConstants;
import org.avp.common.block.AVPAlienBlocks;
import org.avp.common.block.AVPIndustrialBlocks;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.RecipeUtils;
import org.avp.common.item.AVPItems;

public final class AVPAlienBlockRecipes {

    public static void addAlienBlockRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPAlienBlocks.INSTANCE.resin, 3)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.resinBall)
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPAlienBlocks.INSTANCE.resinVeins, 3)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.resinBall)
            .pattern(" A ")
            .pattern("AAA")
            .pattern(" A ")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPAlienBlocks.INSTANCE.resinWebbing, 3)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.resinBall)
            .pattern("A A")
            .pattern(" A ")
            .pattern("A A")
            .save(recipeOutput);
    }

    private AVPAlienBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
