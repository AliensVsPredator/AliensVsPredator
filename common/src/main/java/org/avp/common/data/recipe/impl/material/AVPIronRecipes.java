package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

import org.avp.api.Holder;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;

public final class AVPIronRecipes {

    public static void addIronRecipes(RecipeOutput recipeOutput) {
        // TODO: This is incredibly hacky, need to fix.
        var ironHolder = new Holder<>("null", () -> Blocks.IRON_BLOCK);
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, ironHolder, AVPBlocks.INSTANCE.iron);
    }

    private AVPIronRecipes() {
        throw new UnsupportedOperationException();
    }
}
