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
import org.avp.common.data.recipe.AVPRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;

public class MetalOrioniteBlockDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    public static final MetalOrioniteBlockDataContainer INSTANCE = new MetalOrioniteBlockDataContainer();

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.SAND)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(20, 12);

    private final SingleBlockDataContainer.Holder base;

    protected MetalOrioniteBlockDataContainer() {
        this.base = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(PROPERTIES),
                "orionite_block",
                BlockModelData.NORMAL_CUBE,
                BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)),
                LootProviders.SELF
            )
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        // Ingots -> block
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPItemRegistry.INSTANCE.ingotOrionite.get())
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .into(1, base);

        // Block -> ingots
        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, base)
            .into(9, AVPItemRegistry.INSTANCE.ingotOrionite.get());
    }
}
