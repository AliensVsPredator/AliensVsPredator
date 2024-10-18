package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPWeaponBlueprintItemRegistry;

public final class AVPWeaponBlueprintRecipes {

    public static void addWeaponBlueprintRecipes(RecipeOutput recipeOutput) {
        // For every weapon blueprint, add a recipe that allows it to be used in crafting itself into two more.
        AVPWeaponBlueprintItemRegistry.INSTANCE.getValues()
            .forEach(
                holder -> AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, holder, 2)
                    .defineAndUnlockIfHas('A', Items.PAPER)
                    .defineAndUnlockIfHas('B', Items.DIAMOND)
                    .defineAndUnlockIfHas('C', holder)
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
