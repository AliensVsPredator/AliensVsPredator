package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;

public class YautjaShipBlockSetDataContainer extends SingleBlockDataContainer.Holder {

    private static final String REGISTRY_NAME_PREFIX = "yautja_ship_";

    private static final BlockBehaviour.Properties METAL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
        .mapColor(MapColor.COLOR_RED)
        .strength(75, 1500);

    private static final BlockBehaviour.Properties DECOR_1_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_RED)
        .strength(75, 1500)
        .lightLevel($0 -> 10);

    private static final BlockBehaviour.Properties DECOR_2_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_RED)
        .strength(75, 1500)
        .lightLevel(($0) -> 8);

    private static final BlockBehaviour.Properties PANEL_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_RED)
        .strength(75, 1500)
        .lightLevel(($0) -> 6);

    private static final BlockBehaviour.Properties WALL_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_RED)
        .strength(75, 1500)
        .lightLevel(($0) -> 4);

    private static final BlockTagData PICKAXE_TAGS = BlockTagData.ofBlock(
        Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
    );

    private final VanillaVariantBlockDataContainer baseVariantSet;

    private final VanillaVariantBlockDataContainer decor3VariantSet;

    protected YautjaShipBlockSetDataContainer() {
        super(
            () -> new Block(METAL_PROPERTIES),
            REGISTRY_NAME_PREFIX + "brick",
            BlockModelData.NORMAL_CUBE,
            PICKAXE_TAGS,
            LootProviders.SELF
        );

        // Variants
        this.baseVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(this)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.addVariant(
            this.transform(REGISTRY_NAME_PREFIX + "decor_1")
                .withSupplier(() -> new Block(DECOR_1_PROPERTIES))
                .build()
        );

        this.addVariant(
            this.transform(REGISTRY_NAME_PREFIX + "decor_2")
                .withSupplier(() -> new Block(DECOR_2_PROPERTIES))
                .build()
        );

        var decor3 = this.addVariant(this.extend(REGISTRY_NAME_PREFIX + "decor_3"));

        this.decor3VariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(decor3)
                .withSlab()
                .withStairs()
                .withWall()
        );

        this.addVariant(
            this.transform(REGISTRY_NAME_PREFIX + "panel")
                .withSupplier(() -> new Block(PANEL_PROPERTIES))
                .build()
        );

        this.addVariant(this.extend(REGISTRY_NAME_PREFIX + "support_pillar"));

        this.addVariant(
            this.transform(REGISTRY_NAME_PREFIX + "wall_base")
                .withSupplier(() -> new Block(WALL_PROPERTIES))
                .build()
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        baseVariantSet.createRecipes(recipeOutput);
        decor3VariantSet.createRecipes(recipeOutput);
    }

    public static final YautjaShipBlockSetDataContainer INSTANCE = new YautjaShipBlockSetDataContainer();
}
