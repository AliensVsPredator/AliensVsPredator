package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPBulletRecipes {

    public static void addRecipes(RecipeOutput recipeOutput) {
        addCaselessBulletRecipes(recipeOutput);
        addHeavyBulletRecipes(recipeOutput);
        addPistolBulletRecipes(recipeOutput);
        addRifleBulletRecipes(recipeOutput);
        addShotgunBulletRecipes(recipeOutput);
    }

    private static void addCaselessBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletCaseless, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingCaseless)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletCaselessAcid, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingCaseless)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.bottleTintedAcid)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletCaselessElectric, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingCaseless)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.capacitor)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletCaselessExplosive)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingCaseless)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletCaselessIncendiary, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingCaseless)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletCaselessPenetration, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingCaseless)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addHeavyBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletHeavy, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingHeavy)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletHeavyAcid, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingHeavy)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.bottleTintedAcid)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletHeavyElectric, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingHeavy)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.capacitor)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletHeavyExplosive, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingHeavy)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletHeavyIncendiary, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingHeavy)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletHeavyPenetration, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingHeavy)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addPistolBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletPistol, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingPistol)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletPistolAcid, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingPistol)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.bottleTintedAcid)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletPistolElectric, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingPistol)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.capacitor)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletPistolExplosive, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingPistol)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletPistolIncendiary, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingPistol)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletPistolPenetration, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingPistol)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addRifleBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletRifle, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingRifle)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletRifleAcid, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingRifle)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.bottleTintedAcid)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletRifleElectric, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingRifle)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.capacitor)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletRifleExplosive, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingRifle)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletRifleIncendiary, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingRifle)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletRiflePenetration, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingRifle)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addShotgunBulletRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletShotgun, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingShotgun)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletShotgunAcid, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingShotgun)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.bottleTintedAcid)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletShotgunElectric, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingShotgun)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.capacitor)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletShotgunExplosive, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingShotgun)
            .defineAndUnlockIfHas('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletShotgunIncendiary, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingShotgun)
            .defineAndUnlockIfHas('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.INSTANCE.bulletShotgunPenetration, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', AVPAmmunitionPartItems.INSTANCE.casingShotgun)
            .defineAndUnlockIfHas('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private AVPBulletRecipes() {
        throw new UnsupportedOperationException();
    }
}
