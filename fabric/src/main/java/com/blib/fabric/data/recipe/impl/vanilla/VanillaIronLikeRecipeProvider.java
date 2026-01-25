package com.blib.fabric.data.recipe.impl.vanilla;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import com.blib.api.common.tag.v1.BLibItemTags;
import com.blib.api.common.tag.v1.CommonItemTags;
import com.blib.fabric.data.recipe.builder.RecipeBuilder;

public class VanillaIronLikeRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createIronLikeRecipes(builder);
    }

    private static void createIronLikeRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.TRANSPORTATION)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('R', Items.REDSTONE_TORCH)
            .define('S', CommonItemTags.RODS_WOODEN)
            .pattern("ISI")
            .pattern("IRI")
            .pattern("ISI")
            .into(6, Blocks.ACTIVATOR_RAIL);

        builder.shaped()
            .withCategory(RecipeCategory.DECORATIONS)
            .define('B', BLibItemTags.IRON_BLOCK_LIKE)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .pattern("BBB")
            .pattern(" I ")
            .pattern("III")
            .into(1, Blocks.ANVIL);

        builder.shaped()
            .withCategory(RecipeCategory.DECORATIONS)
            .define('F', Blocks.FURNACE)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('S', Blocks.SMOOTH_STONE)
            .pattern("III")
            .pattern("IFI")
            .pattern("SSS")
            .into(1, Blocks.BLAST_FURNACE);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .pattern("I I")
            .pattern(" I ")
            .into(1, Items.BUCKET);

        builder.shaped()
            .withCategory(RecipeCategory.BREWING)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .pattern("I I")
            .pattern("I I")
            .pattern("III")
            .into(1, Blocks.CAULDRON);

        builder.shaped()
            .withCategory(RecipeCategory.REDSTONE)
            .define('C', Blocks.CRAFTING_TABLE)
            .define('D', Blocks.DROPPER)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('R', CommonItemTags.DUSTS_REDSTONE)
            .pattern("III")
            .pattern("ICI")
            .pattern("RDR")
            .into(1, Blocks.CRAFTER);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('S', CommonItemTags.STRINGS)
            .define('T', CommonItemTags.RODS_WOODEN)
            .define('W', Blocks.TRIPWIRE_HOOK)
            .pattern("TIT")
            .pattern("SWS")
            .pattern(" S ")
            .into(1, Items.CROSSBOW);

        builder.shaped()
            .withCategory(RecipeCategory.TRANSPORTATION)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('P', Blocks.STONE_PRESSURE_PLATE)
            .define('R', CommonItemTags.DUSTS_REDSTONE)
            .pattern("I I")
            .pattern("IPI")
            .pattern("IRI")
            .into(6, Blocks.DETECTOR_RAIL);

        builder.shapeless()
            .withCategory(RecipeCategory.TOOLS)
            .requires(1, BLibItemTags.IRON_INGOT_LIKE)
            .requires(1, Items.FLINT)
            .into(1, Items.FLINT_AND_STEEL);

        builder.shaped()
            .withCategory(RecipeCategory.REDSTONE)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('C', Items.CHEST)
            .pattern("I I")
            .pattern("ICI")
            .pattern(" I ")
            .into(1, Blocks.HOPPER);

        builder.shaped()
            .withCategory(RecipeCategory.TRANSPORTATION)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .pattern("I I")
            .pattern("III")
            .into(1, Items.MINECART);

        builder.shaped()
            .withCategory(RecipeCategory.REDSTONE)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('C', Items.COBBLESTONE)
            .define('P', ItemTags.PLANKS)
            .define('R', CommonItemTags.DUSTS_REDSTONE)
            .pattern("PPP")
            .pattern("CIC")
            .pattern("CRC")
            .into(1, Blocks.PISTON);

        builder.shaped()
            .withCategory(RecipeCategory.TRANSPORTATION)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('S', CommonItemTags.RODS_WOODEN)
            .pattern("I I")
            .pattern("ISI")
            .pattern("I I")
            .into(16, Blocks.RAIL);

        builder.shaped()
            .withCategory(RecipeCategory.TOOLS)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .pattern(" I")
            .pattern("I ")
            .into(1, Items.SHEARS);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('P', ItemTags.PLANKS)
            .pattern("PIP")
            .pattern("PPP")
            .pattern(" P ")
            .into(1, Items.SHIELD);

        builder.shaped()
            .withCategory(RecipeCategory.DECORATIONS)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('P', ItemTags.PLANKS)
            .pattern("II")
            .pattern("PP")
            .pattern("PP")
            .into(1, Blocks.SMITHING_TABLE);

        builder.shaped()
            .withCategory(RecipeCategory.DECORATIONS)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('S', Blocks.STONE)
            .pattern(" I ")
            .pattern("SSS")
            .into(1, Blocks.STONECUTTER);

        builder.shaped()
            .withCategory(RecipeCategory.REDSTONE)
            .define('I', BLibItemTags.IRON_INGOT_LIKE)
            .define('P', ItemTags.PLANKS)
            .define('S', CommonItemTags.RODS_WOODEN)
            .pattern("I")
            .pattern("S")
            .pattern("P")
            .into(2, Blocks.TRIPWIRE_HOOK);
    }
}
