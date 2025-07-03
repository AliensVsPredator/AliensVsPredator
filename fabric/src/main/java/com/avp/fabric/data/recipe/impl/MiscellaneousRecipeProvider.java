package com.avp.fabric.data.recipe.impl;

import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.tag.AVPItemTags;
import com.avp.fabric.data.recipe.RecipeConstants;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class MiscellaneousRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', AVPItems.ALUMINUM_INGOT)
            .define('B', AVPItems.SYRINGE)
            .define('C', AVPItems.INTEGRATED_CIRCUIT)
            .define('D', AVPItems.LED_DISPLAY)
            .define('E', AVPItems.REDSTONE_CRYSTAL)
            .pattern("ABA")
            .pattern("CDC")
            .pattern("AEA")
            .into(1, AVPItems.GENE_READER);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', Items.IRON_NUGGET)
            .define('B', Items.GLASS_BOTTLE)
            .define('C', AVPItems.POLYMER)
            .pattern("A  ")
            .pattern(" B ")
            .pattern("  C")
            .into(1, AVPItems.SYRINGE);

        builder.blast(Blocks.REDSTONE_BLOCK)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(AVPItems.REDSTONE_CRYSTAL);

        builder.blast(AlienItems.IRRADIATED_CHITIN)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.UNCOMMON_MATERIAL_SMELT_EXPERIENCE)
            .into(AlienItems.CHITIN);

        builder.blast(AlienItems.PLATED_IRRADIATED_CHITIN)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.UNCOMMON_MATERIAL_SMELT_EXPERIENCE)
            .into(AlienItems.PLATED_CHITIN);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', Items.LAPIS_LAZULI)
            .define('P', AVPItems.POLYMER)
            .define('D', ItemTags.PLANKS)
            .pattern("A A")
            .pattern("P P")
            .pattern("D D")
            .into(1, AVPBlocks.BLUEPRINT_BLOCK);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', AVPItems.AUTUNITE_DUST)
            .define('G', Items.GOLD_INGOT)
            .define('D', Items.DIAMOND)
            .pattern("GDG")
            .pattern("AAA")
            .pattern("GDG")
            .into(1, AVPItems.NUCLEAR_BATTERY);

        // builder.shaped()
        // .withCategory(RecipeCategory.MISC)
        // .define('S', AVPItems.SPEAKER)
        // .define('D', AVPItems.SERVO)
        // .define('A', AVPItems.STEEL_INGOT)
        // .pattern("SAS")
        // .pattern("SDS")
        // .pattern("AAA")
        // .into(1, AVPBlocks.RESONATOR_BLOCK);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', ItemTags.COALS)
            .pattern("A")
            .into(2, AVPItems.CARBON_DUST);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', AVPItems.ALUMINUM_INGOT)
            .define('B', AVPItems.POLYMER)
            .define('C', Items.CHEST)
            .pattern("ABA")
            .pattern("BCB")
            .pattern("ABA")
            .into(1, AVPItems.ARMOR_CASE);

        builder.smelt(AlienItems.IRRADIATED_RESIN_BALL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(Items.SLIME_BALL);

        builder.smelt(AlienItems.ABERRANT_RESIN_BALL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(Items.SLIME_BALL);

        builder.smelt(AlienItems.RESIN_BALL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(Items.SLIME_BALL);

        builder.smelt(AlienItems.NETHER_RESIN_BALL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(Items.SLIME_BALL);

        builder.smelt(Items.SLIME_BALL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(AVPItems.POLYMER);

        builder.blast(CoreBlocks.SILICA_GRAVEL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.RARE_SMELT_EXPERIENCE)
            .into(AVPItems.SILICON.get());

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('I', AVPItemTags.IRON_INGOT_LIKE)
            .define('N', Items.IRON_NUGGET)
            .pattern("INI")
            .pattern("NIN")
            .pattern("INI")
            .into(16, AVPBlocks.RAZOR_WIRE);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('I', AlienItems.RAW_ROYAL_JELLY)
            .pattern("III")
            .pattern("III")
            .pattern("III")
            .into(1, AlienBlocks.ROYAL_JELLY_BLOCK);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, AlienBlocks.ROYAL_JELLY_BLOCK)
            .into(9, AlienItems.RAW_ROYAL_JELLY);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('T', AVPItems.TITANIUM_INGOT)
            .define('R', AVPItems.REDSTONE_CRYSTAL)
            .define('P', Items.PISTON)
            .define('B', Items.BUCKET)
            .pattern("TRT")
            .pattern("TPT")
            .pattern("TBT")
            .into(1, AVPItems.CANISTER);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, Items.POISONOUS_POTATO)
            .requires(1, AlienItems.RAW_ROYAL_JELLY)
            .into(1, AlienItems.POISON_JELLY);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('C', Items.CLOCK)
            .define('S', AVPItems.LED_DISPLAY)
            .define('P', AVPItems.CPU)
            .define('L', AVPItems.LEAD_INGOT)
            .define('N', AVPItems.NEODYMIUM_MAGNET)
            .define('T', Items.TNT)
            .pattern("CSP")
            .pattern("LNL")
            .pattern("TTT")
            .into(1, AVPBlocks.NUKE_BLOCK);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('S', Items.SMOOTH_STONE)
            .define('F', Items.FURNACE)
            .define('I', AVPItemTags.IRON_INGOT_LIKE)
            .pattern("SSS")
            .pattern("SFS")
            .pattern("III")
            .into(1, AVPBlocks.INDUSTRIAL_FURNACE);

        // Stone Recipes
        builder.industrialFurnaceSmelting(Items.COBBLESTONE)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.STONE);

        builder.industrialFurnaceSmelting(Items.COBBLED_DEEPSLATE)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.DEEPSLATE);

        builder.industrialFurnaceSmelting(Items.QUARTZ_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.SMOOTH_QUARTZ);

        builder.industrialFurnaceSmelting(Items.STONE)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.SMOOTH_STONE);

        builder.industrialFurnaceSmelting(Items.BASALT)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.SMOOTH_BASALT);

        // Sandstone Recipes
        builder.industrialFurnaceSmelting(Items.SANDSTONE)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.SMOOTH_SANDSTONE);

        builder.industrialFurnaceSmelting(Items.RED_SANDSTONE)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.SMOOTH_RED_SANDSTONE);

        // Cracked Bricks Recipes
        builder.industrialFurnaceSmelting(Items.STONE_BRICKS)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.CRACKED_STONE_BRICKS);

        builder.industrialFurnaceSmelting(Items.DEEPSLATE_BRICKS)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.CRACKED_DEEPSLATE_BRICKS);

        builder.industrialFurnaceSmelting(Items.DEEPSLATE_TILES)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.CRACKED_DEEPSLATE_TILES);

        builder.industrialFurnaceSmelting(Items.POLISHED_BLACKSTONE_BRICKS)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS);

        builder.industrialFurnaceSmelting(Items.NETHER_BRICKS)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.CRACKED_NETHER_BRICKS);

        // Terracotta Recipes - All colors
        builder.industrialFurnaceSmelting(Items.WHITE_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.WHITE_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.ORANGE_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.ORANGE_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.MAGENTA_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.MAGENTA_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.LIGHT_BLUE_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.LIGHT_BLUE_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.YELLOW_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.YELLOW_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.LIME_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.LIME_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.PINK_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.PINK_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.GRAY_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.GRAY_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.LIGHT_GRAY_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.LIGHT_GRAY_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.CYAN_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.CYAN_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.PURPLE_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.PURPLE_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.BLUE_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.BLUE_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.BROWN_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.BROWN_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.GREEN_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.GREEN_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.RED_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.RED_GLAZED_TERRACOTTA);

        builder.industrialFurnaceSmelting(Items.BLACK_TERRACOTTA)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.BLACK_GLAZED_TERRACOTTA);

        // Miscellaneous Recipes
        builder.industrialFurnaceSmelting(Items.WET_SPONGE)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.15f)
            .withCookingTime(100)
            .into(Items.SPONGE);

        builder.industrialFurnaceSmelting(Items.SAND)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.GLASS);

        builder.industrialFurnaceSmelting(Items.RED_SAND)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.GLASS);

        builder.industrialFurnaceSmelting(Items.MUD)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.1f)
            .withCookingTime(100)
            .into(Items.CLAY);

        builder.industrialFurnaceSmelting(Items.CLAY)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(0.35f)
            .withCookingTime(100)
            .into(Items.TERRACOTTA);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(9, AlienItems.ALIEN_MUSIC_DISC_1_FRAGMENT)
            .into(1, AlienItems.ALIEN_MUSIC_DISC_1);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(9, AVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT)
            .into(1, AVPItems.PREDATOR_MUSIC_DISC_1);
    }
}
