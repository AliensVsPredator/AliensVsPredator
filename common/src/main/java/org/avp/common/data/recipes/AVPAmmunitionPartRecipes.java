package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

/**
 * @author Boston Vanseghi
 */
public final class AVPAmmunitionPartRecipes {

    public static void addAmmunitionPartRecipes(RecipeOutput recipeOutput) {
        addCasingRecipes(recipeOutput);
        addRocketRecipes(recipeOutput);
        addTipRecipes(recipeOutput);
    }

    private static void addCasingRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.CASING_PISTOL.get())
            .define('A', Items.GUNPOWDER)
            .define('B', Items.COPPER_INGOT)
            .pattern("A")
            .pattern("B")
            .unlockedBy("has_gunpowder", AVPRecipeProvider.has(Items.GUNPOWDER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.CASING_SHOTGUN.get())
            .define('A', Items.GUNPOWDER)
            .define('B', Items.COPPER_INGOT)
            .define('C', AVPItems.POLYMER.get())
            .pattern("A")
            .pattern("C")
            .pattern("B")
            .unlockedBy("has_gunpowder", AVPRecipeProvider.has(Items.GUNPOWDER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.CASING_RIFLE.get())
            .define('A', Items.GUNPOWDER)
            .define('B', Items.COPPER_INGOT)
            .pattern("A")
            .pattern("B")
            .pattern("B")
            .unlockedBy("has_gunpowder", AVPRecipeProvider.has(Items.GUNPOWDER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.CASING_CASELESS.get())
            .define('A', Items.GUNPOWDER)
            .define('B', Items.CLAY_BALL)
            .pattern(" A ")
            .pattern("BAB")
            .pattern(" B ")
            .unlockedBy("has_gunpowder", AVPRecipeProvider.has(Items.GUNPOWDER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.CASING_HEAVY.get())
            .define('A', Items.GUNPOWDER)
            .define('B', Items.COPPER_INGOT)
            .pattern("BAB")
            .pattern("BBB")
            .unlockedBy("has_gunpowder", AVPRecipeProvider.has(Items.GUNPOWDER))
            .save(recipeOutput);
    }

    private static void addRocketRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.ROCKET.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .define('B', Items.TNT)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .unlockedBy("has_steel_ingot", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.ROCKET_PENETRATION.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .define('B', Items.TNT)
            .define('C', Items.OBSIDIAN)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .unlockedBy("has_steel_ingot", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.ROCKET_INCENDIARY.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .define('B', Items.TNT)
            .define('C', Items.BLAZE_POWDER)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .unlockedBy("has_steel_ingot", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.ROCKET_ELECTRIC.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .define('B', Items.TNT)
            .define('C', AVPElectronicItems.CAPACITOR.get())
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .unlockedBy("has_steel_ingot", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);
    }

    private static void addTipRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .pattern("A")
            .unlockedBy("has_steel_ingot", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);
    }

    private AVPAmmunitionPartRecipes() {
        throw new UnsupportedOperationException();
    }
}
