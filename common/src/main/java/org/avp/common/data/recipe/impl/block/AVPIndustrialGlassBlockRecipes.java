package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.avp.common.AVPConstants;
import org.avp.common.block.AVPIndustrialGlassBlocks;
import org.avp.common.data.recipe.AVPRecipeProvider;

public final class AVPIndustrialGlassBlockRecipes {

    public static void addIndustrialGlassBlockRecipes(RecipeOutput recipeOutput) {
        var industrialGlassOutput = AVPIndustrialGlassBlocks.INSTANCE.glass.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.GLASS), RecipeCategory.MISC, industrialGlassOutput, 0.7F, 100)
            .unlockedBy("has_glass", AVPRecipeProvider.has(Items.GLASS))
            .save(recipeOutput, AVPConstants.MOD_ID + ":industrial_glass_from_blasting_glass");
    }

    private AVPIndustrialGlassBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
