package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.avp.api.Holder;
import org.avp.common.block.AVPPlasticBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.item.AVPItems;

import java.util.List;

public final class AVPPlasticBlockRecipes {

    public static void addPlasticBlockRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPPlasticBlocks.INSTANCE.plasticGreen.base().base())
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.polymer)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        var plasticList = List.of(
            AVPPlasticBlocks.INSTANCE.plasticBlack,
            AVPPlasticBlocks.INSTANCE.plasticBlue,
            AVPPlasticBlocks.INSTANCE.plasticBrown,
            AVPPlasticBlocks.INSTANCE.plasticCyan,
            AVPPlasticBlocks.INSTANCE.plasticGreen,
            AVPPlasticBlocks.INSTANCE.plasticLightBlue,
            AVPPlasticBlocks.INSTANCE.plasticLightGray,
            AVPPlasticBlocks.INSTANCE.plasticLime,
            AVPPlasticBlocks.INSTANCE.plasticMagenta,
            AVPPlasticBlocks.INSTANCE.plasticOrange,
            AVPPlasticBlocks.INSTANCE.plasticPink,
            AVPPlasticBlocks.INSTANCE.plasticPurple,
            AVPPlasticBlocks.INSTANCE.plasticRed,
            AVPPlasticBlocks.INSTANCE.plasticWhite,
            AVPPlasticBlocks.INSTANCE.plasticYellow
        );

        for (int i = 0; i < plasticList.size(); i++) {
            var dyeItem = plasticList.get(i).dyeItem();
            var baseBlock = plasticList.get(i).base().base();
            AVPShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, baseBlock)
                .requiresAndUnlockIfHas('A', dyeItem)
                .requiresAndUnlockIfHas(
                    'B',
                    Ingredient.of(plasticList.stream()
                        .filter(plasticBlockSet -> !plasticBlockSet.base().base().equals(baseBlock))
                        .map(plasticBlockSet -> new ItemStack(plasticBlockSet.base().base().get()))))
                .save(recipeOutput, "dye_" + AVPRecipeHelper.getItemName(baseBlock.get()));
        }

        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticBlack, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticBlue, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticBrown, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticCyan, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticGray, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticGreen, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticLightBlue, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticLightGray, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticLime, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticMagenta, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticOrange, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticPink, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticPurple, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticRed, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticWhite, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlocks.INSTANCE.plasticYellow, recipeOutput);
    }

    private static void addPlasticBlockRecipeSet(AVPPlasticBlocks.PlasticBlockSet plasticBlockSet, RecipeOutput recipeOutput) {
        AVPMetalRecipeHelper.addStandardCutterRecipes(recipeOutput, Holder.empty(), plasticBlockSet.base());
    }

    private AVPPlasticBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
