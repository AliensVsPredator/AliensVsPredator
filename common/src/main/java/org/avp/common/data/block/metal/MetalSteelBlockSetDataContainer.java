package org.avp.common.data.block.metal;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.RecipeCreator;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.block.VanillaVariantBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;

public class MetalSteelBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    public static final MetalSteelBlockSetDataContainer INSTANCE = new MetalSteelBlockSetDataContainer();

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_LIGHT_BLUE)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(10, 12);

    public final SingleBlockDataContainer.Holder base;

    private final SingleBlockDataContainer.Holder cut;

    private final VanillaVariantBlockDataContainer cutVariantSet;

    private final SingleBlockDataContainer.Holder grate;

    private final SingleBlockDataContainer.Holder plated;

    private final VanillaVariantBlockDataContainer platedVariantSet;

    private final SingleBlockDataContainer.Holder platedChevron;

    private final VanillaVariantBlockDataContainer platedChevronVariantSet;

    private final SingleBlockDataContainer.Holder platedStack;

    private final VanillaVariantBlockDataContainer platedStackVariantSet;

    private final SingleBlockDataContainer.Holder vent;

    protected MetalSteelBlockSetDataContainer() {
        this.base = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                "steel_block",
                BlockModelData.NORMAL_CUBE,
                BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)),
                LootProviders.SELF
            )
        );

        // Cut
        this.cut = this.addVariant(base.extend("steel_cut"));
        this.cutVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(cut)
                .withSlab()
                .withStairs()
                .withWall()
        );

        // Grate
        this.grate = this.addVariant(
            base.transform("steel_grate")
                .withSupplier(() -> new Block(BlockBehaviour.Properties.of().noOcclusion()))
                .withModelData(BlockModelData.TRANSPARENT_CUBE)
                .build()

        );

        // Plated
        this.plated = this.addVariant(base.extend("steel_plated"));
        this.platedVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(plated)
                .withSlab()
                .withStairs()
                .withWall()
        );

        // Plated Chevron
        this.platedChevron = this.addVariant(base.extend("steel_plated_chevron"));
        this.platedChevronVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(platedChevron)
                .withSlab()
                .withStairs()
                .withWall()
        );

        // Plated Stack
        this.platedStack = this.addVariant(base.extend("steel_plated_stack"));
        this.platedStackVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(platedStack)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.vent = this.addVariant(base.extend("steel_vent"));
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        // Ingots -> base block
        builder.shape()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPItemRegistry.INSTANCE.ingotSteel.get())
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .into(1, base);

        var stonecutBase = builder.stonecut(base)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        // 1 Base block -> 4 cut blocks
        builder.shape()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', base)
            .pattern("AA")
            .pattern("AA")
            .into(4, cut);

        stonecutBase.into(4, cut);
        stonecutBase.into(2, grate);
        stonecutBase.into(4, plated);
        stonecutBase.into(4, platedChevron);
        stonecutBase.into(4, platedStack);
        stonecutBase.into(1, vent);

        cutVariantSet.createRecipes(recipeOutput);
        platedVariantSet.createRecipes(recipeOutput);
        platedChevronVariantSet.createRecipes(recipeOutput);
        platedStackVariantSet.createRecipes(recipeOutput);
    }
}
