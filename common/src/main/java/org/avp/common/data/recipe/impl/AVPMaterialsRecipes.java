package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPMaterialsRecipes {

    public static void addMaterialRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPItemRegistry.INSTANCE.carbon, 8)
            .defineAndUnlockIfHas('A', ItemTags.COALS)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AVPItemRegistry.INSTANCE.neodymiumMagnet, 4)
            .requiresAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.neodymium)
            .requiresAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.cobalt, 2)
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPItemRegistry.INSTANCE.polycarbonate, 4)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.silica)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.carbon)
            .pattern("AAA")
            .pattern("BBB")
            .pattern("BBB")
            .save(recipeOutput);

        var poylmerOutput = AVPItemRegistry.INSTANCE.polymer.get();
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.SLIME_BALL), RecipeCategory.MISC, poylmerOutput, 0, 200)
            .unlockedBy("has_slime_ball", AVPRecipeProvider.has(Items.SLIME_BALL))
            .save(recipeOutput, AVPConstants.MOD_ID + ":polymer_from_smelting_slime_ball");

        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPItemRegistry.INSTANCE.bottleTinted, 3)
            .defineAndUnlockIfHas('A', Items.TINTED_GLASS)
            .pattern("A A")
            .pattern(" A ")
            .save(recipeOutput);
    }

    private AVPMaterialsRecipes() {
        throw new UnsupportedOperationException();
    }
}
