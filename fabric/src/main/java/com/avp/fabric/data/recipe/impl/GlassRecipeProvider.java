package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

import com.avp.core.common.block.AVPBlocks;
import com.avp.fabric.data.recipe.RecipeConstants;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class GlassRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.blast(Blocks.GLASS)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(AVPBlocks.INDUSTRIAL_GLASS);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPBlocks.INDUSTRIAL_GLASS)
            .pattern("AAA")
            .pattern("AAA")
            .into(16, AVPBlocks.INDUSTRIAL_GLASS_PANE);

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach((dyeColor, block) -> {
            var dyeItem = DyeItem.byColor(dyeColor);

            // Industrial glass combined with dyes creates colored industrial glass.
            builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', AVPBlocks.INDUSTRIAL_GLASS)
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
                .into(16, AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor));

            // All colored industrial glass blocks can be blasted again to remove dyed colors.
            builder.blast(block)
                .withCategory(RecipeCategory.MISC)
                .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
                .into(AVPBlocks.INDUSTRIAL_GLASS);
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
            .into(AVPBlocks.INDUSTRIAL_GLASS)
        );
    }
}
