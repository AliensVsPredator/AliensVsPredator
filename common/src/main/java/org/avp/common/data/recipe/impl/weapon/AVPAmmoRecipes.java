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
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.ammoChargePack)
            .defineAndUnlockIfHas('A', Items.GOLD_NUGGET)
            .defineAndUnlockIfHas('B', AVPElectronicItems.INSTANCE.capacitor)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.resistor)
            .defineAndUnlockIfHas('D', AVPItems.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('E', AVPItems.INSTANCE.dustLithium)
            .pattern("AAA")
            .pattern("BCB")
            .pattern("DED")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.ammoFlamethrower)
            .defineAndUnlockIfHas('A', Items.SLIME_BALL)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .defineAndUnlockIfHas('D', AVPItems.INSTANCE.polycarbonate)
            .pattern("DAD")
            .pattern("BCB")
            .pattern("BBB")
            .save(recipeOutput);
    }

    private AVPAmmoRecipes() {
        throw new UnsupportedOperationException();
    }
}
