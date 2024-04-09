package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPAmmoRecipes {

    public static void addAmmoRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.AMMO_CHARGE_PACK.get())
            .define('A', Items.GOLD_NUGGET)
            .define('B', AVPElectronicItems.CAPACITOR.get())
            .define('C', AVPElectronicItems.RESISTOR.get())
            .define('D', AVPItems.POLYCARBONATE.get())
            .define('E', AVPItems.INGOT_LITHIUM.get())
            .pattern("AAA")
            .pattern("BCB")
            .pattern("DED")
            .unlockedBy("has_capacitor", AVPRecipeProvider.has(AVPElectronicItems.CAPACITOR.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.AMMO_FLAMETHROWER.get())
            .define('A', Items.SLIME_BALL)
            .define('B', AVPItems.INGOT_ALUMINUM.get())
            .define('C', Items.BLAZE_POWDER)
            .define('D', AVPItems.POLYCARBONATE.get())
            .pattern("DAD")
            .pattern("BCB")
            .pattern("BBB")
            .unlockedBy("has_blaze_powder", AVPRecipeProvider.has(Items.BLAZE_POWDER))
            .save(recipeOutput);
    }

    private AVPAmmoRecipes() {
        throw new UnsupportedOperationException();
    }
}
