package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

import org.avp.api.Holder;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;

public final class AVPGoldRecipes {

    public static void addGoldRecipes(RecipeOutput recipeOutput) {
        // TODO: This is incredibly hacky, need to fix.
        var goldHolder = new Holder<>("null", () -> Blocks.GOLD_BLOCK);
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, goldHolder, AVPBlocks.INSTANCE.gold);
    }

    private AVPGoldRecipes() {
        throw new UnsupportedOperationException();
    }
}
