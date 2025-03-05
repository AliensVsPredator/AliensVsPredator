package com.avp.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import com.avp.common.item.AVPItems;
import com.avp.data.recipe.builder.RecipeBuilder;

public class GunRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('S', AVPItems.STEEL_INGOT)
            .define('T', Items.TNT)
            .pattern("SSS")
            .pattern("STS")
            .pattern("SSS")
            .into(1, AVPItems.ROCKET);

        createCasingRecipes(builder);
        createBulletRecipes(builder);
        createGunPartRecipes(builder);
        createGunRecipes(builder);
    }

    private static void createCasingRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', AVPItems.BRASS_INGOT)
            .pattern("A")
            .pattern("B")
            .into(1, AVPItems.SMALL_CASING);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', AVPItems.BRASS_INGOT)
            .pattern("A")
            .pattern("B")
            .pattern("B")
            .into(1, AVPItems.MEDIUM_CASING);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', AVPItems.BRASS_INGOT)
            .pattern("BAB")
            .pattern("BBB")
            .into(1, AVPItems.HEAVY_CASING);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', AVPItems.BRASS_INGOT)
            .define('C', AVPItems.POLYMER)
            .pattern("A")
            .pattern("C")
            .pattern("B")
            .into(1, AVPItems.SHOTGUN_CASING);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', Items.CLAY_BALL)
            .pattern(" A ")
            .pattern("BAB")
            .pattern(" B ")
            .into(1, AVPItems.CASELESS_CASING);
    }

    private static void createBulletRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.LEAD_INGOT)
            .pattern("A")
            .into(4, AVPItems.BULLET_TIP);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.BULLET_TIP)
            .define('B', AVPItems.SMALL_CASING)
            .pattern("A")
            .pattern("B")
            .into(8, AVPItems.SMALL_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.BULLET_TIP)
            .define('B', AVPItems.MEDIUM_CASING)
            .pattern("A")
            .pattern("B")
            .into(8, AVPItems.MEDIUM_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.BULLET_TIP)
            .define('B', AVPItems.HEAVY_CASING)
            .pattern("A")
            .pattern("B")
            .into(16, AVPItems.HEAVY_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.BULLET_TIP)
            .define('B', AVPItems.SHOTGUN_CASING)
            .pattern("A")
            .pattern("B")
            .into(8, AVPItems.SHOTGUN_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.BULLET_TIP)
            .define('B', AVPItems.CASELESS_CASING)
            .pattern("A")
            .pattern("B")
            .into(8, AVPItems.CASELESS_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.ALUMINUM_INGOT)
            .define('B', Items.MAGMA_CREAM)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .into(1, AVPItems.FUEL_TANK);
    }

    private static void createGunRecipes(RecipeBuilder builder) {
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_F903WE_RIFLE, AVPItems.F903WE_RIFLE, true);
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, AVPItems.FLAMETHROWER_SEVASTOPOL, false);
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_M37_12_SHOTGUN, AVPItems.M37_12_SHOTGUN, true);
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_ZX_76_SHOTGUN, AVPItems.ZX_76_SHOTGUN, true);
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, AVPItems.M88_MOD_4_COMBAT_PISTOL, false);
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, AVPItems.M42A3_SNIPER_RIFLE, true);
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, AVPItems.M4RA_BATTLE_RIFLE, true);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BLUEPRINT_M41A_PULSE_RIFLE)
            .requires(1, AVPItems.BARREL)
            .requires(1, AVPItems.GRIP)
            .requires(1, AVPItems.SMART_RECEIVER)
            .into(1, AVPItems.M41A_PULSE_RIFLE);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BLUEPRINT_M56_SMARTGUN)
            .requires(1, AVPItems.SMART_BARREL)
            .requires(1, AVPItems.GRIP)
            .requires(1, AVPItems.SMART_RECEIVER)
            .into(1, AVPItems.M56_SMARTGUN);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BLUEPRINT_OLD_PAINLESS)
            .requires(1, AVPItems.MINIGUN_BARREL)
            .requires(2, AVPItems.GRIP)
            .requires(1, AVPItems.RECEIVER)
            .into(1, AVPItems.OLD_PAINLESS);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER)
            .requires(1, AVPItems.ROCKET_BARREL)
            .requires(1, AVPItems.GRIP)
            .requires(1, AVPItems.SMART_RECEIVER)
            .into(1, AVPItems.M6B_ROCKET_LAUNCHER);
    }

    private static void createGunPartRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.STEEL_INGOT)
            .pattern("AAA")
            .into(1, AVPItems.BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.POLYMER)
            .define('B', AVPItems.STEEL_INGOT)
            .pattern("B")
            .pattern("A")
            .pattern("A")
            .into(1, AVPItems.GRIP);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.STEEL_INGOT)
            .define('B', AVPItems.BARREL)
            .pattern("BBB")
            .pattern("A A")
            .pattern("BBB")
            .into(1, AVPItems.MINIGUN_BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.STEEL_INGOT)
            .pattern("AAA")
            .pattern("   ")
            .pattern("AAA")
            .into(1, AVPItems.ROCKET_BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.STEEL_INGOT)
            .define('B', AVPItems.POLYMER)
            .define('C', Items.TRIPWIRE_HOOK)
            .define('D', Items.STONE_BUTTON)
            .pattern("AAA")
            .pattern("BAC")
            .pattern("BDB")
            .into(1, AVPItems.RECEIVER);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BARREL)
            .requires(1, Items.OBSERVER)
            .into(1, AVPItems.SMART_BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.CAPACITOR)
            .define('B', AVPItems.BATTERY_PACK)
            .define('C', AVPItems.CPU)
            .define('L', AVPItems.LED_DISPLAY)
            .define('P', AVPItems.POLYMER)
            .define('R', AVPItems.RECEIVER)
            .pattern(" R ")
            .pattern("CLB")
            .pattern("PAP")
            .into(1, AVPItems.SMART_RECEIVER);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.POLYMER)
            .define('B', AVPItems.STEEL_INGOT)
            .pattern("  A")
            .pattern("BAA")
            .pattern("  A")
            .into(1, AVPItems.STOCK);
    }

    private static void createGenericGunRecipe(RecipeBuilder builder, Item blueprintItem, Item result, boolean hasStock) {
        var shapeless = builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, blueprintItem)
            .requires(1, AVPItems.BARREL)
            .requires(1, AVPItems.GRIP)
            .requires(1, AVPItems.RECEIVER);

        if (hasStock) {
            shapeless.requires(1, AVPItems.STOCK);
        }

        shapeless.into(1, result);
    }
}
