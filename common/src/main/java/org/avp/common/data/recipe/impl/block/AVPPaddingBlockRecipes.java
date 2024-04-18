package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.List;

import org.avp.api.Holder;
import org.avp.api.Tuple;
import org.avp.common.block.AVPPaddingBlocks;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;

public final class AVPPaddingBlockRecipes {

    private static final List<Tuple<ItemLike, List<Holder<Block>>>> PADDING_DATA = List.of(
        new Tuple<>(Items.BLACK_DYE, null),
        new Tuple<>(Items.BLUE_DYE, null),
        new Tuple<>(Items.BROWN_DYE, null),
        new Tuple<>(Items.BLUE_DYE, null),
        new Tuple<>(Items.CYAN_DYE, null),
        new Tuple<>(Items.GRAY_DYE, null),
        new Tuple<>(Items.GREEN_DYE, null),
        new Tuple<>(Items.LIGHT_BLUE_DYE, null),
        new Tuple<>(Items.LIGHT_GRAY_DYE, null),
        new Tuple<>(Items.LIME_DYE, null),
        new Tuple<>(Items.MAGENTA_DYE, null),
        new Tuple<>(
            Items.ORANGE_DYE,
            List.of(
                AVPPaddingBlocks.INSTANCE.paddingOrangePanel,
                AVPPaddingBlocks.INSTANCE.paddingOrangePipes,
                AVPPaddingBlocks.INSTANCE.paddingOrangeSquare,
                AVPPaddingBlocks.INSTANCE.paddingOrangeTiles
            )
        ),
        new Tuple<>(Items.PINK_DYE, null),
        new Tuple<>(Items.PURPLE_DYE, null),
        new Tuple<>(Items.RED_DYE, null),
        new Tuple<>(
            Items.WHITE_DYE,
            List.of(
                AVPPaddingBlocks.INSTANCE.paddingWhitePanel,
                AVPPaddingBlocks.INSTANCE.paddingWhitePipes,
                AVPPaddingBlocks.INSTANCE.paddingWhiteSquare,
                AVPPaddingBlocks.INSTANCE.paddingWhiteTiles
            )
        ),
        new Tuple<>(Items.YELLOW_DYE, null)
    );

    public static void addPaddingBlockRecipes(RecipeOutput recipeOutput) {
        PADDING_DATA.forEach(data -> {
            var dye = data.first();

            if (data.second() == null) {
                return;
            }

            var panel = data.second().get(0).get();
            var pipes = data.second().get(1).get();
            var square = data.second().get(2).get();
            var tiles = data.second().get(3).get();

            AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, panel)
                .defineAndUnlockIfHas('A', Items.LEATHER)
                .defineAndUnlockIfHas('B', ItemTags.WOOL)
                .defineAndUnlockIfHas('C', dye)
                .pattern(" AC")
                .pattern("ABA")
                .pattern(" A ")
                .save(recipeOutput);

            AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pipes, 2)
                .defineAndUnlockIfHas('A', panel)
                .defineAndUnlockIfHas('B', Items.IRON_NUGGET)
                .pattern("BA")
                .pattern("AB")
                .save(recipeOutput);

            AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, square, 4)
                .defineAndUnlockIfHas('A', panel)
                .pattern("AA")
                .pattern("AA")
                .save(recipeOutput);

            AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, tiles, 4)
                .defineAndUnlockIfHas('A', square)
                .pattern("AA")
                .pattern("AA")
                .save(recipeOutput);
        });
    }

    private AVPPaddingBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
