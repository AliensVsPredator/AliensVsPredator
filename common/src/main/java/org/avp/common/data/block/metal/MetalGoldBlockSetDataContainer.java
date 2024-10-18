package org.avp.common.data.block.metal;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.RecipeCreator;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.block.VanillaVariantBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeBuilder;

public class MetalGoldBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    public static final MetalGoldBlockSetDataContainer INSTANCE = new MetalGoldBlockSetDataContainer();

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK);

    private final SingleBlockDataContainer.Holder grate;

    private final SingleBlockDataContainer.Holder plated;

    private final VanillaVariantBlockDataContainer platedVariantSet;

    private final SingleBlockDataContainer.Holder platedChevron;

    private final VanillaVariantBlockDataContainer platedChevronVariantSet;

    private final SingleBlockDataContainer.Holder platedStack;

    private final VanillaVariantBlockDataContainer platedStackVariantSet;

    private final SingleBlockDataContainer.Holder vent;

    protected MetalGoldBlockSetDataContainer() {
        var base = new SingleBlockDataContainer(
            () -> new Block(PROPERTIES),
            "gold_block",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)),
            LootProviders.SELF
        );

        // Grate
        this.grate = this.addVariant(
            base.transform("gold_grate")
                .withSupplier(() -> new Block(BlockBehaviour.Properties.of().noOcclusion()))
                .withModelData(BlockModelData.TRANSPARENT_CUBE)
                .build()

        );

        // Plated
        this.plated = this.addVariant(base.extend("gold_plated"));
        this.platedVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(plated)
                .withSlab()
                .withStairs()
                .withWall()
        );

        // Plated Chevron
        this.platedChevron = this.addVariant(base.extend("gold_plated_chevron"));
        this.platedChevronVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(platedChevron)
                .withSlab()
                .withStairs()
                .withWall()
        );

        // Plated Stack
        this.platedStack = this.addVariant(base.extend("gold_plated_stack"));
        this.platedStackVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(platedStack)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.vent = this.addVariant(base.extend("gold_vent"));
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        var stonecutBase = builder.stonecut(Blocks.GOLD_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        stonecutBase.into(2, grate);
        stonecutBase.into(4, plated);
        stonecutBase.into(4, platedChevron);
        stonecutBase.into(4, platedStack);
        stonecutBase.into(1, vent);

        platedVariantSet.createRecipes(recipeOutput);
        platedChevronVariantSet.createRecipes(recipeOutput);
        platedStackVariantSet.createRecipes(recipeOutput);
    }
}
