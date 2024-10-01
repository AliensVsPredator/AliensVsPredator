package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
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
import org.avp.common.data.recipe.AVPRecipeBuilder;

public class TempleBlockDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    private static final String REGISTRY_NAME_PREFIX = "temple_";

    private static final BlockBehaviour.Properties STONE_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(3, 6);

    private static final BlockBehaviour.Properties SKULL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK)
        .strength(3, 6);

    private static final BlockTagData PICKAXE_TAGS = BlockTagData.ofBlock(
        Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
    );

    public final SingleBlockDataContainer.Holder base;

    private final VanillaVariantBlockDataContainer baseVariantSet;

    private final SingleBlockDataContainer.Holder brickChestburster;

    private final SingleBlockDataContainer.Holder brickFacehugger;

    private final SingleBlockDataContainer.Holder brickSingle;

    private final VanillaVariantBlockDataContainer brickSingleVariantSet;

    private final SingleBlockDataContainer.Holder floor;

    private final VanillaVariantBlockDataContainer floorVariantSet;

    private final SingleBlockDataContainer.Holder skulls;

    private final SingleBlockDataContainer.Holder tile;

    private final VanillaVariantBlockDataContainer tileVariantSet;

    private final SingleBlockDataContainer.Holder wallBase;

    protected TempleBlockDataContainer() {
        this.base = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(STONE_PROPERTIES),
                REGISTRY_NAME_PREFIX + "brick",
                BlockModelData.NORMAL_CUBE,
                PICKAXE_TAGS,
                LootProviders.SELF
            )
        );

        this.baseVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(base)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.brickChestburster = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "brick_chestburster"));
        this.brickFacehugger = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "brick_facehugger"));

        this.brickSingle = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "brick_single"));

        this.brickSingleVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(brickSingle)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.floor = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "floor"));

        this.floorVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(floor)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.skulls = this.addVariant(
            base.transform(REGISTRY_NAME_PREFIX + "skulls")
                .withSupplier(() -> new Block(SKULL_PROPERTIES))
                .build()
        );

        this.tile = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "tile"));

        this.tileVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(tile)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.wallBase = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "wall_base"));
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        builder.shape()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', Items.SKELETON_SKULL)
            .pattern("AA")
            .pattern("AA")
            .into(1, skulls);

        var stonecut = builder.stonecut(base);

        stonecut.into(1, brickChestburster);
        stonecut.into(1, brickFacehugger);
        stonecut.into(1, wallBase);

        builder.stonecut(brickSingle)
            .into(1, wallBase);

        builder.blast(Items.DEEPSLATE)
            .withCategory(RecipeCategory.MISC)
            .withCookTime(100)
            .into(floor);

        var floorStonecut = builder.stonecut(floor);
        floorStonecut.into(1, wallBase);
        floorStonecut.into(1, brickSingle);
        floorStonecut.into(1, base);
        floorStonecut.into(1, tile);

        baseVariantSet.createRecipes(recipeOutput);
        brickSingleVariantSet.createRecipes(recipeOutput);
        floorVariantSet.createRecipes(recipeOutput);
        tileVariantSet.createRecipes(recipeOutput);
    }

    public static final TempleBlockDataContainer INSTANCE = new TempleBlockDataContainer();
}
