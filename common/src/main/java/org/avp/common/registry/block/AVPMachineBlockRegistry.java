package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockModelDataType;
import org.avp.api.common.data.block.BlockModelRenderType;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.game.block.SolarPanelBlock;

@Deprecated(forRemoval = true)
public class AVPMachineBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPMachineBlockRegistry INSTANCE = new AVPMachineBlockRegistry();

    public final BLHolder<Block> solarPanel;

    private AVPMachineBlockRegistry() {
        var tags = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
            .mapColor(MapColor.COLOR_GRAY);

        solarPanel = createHolder(
            "solar_panel",
            new BlockModelData(
                () -> new SolarPanelBlock(properties),
                BlockModelDataType.Cube::new,
                BlockModelRenderType.NORMAL
            ),
            tags
        );
    }
}
