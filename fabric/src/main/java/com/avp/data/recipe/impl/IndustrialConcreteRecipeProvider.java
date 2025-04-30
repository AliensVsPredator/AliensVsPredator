package com.avp.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.BlockProperties;
import com.avp.data.recipe.builder.RecipeBuilder;
import com.avp.data.recipe.util.RecipeUtil;

public class IndustrialConcreteRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createIndustrialConcreteBlockRecipes(builder);
    }

    private static void createIndustrialConcreteBlockRecipes(RecipeBuilder builder) {
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(((dyeColor, block) -> {
            var dyeItem = DyeItem.byColor(dyeColor);

            builder.shapeless()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .requires(1, AVPBlocks.STEEL_BARS)
                .requires(1, BlockProperties.DYE_COLOR_TO_CONCRETE_BLOCKS.get(dyeColor))
                .into(1, block);

            var ingredient = Ingredient.of(
                AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values()
                    .stream()
                    .filter(industrialConcreteBlock -> !industrialConcreteBlock.equals(block))
                    .map(ItemStack::new)
            );

            builder.shapeless()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .requires(1, dyeItem)
                .requires(1, ingredient)
                .withCustomName((outputItem) -> "dye_" + outputItem)
                .into(1, block);

            var slabBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor);
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, block, slabBlock);

            var stairBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, block, stairBlock);

            var wallBlock = AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor);
            RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, block, wallBlock);
        }));
    }
}
