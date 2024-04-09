package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.item.AVPItems;

public final class AVPMaterialsRecipes {

    public static void addMaterialRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPItems.CARBON.get(), 8)
            .define('A', ItemTags.COALS)
            .pattern("AA")
            .pattern("AA")
            .unlockedBy("has_coal", AVPRecipeProvider.has(ItemTags.COALS))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AVPItems.NEODYMIUM_MAGNET.get(), 4)
            .requires(AVPItems.NEODYMIUM.get())
            .requires(AVPItems.COBALT.get(), 2)
            .unlockedBy("has_cobalt", AVPRecipeProvider.has(AVPItems.COBALT.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPItems.POLYCARBONATE.get(), 4)
            .define('A', AVPItems.SILICA.get())
            .define('B', AVPItems.CARBON.get())
            .pattern("AAA")
            .pattern("BBB")
            .pattern("BBB")
            .unlockedBy("has_carbon", AVPRecipeProvider.has(AVPItems.CARBON.get()))
            .save(recipeOutput);

        var poylmerOutput = AVPItems.POLYMER.get();
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.SLIME_BALL), RecipeCategory.MISC, poylmerOutput, 0.0F, 200)
            .unlockedBy("has_slime_ball", AVPRecipeProvider.has(Items.SLIME_BALL))
            .save(recipeOutput, AVPConstants.MOD_ID + ":polymer_from_smelting_slime_ball");

        var steelOutput = AVPItems.INGOT_STEEL.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_INGOT), RecipeCategory.MISC, steelOutput, 0.7F, 100)
            .unlockedBy("has_iron_ingot", AVPRecipeProvider.has(Items.IRON_INGOT))
            .save(recipeOutput, AVPConstants.MOD_ID + ":ingot_steel_from_blasting_iron_ingot");
    }

    private AVPMaterialsRecipes() {
        throw new UnsupportedOperationException();
    }
}
