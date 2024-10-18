package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPAmmunitionPartItemRegistry;
import org.avp.common.registry.item.AVPBulletItemRegistry;
import org.avp.common.registry.item.AVPElectronicItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPBulletRecipes {

    public static void addRecipes(RecipeOutput recipeOutput) {
        addBulletSetRecipes(
            recipeOutput,
            AVPBulletItemRegistry.INSTANCE.bulletCaseless,
            AVPAmmunitionPartItemRegistry.INSTANCE.casingCaseless
        );
        addBulletSetRecipes(recipeOutput, AVPBulletItemRegistry.INSTANCE.bulletHeavy, AVPAmmunitionPartItemRegistry.INSTANCE.casingHeavy);
        addBulletSetRecipes(recipeOutput, AVPBulletItemRegistry.INSTANCE.bulletPistol, AVPAmmunitionPartItemRegistry.INSTANCE.casingPistol);
        addBulletSetRecipes(recipeOutput, AVPBulletItemRegistry.INSTANCE.bulletRifle, AVPAmmunitionPartItemRegistry.INSTANCE.casingRifle);
        addBulletSetRecipes(
            recipeOutput,
            AVPBulletItemRegistry.INSTANCE.bulletShotgun,
            AVPAmmunitionPartItemRegistry.INSTANCE.casingShotgun
        );
    }

    private static void addStandardBulletRecipe(
        RecipeOutput recipeOutput,
        BLHolder<? extends ItemLike> outputHolder,
        BLHolder<? extends ItemLike> casingHolder
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, outputHolder, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItemRegistry.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', casingHolder)
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);
    }

    private static void addModifiedBulletRecipe(
        RecipeOutput recipeOutput,
        BLHolder<? extends ItemLike> outputHolder,
        BLHolder<? extends ItemLike> casingHolder,
        ItemLike itemLikeModification
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, outputHolder, 16)
            .defineAndUnlockIfHas('A', AVPAmmunitionPartItemRegistry.INSTANCE.bulletTip)
            .defineAndUnlockIfHas('B', casingHolder)
            .defineAndUnlockIfHas('C', itemLikeModification)
            .pattern("AC")
            .pattern("B ")
            .save(recipeOutput);
    }

    private static void addModifiedBulletRecipe(
        RecipeOutput recipeOutput,
        BLHolder<? extends ItemLike> outputHolder,
        BLHolder<? extends ItemLike> casingHolder,
        BLHolder<? extends ItemLike> modificationHolder
    ) {
        addModifiedBulletRecipe(recipeOutput, outputHolder, casingHolder, modificationHolder.get());
    }

    private static void addBulletSetRecipes(
        RecipeOutput recipeOutput,
        AVPBulletItemRegistry.ItemHolderBulletSet itemHolderBulletSet,
        BLHolder<? extends ItemLike> casingHolder
    ) {
        addStandardBulletRecipe(recipeOutput, itemHolderBulletSet.base(), casingHolder);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.acid(), casingHolder, AVPItemRegistry.INSTANCE.bottleTintedAcid);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.electric(), casingHolder, AVPElectronicItemRegistry.INSTANCE.capacitor);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.explosive(), casingHolder, Items.TNT);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.incendiary(), casingHolder, Items.BLAZE_POWDER);
        addModifiedBulletRecipe(recipeOutput, itemHolderBulletSet.penetration(), casingHolder, Items.OBSIDIAN);
    }

    private AVPBulletRecipes() {
        throw new UnsupportedOperationException();
    }
}
