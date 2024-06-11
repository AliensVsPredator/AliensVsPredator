package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPAmmunitionPartItemRegistry;
import org.avp.common.registry.item.AVPElectronicItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPAmmunitionPartRecipes {

    public static void addAmmunitionPartRecipes(RecipeOutput recipeOutput) {
        addCasingRecipes(recipeOutput);
        addRocketRecipes(recipeOutput);
        addTipRecipes(recipeOutput);
    }

    private static void addCasingRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.casingPistol)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.casingShotgun)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('C', AVPItemRegistry.INSTANCE.polymer)
            .pattern("A")
            .pattern("C")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.casingRifle)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .pattern("A")
            .pattern("B")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.casingCaseless)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.CLAY_BALL)
            .pattern(" A ")
            .pattern("BAB")
            .pattern(" B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.casingHeavy)
            .defineAndUnlockIfHas('A', Items.GUNPOWDER)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .pattern("BAB")
            .pattern("BBB")
            .save(recipeOutput);
    }

    private static void addRocketRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.rocket)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', Items.TNT)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.rocketPenetration)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', Items.TNT)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.rocketIncendiary)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', Items.TNT)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.rocketElectric)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', Items.TNT)
            .defineAndUnlockIfHas('C', AVPElectronicItemRegistry.INSTANCE.capacitor)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .save(recipeOutput);
    }

    private static void addTipRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItemRegistry.INSTANCE.bulletTip, 4)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .pattern("A")
            .save(recipeOutput);
    }

    private AVPAmmunitionPartRecipes() {
        throw new UnsupportedOperationException();
    }
}
