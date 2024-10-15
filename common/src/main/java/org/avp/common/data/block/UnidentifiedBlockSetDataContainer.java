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
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.RecipeCreator;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;

public class UnidentifiedBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    private static final String REGISTRY_NAME_PREFIX = "unidentified_";

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .mapColor(MapColor.COLOR_GRAY);

    private static final BlockTagData PICKAXE_TAGS = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

    private static final BlockTagData SHOVEL_TAGS = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_SHOVEL));

    public final SingleBlockDataContainer.Holder rock;

    protected UnidentifiedBlockSetDataContainer() {
        super();

        this.rock = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                REGISTRY_NAME_PREFIX + "rock",
                BlockModelData.NORMAL_CUBE,
                PICKAXE_TAGS,
                LootProviders.SELF
            )
        );

        this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                REGISTRY_NAME_PREFIX + "stone",
                BlockModelData.NORMAL_CUBE,
                PICKAXE_TAGS,
                LootProviders.SELF
            )
        );

        this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES), // TODO: Change properties.
                REGISTRY_NAME_PREFIX + "dirt",
                BlockModelData.NORMAL_CUBE,
                SHOVEL_TAGS,
                LootProviders.SELF
            )
        );

        this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES), // TODO: Change properties.
                REGISTRY_NAME_PREFIX + "gravel",
                BlockModelData.NORMAL_CUBE,
                SHOVEL_TAGS,
                LootProviders.SELF
            )
        );

        this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES), // TODO: Change properties.
                REGISTRY_NAME_PREFIX + "sand",
                BlockModelData.NORMAL_CUBE,
                SHOVEL_TAGS,
                LootProviders.SELF
            )
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {}

    public static final UnidentifiedBlockSetDataContainer INSTANCE = new UnidentifiedBlockSetDataContainer();
}
