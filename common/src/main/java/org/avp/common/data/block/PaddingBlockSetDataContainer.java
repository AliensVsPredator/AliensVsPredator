package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.RecipeCreator;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.recipe.AVPRecipeBuilder;
import org.avp.common.data.tag.AVPBlockTags;

public class PaddingBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    private static final String REGISTRY_NAME_PREFIX = "padding_";

    private final Map<DyeColor, ColoredPaddingBlockSet> COLORS_TO_PADDING_BLOCK_SET_MAP;

    protected PaddingBlockSetDataContainer() {
        var map = new HashMap<DyeColor, ColoredPaddingBlockSet>();

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                .mapColor(dyeColor);
            var baseName = REGISTRY_NAME_PREFIX + dyeColor.getName();

            var base = new SingleBlockDataContainer(
                () -> new Block(properties),
                baseName,
                BlockModelData.NORMAL_CUBE,
                BlockTagData.ofBlock(Set.of(AVPBlockTags.PADDING)),
                LootProviders.SELF
            );

            var panel = this.addVariant(base.extend(baseName + "_panel"));

            var pipes = this.addVariant(base.extend(baseName + "_pipes"));

            var square = this.addVariant(base.extend(baseName + "_square"));

            var squareVariants = this.addVariant(
                new VanillaVariantBlockDataContainer(square)
                    .withSlab()
                    .withStairs()
                    .withWall()
            );

            var tile = this.addVariant(base.extend(baseName + "_tiles"));

            var tileVariants = this.addVariant(
                new VanillaVariantBlockDataContainer(tile)
                    .withSlab()
                    .withStairs()
                    .withWall()
            );

            var set = new ColoredPaddingBlockSet(
                dyeColor,
                panel,
                pipes,
                square,
                squareVariants,
                tile,
                tileVariants
            );

            map.put(dyeColor, set);
        });

        COLORS_TO_PADDING_BLOCK_SET_MAP = Collections.unmodifiableMap(map);
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        COLORS_TO_PADDING_BLOCK_SET_MAP.forEach(((dyeColor, coloredPaddingBlockSet) -> {
            var dyeItem = DyeItem.byColor(dyeColor);

            // Panel recipe
            builder.shapeless()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .requires(1, Items.LEATHER)
                .requires(1, ItemTags.WOOL)
                .requires(1, dyeItem)
                .into(1, coloredPaddingBlockSet.panel());

            // Pipes recipe
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', coloredPaddingBlockSet.panel())
                .define('B', Items.IRON_NUGGET)
                .pattern("BA")
                .pattern("AB")
                .into(2, coloredPaddingBlockSet.pipes());

            // Square recipe
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', coloredPaddingBlockSet.panel())
                .pattern("AA")
                .pattern("AA")
                .into(4, coloredPaddingBlockSet.square());

            // Tile recipe
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', coloredPaddingBlockSet.square())
                .pattern("AA")
                .pattern("AA")
                .into(4, coloredPaddingBlockSet.tiles());

            coloredPaddingBlockSet.squareVariants().createRecipes(recipeOutput);
            coloredPaddingBlockSet.tileVariants().createRecipes(recipeOutput);
        }));
    }

    private record ColoredPaddingBlockSet(
        DyeColor dyeColor,
        SingleBlockDataContainer.Holder panel,
        SingleBlockDataContainer.Holder pipes,
        SingleBlockDataContainer.Holder square,
        VanillaVariantBlockDataContainer squareVariants,
        SingleBlockDataContainer.Holder tiles,
        VanillaVariantBlockDataContainer tileVariants
    ) {}

    public static final PaddingBlockSetDataContainer INSTANCE = new PaddingBlockSetDataContainer();
}
