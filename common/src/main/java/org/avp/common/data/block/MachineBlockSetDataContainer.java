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

public class MachineBlockSetDataContainer extends ExtendedBlockDataContainer implements RecipeCreator {

    // FIXME:
    private static final BlockBehaviour.Properties SOLAR_PANEL_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .mapColor(MapColor.COLOR_GRAY);

    private static final BlockTagData BLOCK_TAGS = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

    public final SingleBlockDataContainer.Holder solarPanel;

    protected MachineBlockSetDataContainer() {
        this.solarPanel = this.addVariant(
            new SingleBlockDataContainer(
                () -> new Block(SOLAR_PANEL_PROPERTIES),
                "solar_panel",
                BlockModelData.NORMAL_CUBE,
                BLOCK_TAGS,
                LootProviders.SELF
            )
        );
    }

    @Override
    public void createRecipes(RecipeOutput recipeOutput) {}

    public static final MachineBlockSetDataContainer INSTANCE = new MachineBlockSetDataContainer();
}
