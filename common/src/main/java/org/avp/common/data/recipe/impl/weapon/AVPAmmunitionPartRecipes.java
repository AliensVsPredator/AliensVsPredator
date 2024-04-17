package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPAmmunitionPartRecipes {

    public static void addAmmunitionPartRecipes(RecipeOutput recipeOutput) {
        addCasingRecipes(recipeOutput);
        addRocketRecipes(recipeOutput);
        addTipRecipes(recipeOutput);
    }

    private static void addCasingRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.CASING_PISTOL)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.CASING_SHOTGUN)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.POLYMER)
            .pattern("A")
            .pattern("C")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.CASING_RIFLE)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .pattern("A")
            .pattern("B")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.CASING_CASELESS)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.CLAY_BALL)
            .pattern(" A ")
            .pattern("BAB")
            .pattern(" B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.CASING_HEAVY)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .pattern("BAB")
            .pattern("BBB")
            .save(recipeOutput);
    }

    private static void addRocketRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.ROCKET)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .defineAndUnlockIfHas('B', Items.TNT)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.ROCKET_PENETRATION)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .defineAndUnlockIfHas('B', Items.TNT)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.ROCKET_INCENDIARY)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .defineAndUnlockIfHas('B', Items.TNT)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.ROCKET_ELECTRIC)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .defineAndUnlockIfHas('B', Items.TNT)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.CAPACITOR)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);
    }

    private static void addTipRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.INSTANCE.BULLET_TIP)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .pattern("A")
            .save(recipeOutput);
    }

    private AVPAmmunitionPartRecipes() {
        throw new UnsupportedOperationException();
    }
}
