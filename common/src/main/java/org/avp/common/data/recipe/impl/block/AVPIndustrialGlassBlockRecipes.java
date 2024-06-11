package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.avp.common.AVPConstants;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.block.AVPIndustrialGlassBlockRegistry;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;

import java.util.Set;

public final class AVPIndustrialGlassBlockRecipes {

    private static final Set<Block> STAINED_GLASS_BLOCKS = Set.of(
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

    public static void addIndustrialGlassBlockRecipes(RecipeOutput recipeOutput) {
        // Industrial glass originates from blasting regular glass.
        var industrialGlassOutput = AVPIndustrialGlassBlockRegistry.INSTANCE.glass.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.GLASS), RecipeCategory.MISC, industrialGlassOutput, 0.7F, 100)
            .unlockedBy("has_glass", AVPRecipeProvider.has(Items.GLASS))
            .save(recipeOutput, AVPConstants.MOD_ID + ":industrial_glass_from_blasting_glass");

        // Generate recipes for every colored industrial glass block.
        AVPIndustrialGlassBlockRegistry.getColoredIndustrialGlassEntries().forEach(coloredIndustrialGlassHolder -> {
            var coloredIndustrialGlassName = coloredIndustrialGlassHolder.holder().getResourceLocation().getPath();
            var coloredIndustrialGlassBlock = coloredIndustrialGlassHolder.holder().get();

            // Industrial glass combined with dyes creates colored industrial glass.
            AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, coloredIndustrialGlassBlock, 8)
                .defineAndUnlockIfHas('A', industrialGlassOutput)
                .defineAndUnlockIfHas('B', DyeItem.byColor(coloredIndustrialGlassHolder.dyeColor()))
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .save(recipeOutput);

            // All colored industrial glass blocks can be blasted again to remove dyed colors.
            SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(coloredIndustrialGlassBlock),
                RecipeCategory.MISC,
                industrialGlassOutput,
                0,
                100
            )
                .unlockedBy("has_industrial_glass", AVPRecipeProvider.has(industrialGlassOutput))
                .save(recipeOutput, AVPConstants.MOD_ID + ":industrial_glass_from_blasting_" + coloredIndustrialGlassName);
        });

        STAINED_GLASS_BLOCKS.forEach(stainedGlassBlock ->
            // All stained glass blocks can be blasted again to remove dyed colors.
            SimpleCookingRecipeBuilder.blasting(
                    Ingredient.of(stainedGlassBlock),
                    RecipeCategory.MISC,
                    industrialGlassOutput,
                    0,
                    100
                )
                .unlockedBy("has_industrial_glass", AVPRecipeProvider.has(industrialGlassOutput))
                .save(recipeOutput, AVPConstants.MOD_ID + ":industrial_glass_from_blasting_" + AVPRecipeHelper.getItemName(stainedGlassBlock))
        );
    }

    private AVPIndustrialGlassBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
