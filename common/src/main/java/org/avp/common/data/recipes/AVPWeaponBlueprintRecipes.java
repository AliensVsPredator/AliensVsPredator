package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.avp.common.item.AVPWeaponBlueprintItems;

/**
 * @author Boston Vanseghi
 */
public final class AVPWeaponBlueprintRecipes {

    public static void addWeaponBlueprintRecipes(RecipeOutput recipeOutput) {
        // For every weapon blueprint, add a recipe that allows it to be used in crafting itself into two more.
        AVPWeaponBlueprintItems.getEntries().forEach(itemGameObject -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, itemGameObject.get(), 2)
            .define('A', Items.PAPER)
            .define('B', Items.DIAMOND)
            .define('C', itemGameObject.get())
            .pattern("ABA")
            .pattern("BCB")
            .pattern("ABA")
            .unlockedBy("has_blueprint", AVPRecipeProvider.has(itemGameObject.get()))
            .save(recipeOutput));
    }

    private AVPWeaponBlueprintRecipes() {
        throw new UnsupportedOperationException();
    }
}
