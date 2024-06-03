package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import org.avp.common.block.AVPPaddingBlocks;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;

public final class AVPPaddingBlockRecipes {

    public static void addPaddingBlockRecipes(RecipeOutput recipeOutput) {
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingBlack, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingBlue, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingBrown, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingCyan, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingGray, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingGreen, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingLightBlue, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingLightGray, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingLime, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingMagenta, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingOrange, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingPink, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingPurple, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingRed, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingWhite, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlocks.INSTANCE.paddingYellow, recipeOutput);
    }

    private static void addPaddingBlockRecipeSet(AVPPaddingBlocks.PaddingBlockSet paddingBlockSet, RecipeOutput recipeOutput) {
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.panel())
            .requiresAndUnlockIfHas('A', Items.LEATHER)
            .requiresAndUnlockIfHas('B', ItemTags.WOOL)
            .requiresAndUnlockIfHas('C', paddingBlockSet.dyeItem())
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.pipes(), 2)
            .defineAndUnlockIfHas('A', paddingBlockSet.panel())
            .defineAndUnlockIfHas('B', Items.IRON_NUGGET)
            .pattern("BA")
            .pattern("AB")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.square().base(), 4)
            .defineAndUnlockIfHas('A', paddingBlockSet.panel())
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.tiles().base(), 4)
            .defineAndUnlockIfHas('A', paddingBlockSet.square().base())
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);
    }

    private AVPPaddingBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
