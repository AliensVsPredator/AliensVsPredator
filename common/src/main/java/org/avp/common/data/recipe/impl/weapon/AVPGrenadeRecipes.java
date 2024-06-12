package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;
import org.avp.common.registry.item.AVPWeaponItemRegistry;

public final class AVPGrenadeRecipes {

    public static void addGrenadeRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponItemRegistry.INSTANCE.grenadeM40, 3)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', Items.GUNPOWDER)
            .pattern("ABA")
            .pattern("ABA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponItemRegistry.INSTANCE.grenadeIncendiary, 3)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', Items.GUNPOWDER)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("ACA")
            .pattern("ABA")
            .save(recipeOutput);
    }

    private AVPGrenadeRecipes() {
        throw new UnsupportedOperationException();
    }
}
