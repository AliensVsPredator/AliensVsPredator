package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.recipe.AVPRecipeBuilder;
import org.avp.common.data.tag.AVPBlockTags;
import org.avp.common.registry.item.AVPItemRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class PlasticBlockSetDataContainer extends ExtendedBlockDataContainer {

    private static final String REGISTRY_NAME_PREFIX = "plastic_";

    private static final BlockTagData BLOCK_TAG_DATA = BlockTagData.ofBlock(
        Set.of(AVPBlockTags.ACID_IMMUNE, BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE, AVPBlockTags.PLASTIC)
    );

    private final Map<DyeColor, ColoredPlasticBlockSet> COLORS_TO_PADDING_BLOCK_SET_MAP;

    protected PlasticBlockSetDataContainer() {
        Function<DyeColor, BlockBehaviour.Properties> propertiesSupplier = dyeColor -> BlockBehaviour.Properties.of()
            .mapColor(dyeColor)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
            .strength(9, 10);

        var map = new HashMap<DyeColor, ColoredPlasticBlockSet>();

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            var baseName = REGISTRY_NAME_PREFIX + dyeColor.getName();

            var base = this.addVariant(
                new SingleBlockDataContainer(
                    () -> new Block(propertiesSupplier.apply(dyeColor)),
                    baseName,
                    BlockModelData.NORMAL_CUBE,
                    BLOCK_TAG_DATA,
                    LootProviders.SELF
                )
            );

            var variantSet = this.addVariant(
                new VanillaVariantBlockDataContainer(base)
                    .withSlab()
                    .withStairs()
                    .withWall()
            );

            map.put(dyeColor, new ColoredPlasticBlockSet(dyeColor, base, variantSet));
        });

        COLORS_TO_PADDING_BLOCK_SET_MAP = Collections.unmodifiableMap(map);
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        COLORS_TO_PADDING_BLOCK_SET_MAP.forEach(((dyeColor, coloredPlasticBlockSet) -> {
            var dyeItem = DyeItem.byColor(dyeColor);

            if (dyeColor == DyeColor.GREEN) {
                builder.shape()
                    .withCategory(RecipeCategory.BUILDING_BLOCKS)
                    .define('A', AVPItemRegistry.INSTANCE.polymer.get())
                    .pattern("AA")
                    .pattern("AA")
                    .into(1, coloredPlasticBlockSet.base);
            }

            var ingredient = Ingredient.of(
                COLORS_TO_PADDING_BLOCK_SET_MAP.values().stream()
                    .filter(plasticBlockSet -> !plasticBlockSet.base().equals(coloredPlasticBlockSet.base))
                    .map(plasticBlockSet -> new ItemStack(plasticBlockSet.base())));

            builder.shapeless()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .requires('A', 1, dyeItem)
                .requires('B', 1, ingredient)
                .into(1, coloredPlasticBlockSet.base());

            coloredPlasticBlockSet.variantSet().createRecipes(recipeOutput);
        }));
    }

    private record ColoredPlasticBlockSet(
        DyeColor dyeColor,
        SingleBlockDataContainer.Holder base,
        VanillaVariantBlockDataContainer variantSet
    ) {}

    public static final PlasticBlockSetDataContainer INSTANCE = new PlasticBlockSetDataContainer();
}
