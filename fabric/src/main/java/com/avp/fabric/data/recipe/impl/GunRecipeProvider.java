package com.avp.fabric.data.recipe.impl;

import com.compat.CommonItemTags;
import com.human.common.registry.init.item.HumanGunItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.tag.AVPItemTags;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class GunRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('S', CommonItemTags.INGOTS_STEEL)
            .define('T', Items.TNT)
            .pattern("SSS")
            .pattern("STS")
            .pattern("SSS")
            .into(1, AVPItems.ROCKET);

        createBulletRecipes(builder);
        createGunPartRecipes(builder);
        createGunRecipes(builder);
        createGrenadeRecipes(builder);
    }

    private static void createGrenadeRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_STEEL)
            .define('B', Items.GUNPOWDER)
            .pattern("ABA")
            .pattern("ABA")
            .into(4, AVPItems.GRENADE);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_STEEL)
            .define('B', Items.GUNPOWDER)
            .define('C', Items.BLAZE_POWDER)
            .pattern("ACA")
            .pattern("ABA")
            .into(4, AVPItems.GRENADE_INCENDIARY);
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_STEEL)
            .define('B', Items.GUNPOWDER)
            .define('C', AVPItemTags.URANIUM_NUGGET_LIKE)
            .pattern("ACA")
            .pattern("ABA")
            .into(4, AVPItems.GRENADE_IRRADIATED);
    }

    private static void createBulletRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_BRASS)
            .define('B', Items.GUNPOWDER)
            .define('C', CommonItemTags.NUGGETS_LEAD)
            .pattern(" C ")
            .pattern("ABA")
            .pattern("AAA")
            .into(24, AVPItems.SMALL_BULLET);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_BRASS)
            .define('B', Items.GUNPOWDER)
            .define('C', CommonItemTags.NUGGETS_STEEL)
            .define('D', CommonItemTags.NUGGETS_LEAD)
            .pattern("ADA")
            .pattern("ABA")
            .pattern("ACA")
            .into(16, AVPItems.MEDIUM_BULLET);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_BRASS)
            .define('B', Items.GUNPOWDER)
            .define('C', CommonItemTags.NUGGETS_STEEL)
            .define('D', CommonItemTags.NUGGETS_LEAD)
            .pattern("DDD")
            .pattern("CBC")
            .pattern("CAC")
            .into(8, AVPItems.HEAVY_BULLET);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_BRASS)
            .define('B', Items.GUNPOWDER)
            .define('C', AVPItems.POLYMER)
            .define('D', CommonItemTags.NUGGETS_LEAD)
            .pattern("DDD")
            .pattern("CBC")
            .pattern("CAC")
            .into(12, AVPItems.SHOTGUN_SHELL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_BRASS)
            .define('B', Items.GUNPOWDER)
            .define('C', Items.CLAY_BALL)
            .define('D', CommonItemTags.NUGGETS_LEAD)
            .pattern(" D ")
            .pattern("CBC")
            .pattern("CAC")
            .into(16, AVPItems.CASELESS_BULLET);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.NUGGETS_ALUMINUM)
            .define('B', Items.MAGMA_CREAM)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .into(1, AVPItems.FUEL_TANK);
    }

    private static void createGunRecipes(RecipeBuilder builder) {
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_F903WE_RIFLE, HumanGunItems.F903WE_RIFLE.get(), true);
        createGenericGunRecipe(
            builder,
            AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL,
            HumanGunItems.FLAMETHROWER_SEVASTOPOL.get(),
            false
        );
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_M37_12_SHOTGUN, HumanGunItems.M37_12_SHOTGUN.get(), true);
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_ZX_76_SHOTGUN, HumanGunItems.ZX_76_SHOTGUN.get(), true);
        createGenericGunRecipe(
            builder,
            AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL,
            HumanGunItems.M88MOD4_COMBAT_PISTOL.get(),
            false
        );
        createGenericGunRecipe(
            builder,
            AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE,
            HumanGunItems.M42A3_SNIPER_RIFLE.get(),
            true
        );
        createGenericGunRecipe(builder, AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, HumanGunItems.M4RA_BATTLE_RIFLE.get(), true);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BLUEPRINT_M41A_PULSE_RIFLE)
            .requires(1, AVPItems.BARREL)
            .requires(1, AVPItems.GRIP)
            .requires(1, AVPItems.SMART_RECEIVER)
            .requires(1, AVPItems.STOCK)
            .into(1, HumanGunItems.M41A_PULSE_RIFLE);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BLUEPRINT_M56_SMARTGUN)
            .requires(1, AVPItems.SMART_BARREL)
            .requires(1, AVPItems.GRIP)
            .requires(1, AVPItems.SMART_RECEIVER)
            .into(1, HumanGunItems.M56_SMARTGUN);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BLUEPRINT_OLD_PAINLESS)
            .requires(1, AVPItems.MINIGUN_BARREL)
            .requires(2, AVPItems.GRIP)
            .requires(1, AVPItems.RECEIVER)
            .into(1, HumanGunItems.OLD_PAINLESS);

        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER)
            .requires(1, AVPItems.ROCKET_BARREL)
            .requires(1, AVPItems.GRIP)
            .requires(1, AVPItems.SMART_RECEIVER)
            .into(1, HumanGunItems.M6B_ROCKET_LAUNCHER);
    }

    private static void createGunPartRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.INGOTS_STEEL)
            .pattern("AAA")
            .into(1, AVPItems.BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.POLYMER)
            .define('B', CommonItemTags.INGOTS_STEEL)
            .pattern("B")
            .pattern("A")
            .pattern("A")
            .into(1, AVPItems.GRIP);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.INGOTS_STEEL)
            .define('B', AVPItems.BARREL)
            .pattern("BBB")
            .pattern("A A")
            .pattern("BBB")
            .into(1, AVPItems.MINIGUN_BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.INGOTS_STEEL)
            .pattern("AAA")
            .pattern("   ")
            .pattern("AAA")
            .into(1, AVPItems.ROCKET_BARREL);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.INGOTS_STEEL)
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
            .define('B', CommonItemTags.INGOTS_STEEL)
            .pattern("  A")
            .pattern("BAA")
            .pattern("  A")
            .into(1, AVPItems.STOCK);
    }

    private static void createGenericGunRecipe(RecipeBuilder builder, Supplier<Item> blueprintItem, Item result, boolean hasStock) {
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
