package com.avp.fabric.data.recipe.impl;

import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

import com.avp.fabric.data.recipe.RecipeConstants;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class GlassRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.blast(Blocks.GLASS)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS)
            .pattern("AAA")
            .pattern("AAA")
            .into(16, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_PANE);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS.get()))
            .into(3, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS.get()))
            .into(2, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);

        // Add standard slab and stair crafting recipes
        createStandardSlabRecipe(
            builder,
            HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS.get(),
            HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB.get()
        );
        createStandardStairRecipe(
            builder,
            HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS.get(),
            HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS.get()
        );

        createIndustrialGlassBlockVariantRecipes(builder);

        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach((dyeColor, blockSupplier) -> {
            var block = blockSupplier.get();
            var dyeItem = DyeItem.byColor(dyeColor);

            // Industrial glass combined with dyes creates colored industrial glass.
            builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS)
                .define('B', dyeItem)
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .into(8, block);

            builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', block)
                .pattern("AAA")
                .pattern("AAA")
                .into(16, HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor).get());

            // All colored industrial glass blocks can be blasted again to remove dyed colors.
            builder.blast(block)
                .withCategory(RecipeCategory.MISC)
                .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
                .into(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS);
        });

        createStainedGlassBlastingRecipes(builder);
    }

    private static void createStainedGlassBlastingRecipes(RecipeBuilder builder) {
        // TODO:
        var stainedGlassBlocks = Set.of(
            Blocks.BLACK_STAINED_GLASS,
            Blocks.BLUE_STAINED_GLASS,
            Blocks.BROWN_STAINED_GLASS,
            Blocks.CYAN_STAINED_GLASS,
            Blocks.GRAY_STAINED_GLASS,
            Blocks.GREEN_STAINED_GLASS,
            Blocks.LIGHT_BLUE_STAINED_GLASS,
            Blocks.LIGHT_GRAY_STAINED_GLASS,
            Blocks.LIME_STAINED_GLASS,
            Blocks.MAGENTA_STAINED_GLASS,
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.PINK_STAINED_GLASS,
            Blocks.PURPLE_STAINED_GLASS,
            Blocks.RED_STAINED_GLASS,
            Blocks.WHITE_STAINED_GLASS,
            Blocks.YELLOW_STAINED_GLASS
        );

        stainedGlassBlocks.forEach(stainedGlassBlock ->
        // All stained-glass blocks can be blasted again to remove dyed colors.
        builder.blast(stainedGlassBlock)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS)
        );
    }

    private static void createIndustrialGlassBlockVariantRecipes(RecipeBuilder builder) {
        var industrialGlassBaseBuilder = builder.stonecut(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        industrialGlassBaseBuilder.into(2, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB);
        industrialGlassBaseBuilder.into(1, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS);
    }

    // TODO: Duplicate function, unify at some point.
    private static void createStandardSlabRecipe(RecipeBuilder builder, ItemLike input, ItemLike output) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.SLAB_BLOCK.apply(input))
            .into(6, output);
    }

    // TODO: Duplicate function, unify at some point.
    private static void createStandardStairRecipe(RecipeBuilder builder, ItemLike input, ItemLike output) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.STAIR_BLOCK.apply(input))
            .into(4, output);
    }
}
