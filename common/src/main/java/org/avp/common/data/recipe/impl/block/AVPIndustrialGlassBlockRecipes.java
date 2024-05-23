package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.block.AVPIndustrialGlassBlocks;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;

public final class AVPIndustrialGlassBlockRecipes {

    public static void addIndustrialGlassBlockRecipes(RecipeOutput recipeOutput) {
        // Industrial glass originates from blasting regular glass.
        var industrialGlassOutput = AVPIndustrialGlassBlocks.INSTANCE.glass.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.GLASS), RecipeCategory.MISC, industrialGlassOutput, 0.7F, 100)
            .unlockedBy("has_glass", AVPRecipeProvider.has(Items.GLASS))
            .save(recipeOutput, AVPConstants.MOD_ID + ":industrial_glass_from_blasting_glass");

        // Generate recipes for every colored industrial glass block.
        AVPIndustrialGlassBlocks.getColoredIndustrialGlassEntries().forEach(coloredIndustrialGlassHolder -> {
            var coloredIndustrialGlassName = coloredIndustrialGlassHolder.holder().getResourceLocation().getPath();
            var coloredIndustrialGlassBlock = coloredIndustrialGlassHolder.holder().get();

            // Industrial glass combined with dyes creates colored industrial glass.
            AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, coloredIndustrialGlassBlock, 8)
                .defineAndUnlockIfHas('A', industrialGlassOutput)
                .defineAndUnlockIfHas('B', DyeItem.byColor(coloredIndustrialGlassHolder.dyeColor()))
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .save(recipeOutput);

            // All colored industrial glass blocks can be blasted again to remove dyed colors.
            SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(coloredIndustrialGlassBlock),
                RecipeCategory.MISC,
                industrialGlassOutput,
                0,
                100
            )
                .unlockedBy("has_industrial_glass", AVPRecipeProvider.has(industrialGlassOutput))
                .save(recipeOutput, AVPConstants.MOD_ID + ":industrial_glass_from_blasting_" + coloredIndustrialGlassName);
        });
    }

    private AVPIndustrialGlassBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
