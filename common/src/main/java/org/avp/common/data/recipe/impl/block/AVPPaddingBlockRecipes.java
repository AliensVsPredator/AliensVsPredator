package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import org.avp.common.block.AVPPaddingBlocks;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;

public final class AVPPaddingBlockRecipes {

    public static void addPaddingBlockRecipes(RecipeOutput recipeOutput) {
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingOrange, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingWhite, recipeOutput);
    }

    private static void addPaddingBlockRecipeSet(AVPPaddingBlocks.PaddingBlockSet paddingBlockSet, RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.panel())
            .defineAndUnlockIfHas('A', Items.LEATHER)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .defineAndUnlockIfHas('C', paddingBlockSet.dyeItem())
            .pattern(" AC")
            .pattern("ABA")
            .pattern(" A ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.pipes(), 2)
            .defineAndUnlockIfHas('A', paddingBlockSet.panel())
            .defineAndUnlockIfHas('B', Items.IRON_NUGGET)
            .pattern("BA")
            .pattern("AB")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.square(), 4)
            .defineAndUnlockIfHas('A', paddingBlockSet.panel())
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.tiles(), 4)
            .defineAndUnlockIfHas('A', paddingBlockSet.square())
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);
    }

    private AVPPaddingBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
