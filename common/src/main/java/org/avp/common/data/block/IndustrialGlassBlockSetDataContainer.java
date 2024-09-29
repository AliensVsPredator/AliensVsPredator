package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
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
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.api.common.game.block.ColoredTransparentBlock;
import org.avp.api.common.game.block.CustomTransparentBlock;
import org.avp.common.data.recipe.AVPRecipeBuilder;
import org.avp.common.data.tag.AVPBlockTags;
import org.avp.common.data.tag.AVPItemTags;

public class IndustrialGlassBlockSetDataContainer extends ExtendedBlockDataContainer {

    private static final String REGISTRY_NAME = "industrial_glass";

    private static final BlockTagData BLOCK_TAG_DATA = new BlockTagData(
        Set.of(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE, AVPBlockTags.INDUSTRIAL_GLASS),
        Set.of(AVPItemTags.INDUSTRIAL_GLASS)
    );

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS);

    public final SingleBlockDataContainer.Holder base;

    private final Map<DyeColor, SingleBlockDataContainer.Holder> COLORS_TO_INDUSTRIAL_GLASS_HOLDER_MAP;

    protected IndustrialGlassBlockSetDataContainer() {
        var map = new HashMap<DyeColor, SingleBlockDataContainer.Holder>();

        this.base = this.addVariant(
            new SingleBlockDataContainer(
                () -> new CustomTransparentBlock(PROPERTIES),
                REGISTRY_NAME,
                BlockModelData.TRANSPARENT_CUBE,
                BLOCK_TAG_DATA,
                LootProviders.SELF
            )
        );

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            var dyedIndustrialGlassName = REGISTRY_NAME + "_" + dyeColor.getName();

            var dyedGlass = this.addVariant(
                new SingleBlockDataContainer(
                    () -> new ColoredTransparentBlock(dyeColor, PROPERTIES),
                    dyedIndustrialGlassName,
                    BlockModelData.TRANSPARENT_CUBE,
                    BLOCK_TAG_DATA,
                    LootProviders.SELF
                )
            );

            map.put(dyeColor, dyedGlass);
        });

        COLORS_TO_INDUSTRIAL_GLASS_HOLDER_MAP = Collections.unmodifiableMap(map);
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        builder.blast(Blocks.GLASS)
            .withCategory(RecipeCategory.MISC)
            .withCookTime(100)
            .withExperience(0.7F)
            .into(base);

        COLORS_TO_INDUSTRIAL_GLASS_HOLDER_MAP.forEach((dyeColor, containerWithHolder) -> {
            var coloredIndustrialGlassBlock = containerWithHolder.getHolder().get();
            var dyeItem = DyeItem.byColor(dyeColor);

            // Industrial glass combined with dyes creates colored industrial glass.
            builder.shape()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', base)
                .define('B', dyeItem)
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .into(8, coloredIndustrialGlassBlock);

            // All colored industrial glass blocks can be blasted again to remove dyed colors.
            builder.blast(coloredIndustrialGlassBlock)
                .withCategory(RecipeCategory.MISC)
                .withExperience(0)
                .withCookTime(100)
                .into(base);
        });

        createStainedGlassBlastingRecipes(builder);
    }

    private void createStainedGlassBlastingRecipes(AVPRecipeBuilder builder) {
        var stainedGlassBlocks = Set.of(
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

        stainedGlassBlocks.forEach(stainedGlassBlock ->
        // All stained-glass blocks can be blasted again to remove dyed colors.
        builder.blast(stainedGlassBlock)
            .withCategory(RecipeCategory.MISC)
            .withExperience(0.7F)
            .withCookTime(100)
            .into(base)
        );
    }

    public static final IndustrialGlassBlockSetDataContainer INSTANCE = new IndustrialGlassBlockSetDataContainer();
}
