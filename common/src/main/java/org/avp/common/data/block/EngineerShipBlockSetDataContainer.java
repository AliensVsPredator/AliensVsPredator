package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.RecipeCreator;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;

public class EngineerShipBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    private static final String REGISTRY_NAME_PREFIX = "engineer_ship_";

    private static final BlockBehaviour.Properties METAL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
        .strength(100, 1800);

    private static final BlockTagData PICKAXE_TAGS = BlockTagData.ofBlock(
        Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
    );

    private static final BlockTagData SHOVEL_TAGS = BlockTagData.ofBlock(
        Set.of(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.NEEDS_DIAMOND_TOOL)
    );

    private final SingleBlockDataContainer.Holder base;

    private final VanillaVariantBlockDataContainer baseVariantSet;

    public final SingleBlockDataContainer.Holder brick1;

    private final VanillaVariantBlockDataContainer brick1VariantSet;

    public final SingleBlockDataContainer.Holder brick2;

    private final VanillaVariantBlockDataContainer brick2VariantSet;

    public final SingleBlockDataContainer.Holder brick3;

    private final VanillaVariantBlockDataContainer brick3VariantSet;

    public final SingleBlockDataContainer.Holder column1;

    public final SingleBlockDataContainer.Holder column2;

    public final SingleBlockDataContainer.Holder floor;

    public final SingleBlockDataContainer.Holder gravel;

    public final SingleBlockDataContainer.Holder material0;

    private final VanillaVariantBlockDataContainer material0VariantSet;

    public final SingleBlockDataContainer.Holder material1;

    private final VanillaVariantBlockDataContainer material1VariantSet;

    public final SingleBlockDataContainer.Holder material2;

    private final VanillaVariantBlockDataContainer material2VariantSet;

    public final SingleBlockDataContainer.Holder rock;

    public final SingleBlockDataContainer.Holder rock1;

    public final SingleBlockDataContainer.Holder rock2;

    public final SingleBlockDataContainer.Holder rock3;

    public final SingleBlockDataContainer.Holder wall;

    private final VanillaVariantBlockDataContainer wallVariantSet;

    public final SingleBlockDataContainer.Holder wall1;

    private final VanillaVariantBlockDataContainer wall1VariantSet;

    public final SingleBlockDataContainer.Holder wall2;

    private final VanillaVariantBlockDataContainer wall2VariantSet;

    public final SingleBlockDataContainer.Holder wall3;

    private final VanillaVariantBlockDataContainer wall3VariantSet;

    public final SingleBlockDataContainer.Holder wall4;

    private final VanillaVariantBlockDataContainer wall4VariantSet;

    protected EngineerShipBlockSetDataContainer() {
        this.base = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(METAL_PROPERTIES),
                REGISTRY_NAME_PREFIX + "brick",
                BlockModelData.NORMAL_CUBE,
                PICKAXE_TAGS,
                LootProviders.SELF
            )
        );

        // Variants
        this.baseVariantSet = createFullVanillaVariantSet(base);

        brick1 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "brick_1"));
        this.brick1VariantSet = createFullVanillaVariantSet(brick1);

        brick2 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "brick_2"));
        this.brick2VariantSet = createFullVanillaVariantSet(brick2);

        brick3 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "brick_3"));
        this.brick3VariantSet = createFullVanillaVariantSet(brick3);

        this.column1 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "column_1"));
        this.column2 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "column_2"));

        this.floor = this.addVariant(
            base.transform(REGISTRY_NAME_PREFIX + "floor")
                .withBlockTagData(SHOVEL_TAGS)
                .build()

        );
        this.gravel = this.addVariant(
            base.transform(REGISTRY_NAME_PREFIX + "gravel")
                .withBlockTagData(SHOVEL_TAGS)
                .build()

        );

        this.material0 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "material_0"));
        this.material0VariantSet = createFullVanillaVariantSet(material0);

        this.material1 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "material_1"));
        this.material1VariantSet = createFullVanillaVariantSet(material1);

        this.material2 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "material_2"));
        this.material2VariantSet = createFullVanillaVariantSet(material2);

        this.rock = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "rock"));
        this.rock1 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "rock_1"));
        this.rock2 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "rock_2"));
        this.rock3 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "rock_3"));

        this.wall = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "wall"));
        this.wallVariantSet = createFullVanillaVariantSet(wall);

        this.wall1 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "wall_1"));
        this.wall1VariantSet = createFullVanillaVariantSet(wall1);

        this.wall2 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "wall_2"));
        this.wall2VariantSet = createFullVanillaVariantSet(wall2);

        this.wall3 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "wall_3"));
        this.wall3VariantSet = createFullVanillaVariantSet(wall3);

        this.wall4 = this.addVariant(base.extend(REGISTRY_NAME_PREFIX + "wall_4"));
        this.wall4VariantSet = createFullVanillaVariantSet(wall4);
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        baseVariantSet.createRecipes(recipeOutput);
        brick1VariantSet.createRecipes(recipeOutput);
        brick2VariantSet.createRecipes(recipeOutput);
        brick3VariantSet.createRecipes(recipeOutput);
        material0VariantSet.createRecipes(recipeOutput);
        material1VariantSet.createRecipes(recipeOutput);
        material2VariantSet.createRecipes(recipeOutput);
        wallVariantSet.createRecipes(recipeOutput);
        wall1VariantSet.createRecipes(recipeOutput);
        wall2VariantSet.createRecipes(recipeOutput);
        wall3VariantSet.createRecipes(recipeOutput);
        wall4VariantSet.createRecipes(recipeOutput);
    }

    private @NotNull VanillaVariantBlockDataContainer createFullVanillaVariantSet(
        SingleBlockDataContainer.Holder singleBlockDataContainer
    ) {
        return this.addVariant(
            new VanillaVariantBlockDataContainer(singleBlockDataContainer)
                .withSlab()
                .withStairs()
                .withWall()
        );
    }

    public static final EngineerShipBlockSetDataContainer INSTANCE = new EngineerShipBlockSetDataContainer();
}
