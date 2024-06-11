package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.registry.block.AVPAlienBlockRegistry;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPAlienBlockRecipes {

    public static void addAlienBlockRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPAlienBlockRegistry.INSTANCE.resin, 3)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.resinBall)
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPAlienBlockRegistry.INSTANCE.resinVeins, 3)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.resinBall)
            .pattern(" A ")
            .pattern("AAA")
            .pattern(" A ")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPAlienBlockRegistry.INSTANCE.resinWebbing, 3)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.resinBall)
            .pattern("A A")
            .pattern(" A ")
            .pattern("A A")
            .save(recipeOutput);
    }

    private AVPAlienBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
