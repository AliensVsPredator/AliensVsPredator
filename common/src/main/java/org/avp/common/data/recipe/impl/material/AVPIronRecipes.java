package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

import org.avp.common.registry.block.AVPBlockRegistry;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.registry.holder.AVPHolder;

public final class AVPIronRecipes {

    public static void addIronRecipes(RecipeOutput recipeOutput) {
        // TODO: This is incredibly hacky, need to fix.
        var ironHolder = new AVPHolder<>("null", () -> Blocks.IRON_BLOCK);
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, ironHolder, AVPBlockRegistry.INSTANCE.iron);
    }

    private AVPIronRecipes() {
        throw new UnsupportedOperationException();
    }
}
