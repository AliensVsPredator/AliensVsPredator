package org.avp.common.data.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

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
import org.avp.common.game.block.ResinVeinBlock;
import org.avp.common.registry.item.AVPItemRegistry;

public class AlienBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    private static final BlockBehaviour.Properties RESIN_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .mapColor(MapColor.COLOR_GRAY);

    // FIXME:
    private static final BlockBehaviour.Properties RESIN_VEIN_PROPERTIES = BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_GRAY)
        .replaceable()
        .noCollission()
        .randomTicks()
        .strength(0.2F)
        .sound(SoundType.VINE)
        .ignitedByLava()
        .pushReaction(PushReaction.DESTROY);

    private static final BlockTagData BLOCK_TAGS = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

    public final SingleBlockDataContainer.Holder resin;

    public final SingleBlockDataContainer.Holder resinVein;

    public final SingleBlockDataContainer.Holder resinWebbing;

    protected AlienBlockSetDataContainer() {
        super();

        this.resin = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(RESIN_PROPERTIES),
                "resin",
                BlockModelData.NORMAL_CUBE,
                BLOCK_TAGS,
                LootProviders.SELF
            )
        );

        this.resinVein = this.addVariant(
            new SingleBlockDataContainer(
                () -> new ResinVeinBlock(RESIN_VEIN_PROPERTIES),
                "resin_vein",
                new BlockModelData(
                    BlockModelDataType.MultiFace::new,
                    BlockModelRenderType.TRANSLUCENT
                ),
                BLOCK_TAGS,
                LootProviders.SELF
            )
        );

        // FIXME: fix model data
        this.resinWebbing = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(RESIN_PROPERTIES),
                "resin_webbing",
                BlockModelData.CUTOUT_CUBE,
                BLOCK_TAGS,
                LootProviders.SELF
            )
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPItemRegistry.INSTANCE.resinBall.get())
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .into(3, resin);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPItemRegistry.INSTANCE.resinBall.get())
            .pattern(" A ")
            .pattern("AAA")
            .pattern(" A ")
            .into(3, resinVein);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPItemRegistry.INSTANCE.resinBall.get())
            .pattern("A A")
            .pattern(" A ")
            .pattern("A A")
            .into(3, resinWebbing);
    }

    public static final AlienBlockSetDataContainer INSTANCE = new AlienBlockSetDataContainer();
}
