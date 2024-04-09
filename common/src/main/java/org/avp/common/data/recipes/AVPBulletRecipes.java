package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AVPElectronicItems;

/**
 * @author Boston Vanseghi
 */
public final class AVPBulletRecipes {

    public static void addRecipes(RecipeOutput recipeOutput) {
        addCaselessBulletRecipes(recipeOutput);
        addHeavyBulletRecipes(recipeOutput);
        addPistolBulletRecipes(recipeOutput);
        addRifleBulletRecipes(recipeOutput);
        addShotgunBulletRecipes(recipeOutput);
    }

    private static void addCaselessBulletRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_CASELESS.get())
            .pattern("A")
            .pattern("B")
            .unlockedBy("has_caseless_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_CASELESS.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS_ELECTRIC.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_CASELESS.get())
            .define('C', AVPElectronicItems.CAPACITOR.get())
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_caseless_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_CASELESS.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS_EXPLOSIVE.get())
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_CASELESS.get())
            .define('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_caseless_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_CASELESS.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS_INCENDIARY.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_CASELESS.get())
            .define('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_caseless_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_CASELESS.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_CASELESS_PENETRATION.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_CASELESS.get())
            .define('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_caseless_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_CASELESS.get()))
            .save(recipeOutput);
    }

    private static void addHeavyBulletRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_HEAVY.get())
            .pattern("A")
            .pattern("B")
            .unlockedBy("has_heavy_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_HEAVY.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY_ELECTRIC.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_HEAVY.get())
            .define('C', AVPElectronicItems.CAPACITOR.get())
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_heavy_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_HEAVY.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY_EXPLOSIVE.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_HEAVY.get())
            .define('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_heavy_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_HEAVY.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY_INCENDIARY.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_HEAVY.get())
            .define('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_heavy_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_HEAVY.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_HEAVY_PENETRATION.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_HEAVY.get())
            .define('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_heavy_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_HEAVY.get()))
            .save(recipeOutput);
    }

    private static void addPistolBulletRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_PISTOL.get())
            .pattern("A")
            .pattern("B")
            .unlockedBy("has_pistol_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_PISTOL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL_ELECTRIC.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_PISTOL.get())
            .define('C', AVPElectronicItems.CAPACITOR.get())
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_pistol_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_PISTOL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL_EXPLOSIVE.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_PISTOL.get())
            .define('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_pistol_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_PISTOL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL_INCENDIARY.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_PISTOL.get())
            .define('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_pistol_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_PISTOL.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_PISTOL_PENETRATION.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_PISTOL.get())
            .define('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_pistol_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_PISTOL.get()))
            .save(recipeOutput);
    }

    private static void addRifleBulletRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_RIFLE.get())
            .pattern("A")
            .pattern("B")
            .unlockedBy("has_rifle_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_RIFLE.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE_ELECTRIC.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_RIFLE.get())
            .define('C', AVPElectronicItems.CAPACITOR.get())
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_rifle_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_RIFLE.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE_EXPLOSIVE.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_RIFLE.get())
            .define('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_rifle_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_RIFLE.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE_INCENDIARY.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_RIFLE.get())
            .define('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_rifle_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_RIFLE.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_RIFLE_PENETRATION.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_RIFLE.get())
            .define('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_rifle_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_RIFLE.get()))
            .save(recipeOutput);
    }

    private static void addShotgunBulletRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_SHOTGUN.get())
            .pattern("A")
            .pattern("B")
            .unlockedBy("has_shotgun_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_SHOTGUN.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN_ELECTRIC.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_SHOTGUN.get())
            .define('C', AVPElectronicItems.CAPACITOR.get())
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_shotgun_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_SHOTGUN.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN_EXPLOSIVE.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_SHOTGUN.get())
            .define('C', Items.TNT)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_shotgun_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_SHOTGUN.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN_INCENDIARY.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_SHOTGUN.get())
            .define('C', Items.BLAZE_POWDER)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_shotgun_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_SHOTGUN.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPBulletItems.BULLET_SHOTGUN_PENETRATION.get(), 16)
            .define('A', AVPAmmunitionPartItems.BULLET_TIP.get())
            .define('B', AVPAmmunitionPartItems.CASING_SHOTGUN.get())
            .define('C', Items.OBSIDIAN)
            .pattern("AC")
            .pattern("B ")
            .unlockedBy("has_shotgun_casing", AVPRecipeProvider.has(AVPAmmunitionPartItems.CASING_SHOTGUN.get()))
            .save(recipeOutput);
    }

    private AVPBulletRecipes() {
        throw new UnsupportedOperationException();
    }
}
