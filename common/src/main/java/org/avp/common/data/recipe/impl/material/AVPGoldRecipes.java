package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.registry.block.AVPBlockRegistry;
import org.avp.common.registry.holder.AVPHolder;

public final class AVPGoldRecipes {

    public static void addGoldRecipes(RecipeOutput recipeOutput) {
        // TODO: This is incredibly hacky, need to fix.
        var goldHolder = new AVPHolder<>("null", () -> Blocks.GOLD_BLOCK);
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, goldHolder, AVPBlockRegistry.INSTANCE.gold);
    }

    private AVPGoldRecipes() {
        throw new UnsupportedOperationException();
    }
}
