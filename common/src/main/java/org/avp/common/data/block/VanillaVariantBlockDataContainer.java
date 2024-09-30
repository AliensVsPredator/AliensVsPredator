package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.WoodType;

import org.avp.api.common.data.block.BlockDataUtils;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeBuilder;

public class VanillaVariantBlockDataContainer extends ExtendedBlockDataContainer {

    private final SingleBlockDataContainer.Holder base;

    private SingleBlockDataContainer.Holder fence;

    private SingleBlockDataContainer.Holder fenceGate;

    private SingleBlockDataContainer.Holder slab;

    private SingleBlockDataContainer.Holder stairs;

    private SingleBlockDataContainer.Holder wall;

    public VanillaVariantBlockDataContainer(SingleBlockDataContainer.Holder base) {
        this.base = base;
    }

    public final VanillaVariantBlockDataContainer withFence() {
        this.fence = this.addVariant(BlockDataUtils.intoFence(base));
        return this;
    }

    public final VanillaVariantBlockDataContainer withFenceGate(WoodType woodType) {
        this.fenceGate = this.addVariant(BlockDataUtils.intoFenceGate(woodType, base));
        return this;
    }

    public final VanillaVariantBlockDataContainer withSlab() {
        this.slab = this.addVariant(BlockDataUtils.intoSlab(base));
        return this;
    }

    public final VanillaVariantBlockDataContainer withStairs() {
        this.stairs = this.addVariant(BlockDataUtils.intoStairs(base));
        return this;
    }

    public final VanillaVariantBlockDataContainer withWall() {
        this.wall = this.addVariant(BlockDataUtils.intoWall(base));
        return this;
    }

    public final void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        // Fence
        if (fence != null) {
            // TODO:
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', base)
                .define('B', Items.STICK)
                .pattern("ABA")
                .pattern("ABA")
                .into(3, fence);
        }

        // Fence Gate
        if (fenceGate != null) {
            // TODO:
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', Items.STICK)
                .define('B', base)
                .pattern("ABA")
                .pattern("ABA")
                .into(1, fenceGate);
        }

        var stonecut = builder.stonecut(base)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        // Slab
        if (slab != null) {
            stonecut.into(2, slab);
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', base)
                .pattern("AAA")
                .into(6, slab);
        }

        // Stairs
        if (stairs != null) {
            stonecut.into(1, stairs);
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', base)
                .pattern("A  ")
                .pattern("AA ")
                .pattern("AAA")
                .into(4, stairs);
        }

        // Wall
        if (wall != null) {
            stonecut.into(1, wall);
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', base)
                .pattern("AAA")
                .pattern("AAA")
                .into(6, wall);
        }
    }
}
