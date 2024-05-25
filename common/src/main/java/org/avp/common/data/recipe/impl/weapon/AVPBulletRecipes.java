package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import net.minecraft.world.level.ItemLike;
import org.avp.api.Holder;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPBulletRecipes {

    public static void addRecipes(RecipeOutput recipeOutput) {
        addBulletSetRecipes(recipeOutput, AVPBulletItems.INSTANCE.bulletCaseless, AVPAmmunitionPartItems.INSTANCE.casingCaseless);
        addBulletSetRecipes(recipeOutput, AVPBulletItems.INSTANCE.bulletHeavy, AVPAmmunitionPartItems.INSTANCE.casingHeavy);
        addBulletSetRecipes(recipeOutput, AVPBulletItems.INSTANCE.bulletPistol, AVPAmmunitionPartItems.INSTANCE.casingPistol);
        addBulletSetRecipes(recipeOutput, AVPBulletItems.INSTANCE.bulletRifle, AVPAmmunitionPartItems.INSTANCE.casingRifle);
        addBulletSetRecipes(recipeOutput, AVPBulletItems.INSTANCE.bulletShotgun, AVPAmmunitionPartItems.INSTANCE.casingShotgun);
    }

    private static void addStandardBulletRecipe(
        RecipeOutput recipeOutput,
        Holder<? extends ItemLike> outputHolder,
        Holder<? extends ItemLike> casingHolder
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, outputHolder, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', casingHolder)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);
    }

    private static void addModifiedBulletRecipe(
        RecipeOutput recipeOutput,
        Holder<? extends ItemLike> outputHolder,
        Holder<? extends ItemLike> casingHolder,
        ItemLike itemLikeModification
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, outputHolder, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItems.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', casingHolder)
            .defineAndUnlockIfHas('C', itemLikeModification)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addModifiedBulletRecipe(
        RecipeOutput recipeOutput,
        Holder<? extends ItemLike> outputHolder,
        Holder<? extends ItemLike> casingHolder,
        Holder<? extends ItemLike> modificationHolder
    ) {
        addModifiedBulletRecipe(recipeOutput, outputHolder, casingHolder, modificationHolder.get());
    }

    private static void addBulletSetRecipes(RecipeOutput recipeOutput, AVPBulletItems.ItemHolderBulletSet itemHolderBulletSet, Holder<? extends ItemLike> casingHolder) {
        addStandardBulletRecipe(recipeOutput, itemHolderBulletSet.base(), casingHolder);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.acid(), casingHolder, AVPItems.INSTANCE.bottleTintedAcid);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.electric(), casingHolder, AVPElectronicItems.INSTANCE.capacitor);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.explosive(), casingHolder, Items.TNT);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.incendiary(), casingHolder, Items.BLAZE_POWDER);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.penetration(), casingHolder, Items.OBSIDIAN);
    }

    private AVPBulletRecipes() {
        throw new UnsupportedOperationException();
    }
}
