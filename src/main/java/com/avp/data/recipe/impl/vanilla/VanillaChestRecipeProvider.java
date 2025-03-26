package com.avp.data.recipe.impl.vanilla;

import com.avp.common.block_item.AVPBlockItems;
import com.avp.common.item.AVPItems;
import com.avp.data.recipe.builder.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;

public class VanillaChestRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createChestRecipes(builder);
    }

    private static void createChestRecipes(RecipeBuilder builder) {
        builder.shaped()
                .withCategory(RecipeCategory.DECORATIONS)
                .define('#', AVPItems.LEAD_INGOT)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .into(1, AVPBlockItems.LEAD_CHEST);
    }
}
