package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import org.avp.api.Holder;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPItems;

public final class AVPFerroaluminumRecipes {

    public static void addFerroaluminumRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPBlocks.INSTANCE.ferroaluminum.base())
            .defineAndUnlockIfHas('A', Items.IRON_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.ingotAluminum)
            .pattern("BAB")
            .pattern("AAA")
            .pattern("BAB")
            .save(recipeOutput);

        AVPMetalRecipeHelper.addFullMetalSetRecipes(recipeOutput, Holder.empty(), AVPBlocks.INSTANCE.ferroaluminum);
    }

    private AVPFerroaluminumRecipes() {
        throw new UnsupportedOperationException();
    }
}
