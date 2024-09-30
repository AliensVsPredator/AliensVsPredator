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
import org.avp.api.common.data.block.RecipeCreator;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.recipe.AVPRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;

public class OreTitaniumBlockDataContainer extends SingleBlockDataContainer.Holder implements RecipeCreator {

    public static final OreTitaniumBlockDataContainer INSTANCE = new OreTitaniumBlockDataContainer();

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(4, 4);

    protected OreTitaniumBlockDataContainer() {
        super(
            () -> new Block(PROPERTIES),
            "ore_titanium",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)),
            block -> LootProviders.ORE.apply(block, AVPItemRegistry.INSTANCE.rawTitanium.get())
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {
        var builder = AVPRecipeBuilder.with(recipeOutput);

        var experience = 0.7F;
        var recipeCategory = RecipeCategory.MISC;
        var cookTimeInTicks = 100;

        // Smelting
        // TODO:
//        builder.smelt(this)
//            .withCategory(recipeCategory)
//            .withExperience(experience)
//            .withCookTime(cookTimeInTicks * 2)
//            .into(AVPItemRegistry.INSTANCE.ingotTitanium.get());

        // Blasting
        // TODO:
//        builder.blast(this)
//            .withCategory(recipeCategory)
//            .withExperience(experience)
//            .withCookTime(cookTimeInTicks)
//            .into(AVPItemRegistry.INSTANCE.ingotTitanium.get());
    }
}
