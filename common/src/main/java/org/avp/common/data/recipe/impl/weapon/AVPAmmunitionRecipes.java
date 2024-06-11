package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPAmmunitionPartItemRegistry;
import org.avp.common.registry.item.AVPElectronicItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPAmmunitionRecipes {

    public static void addAmmoRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.ammoChargePack)
            .defineAndUnlockIfHas('A', Items.GOLD_NUGGET)
            .defineAndUnlockIfHas('B', AVPElectronicItemRegistry.INSTANCE.capacitor)
            .defineAndUnlockIfHas('C', AVPElectronicItemRegistry.INSTANCE.resistor)
            .defineAndUnlockIfHas('D', AVPItemRegistry.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('E', AVPItemRegistry.INSTANCE.dustLithium)
            .pattern("AAA")
            .pattern("BCB")
            .pattern("DED")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.ammoFlamethrower)
            .defineAndUnlockIfHas('A', Items.SLIME_BALL)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .defineAndUnlockIfHas('D', AVPItemRegistry.INSTANCE.polycarbonate)
            .pattern("DAD")
            .pattern("BCB")
            .pattern("BBB")
            .save(recipeOutput);
    }

    private AVPAmmunitionRecipes() {
        throw new UnsupportedOperationException();
    }
}
