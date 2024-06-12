package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.registry.block.AVPBlockRegistry;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPFerroaluminumRecipes {

    public static void addFerroaluminumRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPBlockRegistry.INSTANCE.ferroaluminum.base())
            .defineAndUnlockIfHas('A', Items.IRON_INGOT)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.ingotAluminum)
            .pattern("BAB")
            .pattern("AAA")
            .pattern("BAB")
            .save(recipeOutput);

        AVPMetalRecipeHelper.addFullMetalSetRecipes(recipeOutput, BLHolder.empty(), AVPBlockRegistry.INSTANCE.ferroaluminum);
    }

    private AVPFerroaluminumRecipes() {
        throw new UnsupportedOperationException();
    }
}
