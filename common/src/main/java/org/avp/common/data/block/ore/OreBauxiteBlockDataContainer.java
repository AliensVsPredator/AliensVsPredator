package org.avp.common.data.block.ore;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.recipe.AVPRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;

public class OreBauxiteBlockDataContainer extends SingleBlockDataContainer.Holder {

    public static final OreBauxiteBlockDataContainer INSTANCE = new OreBauxiteBlockDataContainer();

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(3.2F, 2.6F);

    protected OreBauxiteBlockDataContainer() {
        super(
            () -> new Block(PROPERTIES),
            "ore_bauxite",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)),
            block -> LootProviders.ORE.apply(block, AVPItemRegistry.INSTANCE.rawBauxite.get())
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        var experience = 0.7F;
        var recipeCategory = RecipeCategory.MISC;
        var cookTimeInTicks = 100;

        // Smelting
        builder.smelt(this)
            .withCategory(recipeCategory)
            .withExperience(experience)
            .withCookTime(cookTimeInTicks * 2)
            .into(AVPItemRegistry.INSTANCE.ingotAluminum.get());

        // Blasting
        builder.blast(this)
            .withCategory(recipeCategory)
            .withExperience(experience)
            .withCookTime(cookTimeInTicks)
            .into(AVPItemRegistry.INSTANCE.ingotAluminum.get());
    }
}
