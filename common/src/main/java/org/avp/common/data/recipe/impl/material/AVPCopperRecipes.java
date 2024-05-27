package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;
import org.avp.api.Holder;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;

public final class AVPCopperRecipes {

    public static void addCopperRecipes(RecipeOutput recipeOutput) {
        // TODO: This is incredibly hacky, need to fix.
        var copperHolder = new Holder<>("null", () -> Blocks.COPPER_BLOCK);
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, copperHolder, AVPBlocks.INSTANCE.copper);
    }

    private AVPCopperRecipes() {
        throw new UnsupportedOperationException();
    }
}
