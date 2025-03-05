package com.avp.fabric.data.recipe.impl.vanilla;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.block.BlockProperties;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class VanillaConcreteRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createConcreteBlockRecipes(builder);
    }

    private static void createConcreteBlockRecipes(RecipeBuilder builder) {
        BlockProperties.DYE_COLOR_TO_CONCRETE_BLOCKS.forEach(((dyeColor, block) -> {
            var slabBlock = AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor);
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, () -> block, slabBlock);

            var stairBlock = AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, () -> block, stairBlock);
        }));
    }
}
