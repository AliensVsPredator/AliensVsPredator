package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPWeaponBlueprintItems;

public final class AVPWeaponBlueprintRecipes {

    public static void addWeaponBlueprintRecipes(RecipeOutput recipeOutput) {
        // For every weapon blueprint, add a recipe that allows it to be used in crafting itself into two more.
        AVPWeaponBlueprintItems.getEntries()
            .forEach(
                itemGameObject -> AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, itemGameObject, 2)
                    .defineAndUnlockIfHas('A', Items.PAPER)
                    .defineAndUnlockIfHas('B', Items.DIAMOND)
                    .defineAndUnlockIfHas('C', itemGameObject)
                    .pattern("ABA")
                    .pattern("BCB")
                    .pattern("ABA")
                    .save(recipeOutput)
            );
    }

    private AVPWeaponBlueprintRecipes() {
        throw new UnsupportedOperationException();
    }
}
