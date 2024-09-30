package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.data.block.metal.MetalSteelBlockSetDataContainer;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPSteelRecipes {

    public static void addSteelRecipes(RecipeOutput recipeOutput) {
        var ingotHolder = AVPItemRegistry.INSTANCE.ingotSteel;

        // TODO: Remove eventually.
        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            MetalSteelBlockSetDataContainer.INSTANCE.getHolder().get(),
            AVPItemRegistry.INSTANCE.ingotSteel.get()
        );

        // Smelting
        // TODO:
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_INGOT), RecipeCategory.MISC, ingotHolder.get(), 0.7F, 100)
            .unlockedBy("has_iron_ingot", AVPRecipeProvider.has(Items.IRON_INGOT))
            .save(recipeOutput, AVPConstants.MOD_ID + ":ingot_steel_from_blasting_iron_ingot");
    }

    private AVPSteelRecipes() {
        throw new UnsupportedOperationException();
    }
}
