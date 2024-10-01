package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockModelDataType;
import org.avp.api.common.data.block.BlockModelRenderType;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.RecipeCreator;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.recipe.AVPRecipeBuilder;
import org.avp.common.data.tag.AVPItemTags;
import org.avp.common.registry.item.AVPItemRegistry;

public class IndustrialBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    private static final String REGISTRY_NAME_PREFIX = "industrial_";

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK);

    private static final BlockTagData PICKAXE_TAGS = BlockTagData.ofBlock(
        Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
    );

    public final SingleBlockDataContainer.Holder wall;

    public final SingleBlockDataContainer.Holder wallHazard;

    public final VanillaVariantBlockDataContainer wallVariantSet;

    protected IndustrialBlockSetDataContainer() {
        super();

        this.wall = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                REGISTRY_NAME_PREFIX + "wall",
                BlockModelData.NORMAL_CUBE,
                PICKAXE_TAGS,
                LootProviders.SELF
            )
        );

        this.wallVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(wall)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.wallHazard = this.addVariant(
            wall.transform(REGISTRY_NAME_PREFIX + "wall_hazard")
                .withSupplier(() -> new RotatedPillarBlock(PROPERTIES))
                .withModelData(
                    new BlockModelData(
                        BlockModelDataType.RotatedPillar::new,
                        BlockModelRenderType.NORMAL
                    )
                )
                .build()
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        builder.shape()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPItemTags.CONCRETE)
            .define('B', AVPItemRegistry.INSTANCE.ingotSteel.get())
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .into(8, wall);

        wallVariantSet.createRecipes(recipeOutput);
    }

    public static final IndustrialBlockSetDataContainer INSTANCE = new IndustrialBlockSetDataContainer();
}
