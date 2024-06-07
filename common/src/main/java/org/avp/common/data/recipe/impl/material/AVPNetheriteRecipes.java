package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

import org.avp.api.Holder;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;

public final class AVPNetheriteRecipes {

    public static void addNetheriteRecipes(RecipeOutput recipeOutput) {
        // TODO: This is incredibly hacky, need to fix.
        var netheriteHolder = new Holder<>("null", () -> Blocks.NETHERITE_BLOCK);
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, netheriteHolder, AVPBlocks.INSTANCE.netherite);
    }

    private AVPNetheriteRecipes() {
        throw new UnsupportedOperationException();
    }
}
