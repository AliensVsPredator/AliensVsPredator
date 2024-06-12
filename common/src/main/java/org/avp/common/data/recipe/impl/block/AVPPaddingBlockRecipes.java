package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.registry.block.AVPPaddingBlockRegistry;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;

public final class AVPPaddingBlockRecipes {

    public static void addPaddingBlockRecipes(RecipeOutput recipeOutput) {
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingBlack, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingBlue, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingBrown, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingCyan, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingGray, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingGreen, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingLightBlue, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingLightGray, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingLime, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingMagenta, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingOrange, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingPink, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingPurple, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingRed, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingWhite, recipeOutput);
        addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.INSTANCE.paddingYellow, recipeOutput);
    }

    private static void addPaddingBlockRecipeSet(AVPPaddingBlockRegistry.PaddingBlockSet paddingBlockSet, RecipeOutput recipeOutput) {
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

        AVPMetalRecipeHelper.addStandardCutterRecipes(recipeOutput, BLHolder.empty(), paddingBlockSet.square());

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, paddingBlockSet.tiles().base(), 4)
            .defineAndUnlockIfHas('A', paddingBlockSet.square().base())
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        AVPMetalRecipeHelper.addStandardCutterRecipes(recipeOutput, BLHolder.empty(), paddingBlockSet.tiles());
    }

    private AVPPaddingBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
