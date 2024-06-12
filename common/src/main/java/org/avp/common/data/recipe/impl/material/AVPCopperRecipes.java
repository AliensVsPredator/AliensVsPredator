package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.registry.block.AVPBlockRegistry;
import org.avp.common.registry.holder.AVPHolder;

public final class AVPCopperRecipes {

    public static void addCopperRecipes(RecipeOutput recipeOutput) {
        // TODO: This is incredibly hacky, need to fix.
        var copperHolder = new AVPHolder<>("null", () -> Blocks.COPPER_BLOCK);
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, copperHolder, AVPBlockRegistry.INSTANCE.copper);
    }

    private AVPCopperRecipes() {
        throw new UnsupportedOperationException();
    }
}
