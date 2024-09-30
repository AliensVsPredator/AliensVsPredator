package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockModelDataType;
import org.avp.api.common.data.block.BlockModelRenderType;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.ExtendedBlockDataContainer;
import org.avp.api.common.data.block.RecipeCreator;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.game.block.AVPWoodType;

public class ParadiseBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    private static final String REGISTRY_NAME_PREFIX = "paradise_";

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .mapColor(MapColor.COLOR_GRAY);

    private static final BlockTagData AXE_TAGS = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_AXE));

    private static final BlockTagData PICKAXE_TAGS = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

    private static final BlockTagData SHOVEL_TAGS = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_SHOVEL));

    public final SingleBlockDataContainer.Holder dirt;

    private final SingleBlockDataContainer.Holder largePlanks;

    private final VanillaVariantBlockDataContainer largeVariantSet;

    private final SingleBlockDataContainer.Holder mediumPlanks;

    private final VanillaVariantBlockDataContainer mediumVariantSet;

    private final SingleBlockDataContainer.Holder smallPlanks;

    private final VanillaVariantBlockDataContainer smallVariantSet;

    protected ParadiseBlockSetDataContainer() {
        super();

        this.dirt = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                REGISTRY_NAME_PREFIX + "dirt",
                BlockModelData.NORMAL_CUBE,
                SHOVEL_TAGS,
                LootProviders.SELF
            )
        );

        this.addVariant(dirt.extend(REGISTRY_NAME_PREFIX + "dirt_mossy"));

        this.addVariant(dirt.extend(REGISTRY_NAME_PREFIX + "dirt_podzol"));

        var leavesBase = new SingleBlockDataContainer(
            () -> new Block(PROPERTIES),
            REGISTRY_NAME_PREFIX + "leaves",
            BlockModelData.TRANSPARENT_CUBE,
            BlockTagData.none(),
            LootProviders.SELF
        );

        this.addVariant(leavesBase.extend(REGISTRY_NAME_PREFIX + "leaves_large"));

        this.addVariant(leavesBase.extend(REGISTRY_NAME_PREFIX + "leaves_medium"));

        this.addVariant(leavesBase.extend(REGISTRY_NAME_PREFIX + "leaves_small"));

        var woodLogBase = new SingleBlockDataContainer(
            () -> new RotatedPillarBlock(PROPERTIES),
            REGISTRY_NAME_PREFIX + "log",
            new BlockModelData(
                BlockModelDataType.RotatedPillar::new,
                BlockModelRenderType.NORMAL
            ),
            AXE_TAGS,
            LootProviders.SELF
        );

        var logLarge = this.addVariant(woodLogBase.extend(REGISTRY_NAME_PREFIX + "log_large"));

        var logMedium = this.addVariant(woodLogBase.extend(REGISTRY_NAME_PREFIX + "log_medium"));

        var logSmall = this.addVariant(woodLogBase.extend(REGISTRY_NAME_PREFIX + "log_small"));

        var woodBase = new SingleBlockDataContainer(
            () -> new RotatedPillarBlock(PROPERTIES),
            REGISTRY_NAME_PREFIX + "wood",
            BlockModelData.NORMAL_CUBE,
            AXE_TAGS,
            LootProviders.SELF
        );

        this.addVariant(
            woodBase.transform(REGISTRY_NAME_PREFIX + "wood_large")
                .withModelData(
                    new BlockModelData(
                        (block) -> new BlockModelDataType.Wood(logLarge.getHolder().get(), block),
                        BlockModelRenderType.NORMAL
                    )
                )
                .build()
        );

        this.addVariant(
            woodBase.transform(REGISTRY_NAME_PREFIX + "wood_medium")
                .withModelData(
                    new BlockModelData(
                        (block) -> new BlockModelDataType.Wood(logMedium.getHolder().get(), block),
                        BlockModelRenderType.NORMAL
                    )
                )
                .build()
        );

        this.addVariant(
            woodBase.transform(REGISTRY_NAME_PREFIX + "wood_small")
                .withModelData(
                    new BlockModelData(
                        (block) -> new BlockModelDataType.Wood(logSmall.getHolder().get(), block),
                        BlockModelRenderType.NORMAL
                    )
                )
                .build()
        );

        this.addVariant(
            new SingleBlockDataContainer(
                () -> new GrassBlock(PROPERTIES),
                REGISTRY_NAME_PREFIX + "grass",
                new BlockModelData(
                    block -> new BlockModelDataType.GrassLike(dirt.getHolder().get(), block),
                    BlockModelRenderType.NORMAL
                ),
                SHOVEL_TAGS,
                LootProviders.SELF
            )
        );

        this.largePlanks = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                REGISTRY_NAME_PREFIX + "planks_large",
                BlockModelData.NORMAL_CUBE,
                AXE_TAGS,
                LootProviders.SELF
            )
        );

        this.largeVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(largePlanks)
                .withFence()
                .withFenceGate(AVPWoodType.LARGE)
                .withSlab()
                .withStairs()
        );

        this.mediumPlanks = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                REGISTRY_NAME_PREFIX + "planks_medium",
                BlockModelData.NORMAL_CUBE,
                AXE_TAGS,
                LootProviders.SELF
            )
        );

        this.mediumVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(mediumPlanks)
                .withFence()
                .withFenceGate(AVPWoodType.MEDIUM)
                .withSlab()
                .withStairs()
        );

        this.smallPlanks = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                REGISTRY_NAME_PREFIX + "planks_small",
                BlockModelData.NORMAL_CUBE,
                AXE_TAGS,
                LootProviders.SELF
            )
        );

        this.smallVariantSet = this.addVariant(
            new VanillaVariantBlockDataContainer(smallPlanks)
                .withFence()
                .withFenceGate(AVPWoodType.SMALL)
                .withSlab()
                .withStairs()
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        largeVariantSet.createRecipes(recipeOutput);
        mediumVariantSet.createRecipes(recipeOutput);
        smallVariantSet.createRecipes(recipeOutput);
    }

    public static final ParadiseBlockSetDataContainer INSTANCE = new ParadiseBlockSetDataContainer();
}
