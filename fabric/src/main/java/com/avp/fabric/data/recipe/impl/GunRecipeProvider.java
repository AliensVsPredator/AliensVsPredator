package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

import com.avp.common.item.AVPItemTags;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class GunRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('S', TempAVPItems.STEEL_INGOT)
            .define('T', Items.TNT)
            .pattern("SSS")
            .pattern("STS")
            .pattern("SSS")
            .into(1, TempAVPItems.ROCKET);

        createCasingRecipes(builder);
        createBulletRecipes(builder);
        createGunPartRecipes(builder);
        createGunRecipes(builder);
        createGrendade(builder);
    }

    private static void createGrendade(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.STEEL_NUGGET)
            .define('B', Items.GUNPOWDER)
            .pattern("ABA")
            .pattern("ABA")
            .into(4, AVPItems.GRENADE);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.STEEL_NUGGET)
            .define('B', Items.GUNPOWDER)
            .define('C', Items.BLAZE_POWDER)
            .pattern("ACA")
            .pattern("ABA")
            .into(4, AVPItems.GRENADE_INCENDIARY);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.STEEL_NUGGET)
            .define('B', Items.GUNPOWDER)
            .define('C', AVPItemTags.URANIUM_NUGGET_LIKE)
            .pattern("ACA")
            .pattern("ABA")
            .into(4, AVPItems.GRENADE_IRRADIATED);
    }

    private static void createCasingRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', TempAVPItems.BRASS_NUGGET)
            .pattern("A")
            .pattern("B")
            .into(1, TempAVPItems.SMALL_CASING);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', TempAVPItems.BRASS_NUGGET)
            .pattern("A")
            .pattern("B")
            .pattern("B")
            .into(1, TempAVPItems.MEDIUM_CASING);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('G', Items.GUNPOWDER)
            .define('S', TempAVPItems.STEEL_NUGGET)
            .pattern("SGS")
            .pattern(" S ")
            .into(1, TempAVPItems.HEAVY_CASING);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', TempAVPItems.BRASS_NUGGET)
            .define('C', TempAVPItems.POLYMER)
            .pattern("A")
            .pattern("C")
            .pattern("B")
            .into(1, TempAVPItems.SHOTGUN_CASING);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', Items.GUNPOWDER)
            .define('B', Items.CLAY_BALL)
            .pattern(" A ")
            .pattern("BAB")
            .pattern(" B ")
            .into(1, TempAVPItems.CASELESS_CARTRIDGE);
    }

    private static void createBulletRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.LEAD_NUGGET)
            .pattern("A")
            .into(4, TempAVPItems.BULLET_TIP);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.BULLET_TIP)
            .define('B', TempAVPItems.SMALL_CASING)
            .pattern("A")
            .pattern("B")
            .into(8, TempAVPItems.SMALL_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.BULLET_TIP)
            .define('B', TempAVPItems.MEDIUM_CASING)
            .pattern("A")
            .pattern("B")
            .into(8, TempAVPItems.MEDIUM_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.BULLET_TIP)
            .define('B', TempAVPItems.HEAVY_CASING)
            .pattern("A")
            .pattern("B")
            .into(16, TempAVPItems.HEAVY_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.BULLET_TIP)
            .define('B', TempAVPItems.SHOTGUN_CASING)
            .pattern("A")
            .pattern("B")
            .into(8, TempAVPItems.SHOTGUN_SHELL);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.BULLET_TIP)
            .define('B', TempAVPItems.CASELESS_CARTRIDGE)
            .pattern("A")
            .pattern("B")
            .into(8, AVPItems.CASELESS_BULLET);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.ALUMINUM_NUGGET)
            .define('B', Items.MAGMA_CREAM)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .into(1, AVPItems.FUEL_TANK);
    }

    private static void createGunRecipes(RecipeBuilder builder) {
        createGenericGunRecipe(builder, TempAVPItems.BLUEPRINT_F903WE_RIFLE, AVPItems.F903WE_RIFLE, true);
        createGenericGunRecipe(builder, TempAVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, AVPItems.FLAMETHROWER_SEVASTOPOL, false);
        createGenericGunRecipe(builder, TempAVPItems.BLUEPRINT_M37_12_SHOTGUN, AVPItems.M37_12_SHOTGUN, true);
        createGenericGunRecipe(builder, TempAVPItems.BLUEPRINT_ZX_76_SHOTGUN, AVPItems.ZX_76_SHOTGUN, true);
        createGenericGunRecipe(builder, TempAVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, AVPItems.M88MOD4_COMBAT_PISTOL, false);
        createGenericGunRecipe(builder, TempAVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, AVPItems.M42A3_SNIPER_RIFLE, true);
        createGenericGunRecipe(builder, TempAVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, AVPItems.M4RA_BATTLE_RIFLE, true);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, TempAVPItems.BLUEPRINT_M41A_PULSE_RIFLE)
            .requires(1, TempAVPItems.BARREL)
            .requires(1, TempAVPItems.GRIP)
            .requires(1, TempAVPItems.SMART_RECEIVER)
            .requires(1, TempAVPItems.STOCK)
            .into(1, AVPItems.M41A_PULSE_RIFLE);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, TempAVPItems.BLUEPRINT_M56_SMARTGUN)
            .requires(1, TempAVPItems.SMART_BARREL)
            .requires(1, TempAVPItems.GRIP)
            .requires(1, TempAVPItems.SMART_RECEIVER)
            .into(1, AVPItems.M56_SMARTGUN);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, TempAVPItems.BLUEPRINT_OLD_PAINLESS)
            .requires(1, TempAVPItems.MINIGUN_BARREL)
            .requires(2, TempAVPItems.GRIP)
            .requires(1, TempAVPItems.RECEIVER)
            .into(1, AVPItems.OLD_PAINLESS);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, TempAVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER)
            .requires(1, TempAVPItems.ROCKET_BARREL)
            .requires(1, TempAVPItems.GRIP)
            .requires(1, TempAVPItems.SMART_RECEIVER)
            .into(1, AVPItems.M6B_ROCKET_LAUNCHER);
    }

    private static void createGunPartRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.STEEL_INGOT)
            .pattern("AAA")
            .into(1, TempAVPItems.BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.POLYMER)
            .define('B', TempAVPItems.STEEL_INGOT)
            .pattern("B")
            .pattern("A")
            .pattern("A")
            .into(1, TempAVPItems.GRIP);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.STEEL_INGOT)
            .define('B', TempAVPItems.BARREL)
            .pattern("BBB")
            .pattern("A A")
            .pattern("BBB")
            .into(1, TempAVPItems.MINIGUN_BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.STEEL_INGOT)
            .pattern("AAA")
            .pattern("   ")
            .pattern("AAA")
            .into(1, TempAVPItems.ROCKET_BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.STEEL_INGOT)
            .define('B', TempAVPItems.POLYMER)
            .define('C', Items.TRIPWIRE_HOOK)
            .define('D', Items.STONE_BUTTON)
            .pattern("AAA")
            .pattern("BAC")
            .pattern("BDB")
            .into(1, TempAVPItems.RECEIVER);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, TempAVPItems.BARREL)
            .requires(1, Items.OBSERVER)
            .into(1, TempAVPItems.SMART_BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.CAPACITOR)
            .define('B', TempAVPItems.BATTERY_PACK)
            .define('C', TempAVPItems.CPU)
            .define('L', TempAVPItems.LED_DISPLAY)
            .define('P', TempAVPItems.POLYMER)
            .define('R', TempAVPItems.RECEIVER)
            .pattern(" R ")
            .pattern("CLB")
            .pattern("PAP")
            .into(1, TempAVPItems.SMART_RECEIVER);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.POLYMER)
            .define('B', TempAVPItems.STEEL_INGOT)
            .pattern("  A")
            .pattern("BAA")
            .pattern("  A")
            .into(1, TempAVPItems.STOCK);
    }

    private static void createGenericGunRecipe(RecipeBuilder builder, Supplier<Item> blueprintItem, Item result, boolean hasStock) {
        var shapeless = builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, blueprintItem)
            .requires(1, TempAVPItems.BARREL)
            .requires(1, TempAVPItems.GRIP)
            .requires(1, TempAVPItems.RECEIVER);

        if (hasStock) {
            shapeless.requires(1, TempAVPItems.STOCK);
        }

        shapeless.into(1, result);
    }
}
