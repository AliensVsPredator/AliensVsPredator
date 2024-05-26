package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import org.avp.api.Holder;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;

public final class AVPFerroaluminumRecipes {

    public static void addFerroaluminumRecipes(RecipeOutput recipeOutput) {
        AVPMetalRecipeHelper.addFullMetalSetRecipes(recipeOutput, Holder.empty(), AVPBlocks.INSTANCE.ferroaluminum);
    }

    private AVPFerroaluminumRecipes() {
        throw new UnsupportedOperationException();
    }
}
