package org.avp.common.data.recipes.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipes.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AVPElectronicItems;

public final class AVPBulletRecipes {

    public static void addRecipes(RecipeOutput recipeOutput) {
        addCaselessBulletRecipes(recipeOutput);
        addHeavyBulletRecipes(recipeOutput);
        addPistolBulletRecipes(recipeOutput);
        addRifleBulletRecipes(recipeOutput);
        addShotgunBulletRecipes(recipeOutput);
    }

    private static void addCaselessBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_CASELESS)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS_ELECTRIC, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_CASELESS)
            .defineAndUnlockIfHas('C', AVPElectronicItems.CAPACITOR)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS_EXPLOSIVE)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_CASELESS)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS_INCENDIARY, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_CASELESS)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS_PENETRATION, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_CASELESS)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addHeavyBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_HEAVY)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY_ELECTRIC, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_HEAVY)
            .defineAndUnlockIfHas('C', AVPElectronicItems.CAPACITOR)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY_EXPLOSIVE, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_HEAVY)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY_INCENDIARY, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_HEAVY)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY_PENETRATION, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_HEAVY)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addPistolBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_PISTOL)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL_ELECTRIC, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_PISTOL)
            .defineAndUnlockIfHas('C', AVPElectronicItems.CAPACITOR)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL_EXPLOSIVE, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_PISTOL)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL_INCENDIARY, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_PISTOL)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL_PENETRATION, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_PISTOL)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addRifleBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_RIFLE)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE_ELECTRIC, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_RIFLE)
            .defineAndUnlockIfHas('C', AVPElectronicItems.CAPACITOR)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE_EXPLOSIVE, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_RIFLE)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE_INCENDIARY, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_RIFLE)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE_PENETRATION, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_RIFLE)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addShotgunBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_SHOTGUN)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN_ELECTRIC, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_SHOTGUN)
            .defineAndUnlockIfHas('C', AVPElectronicItems.CAPACITOR)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN_EXPLOSIVE, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_SHOTGUN)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN_INCENDIARY, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_SHOTGUN)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN_PENETRATION, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.BULLET_TIP)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.CASING_SHOTGUN)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private AVPBulletRecipes() {
        throw new UnsupportedOperationException();
    }
}
