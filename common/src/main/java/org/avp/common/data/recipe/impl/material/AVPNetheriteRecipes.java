package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;

import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.registry.block.AVPBlockRegistry;
import org.avp.common.registry.holder.AVPHolder;

public final class AVPNetheriteRecipes {

    public static void addNetheriteRecipes(RecipeOutput recipeOutput) {
        // TODO: This is incredibly hacky, need to fix.
        var netheriteHolder = new AVPHolder<>("null", () -> Blocks.NETHERITE_BLOCK);
        AVPMetalRecipeHelper.addMetalSetRecipes(recipeOutput, netheriteHolder, AVPBlockRegistry.INSTANCE.netherite);
    }

    private AVPNetheriteRecipes() {
        throw new UnsupportedOperationException();
    }
}
