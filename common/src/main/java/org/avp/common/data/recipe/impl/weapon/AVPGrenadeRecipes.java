package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.Items;
import org.avp.api.Holder;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.item.AVPItems;
import org.avp.common.item.AVPWeaponBlueprintItems;
import org.avp.common.item.AVPWeaponItems;
import org.avp.common.item.AVPWeaponPartItems;

public final class AVPGrenadeRecipes {

    public static void addGrenadeRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.grenadeM40, 3)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', Items.GUNPOWDER)
            .pattern("ABA")
            .pattern("ABA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.grenadeIncendiary, 3)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotSteel)
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
