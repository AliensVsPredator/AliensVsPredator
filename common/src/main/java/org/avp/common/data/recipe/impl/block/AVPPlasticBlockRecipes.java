package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.registry.block.AVPPlasticBlockRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPPlasticBlockRecipes {

    public static void addPlasticBlockRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPPlasticBlockRegistry.INSTANCE.plasticGreen.base().base())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.polymer)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        var plasticList = List.of(
            AVPPlasticBlockRegistry.INSTANCE.plasticBlack,
            AVPPlasticBlockRegistry.INSTANCE.plasticBlue,
            AVPPlasticBlockRegistry.INSTANCE.plasticBrown,
            AVPPlasticBlockRegistry.INSTANCE.plasticCyan,
            AVPPlasticBlockRegistry.INSTANCE.plasticGreen,
            AVPPlasticBlockRegistry.INSTANCE.plasticLightBlue,
            AVPPlasticBlockRegistry.INSTANCE.plasticLightGray,
            AVPPlasticBlockRegistry.INSTANCE.plasticLime,
            AVPPlasticBlockRegistry.INSTANCE.plasticMagenta,
            AVPPlasticBlockRegistry.INSTANCE.plasticOrange,
            AVPPlasticBlockRegistry.INSTANCE.plasticPink,
            AVPPlasticBlockRegistry.INSTANCE.plasticPurple,
            AVPPlasticBlockRegistry.INSTANCE.plasticRed,
            AVPPlasticBlockRegistry.INSTANCE.plasticWhite,
            AVPPlasticBlockRegistry.INSTANCE.plasticYellow
        );

        for (int i = 0; i < plasticList.size(); i++) {
            var dyeItem = plasticList.get(i).dyeItem();
            var baseBlock = plasticList.get(i).base().base();
            AVPShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, baseBlock)
                .requiresAndUnlockIfHas('A', dyeItem)
                .requiresAndUnlockIfHas(
                    'B',
                    Ingredient.of(
                        plasticList.stream()
                            .filter(plasticBlockSet -> !plasticBlockSet.base().base().equals(baseBlock))
                            .map(plasticBlockSet -> new ItemStack(plasticBlockSet.base().base().get()))
                    )
                )
                .save(recipeOutput, "dye_" + AVPRecipeHelper.getItemName(baseBlock.get()));
        }

        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticBlack, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticBlue, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticBrown, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticCyan, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticGray, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticGreen, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticLightBlue, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticLightGray, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticLime, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticMagenta, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticOrange, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticPink, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticPurple, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticRed, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticWhite, recipeOutput);
        addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.INSTANCE.plasticYellow, recipeOutput);
    }

    private static void addPlasticBlockRecipeSet(AVPPlasticBlockRegistry.PlasticBlockSet plasticBlockSet, RecipeOutput recipeOutput) {
        AVPMetalRecipeHelper.addStandardCutterRecipes(recipeOutput, BLHolder.empty(), plasticBlockSet.base());
    }

    private AVPPlasticBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
