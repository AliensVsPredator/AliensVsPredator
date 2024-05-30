package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.avp.api.Holder;
import org.avp.api.block.BlockTagData;
import org.avp.api.block.model.BlockModelData;
import org.avp.api.block.model.render_type.BlockModelRenderType;
import org.avp.api.block.model.type.BlockModelDataType;
import org.avp.common.block.power.SolarPanelBlock;
import org.avp.common.registry.AVPDeferredBlockRegistry;

import java.util.Set;

public class AVPMachineBlocks extends AVPDeferredBlockRegistry {

    public static final AVPMachineBlocks INSTANCE = new AVPMachineBlocks();

    public final Holder<Block> solarPanel;

    private AVPMachineBlocks() {
        var tags = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
            .mapColor(MapColor.COLOR_GRAY);

        solarPanel = createHolder("solar_panel", new BlockModelData(
            () -> new SolarPanelBlock(properties),
            BlockModelDataType.Cube::new,
            BlockModelRenderType.NORMAL
        ), tags);
    }
}
