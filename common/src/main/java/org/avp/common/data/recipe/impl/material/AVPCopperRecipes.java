package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;

public final class AVPCopperRecipes {

    public static void addCopperRecipes(RecipeOutput recipeOutput) {
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, Blocks.COPPER_BLOCK, AVPBlocks.INSTANCE.copper);
    }

    private AVPCopperRecipes() {
        throw new UnsupportedOperationException();
    }
}
