package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPAmmoRecipes {

    public static void addAmmoRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.AMMO_CHARGE_PACK)
            .defineAndUnlockIfHas('A', Items.GOLD_NUGGET)
            .defineAndUnlockIfHas('B', AVPElectronicItems.CAPACITOR)
            .defineAndUnlockIfHas('C', AVPElectronicItems.RESISTOR)
            .defineAndUnlockIfHas('D', AVPItems.POLYCARBONATE)
            .defineAndUnlockIfHas('E', AVPItems.INGOT_LITHIUM)
            .pattern("AAA")
            .pattern("BCB")
            .pattern("DED")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.AMMO_FLAMETHROWER)
            .defineAndUnlockIfHas('A', Items.SLIME_BALL)
            .defineAndUnlockIfHas('B', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .defineAndUnlockIfHas('D', AVPItems.POLYCARBONATE)
            .pattern("DAD")
            .pattern("BCB")
            .pattern("BBB")
            .save(recipeOutput);
    }

    private AVPAmmoRecipes() {
        throw new UnsupportedOperationException();
    }
}
