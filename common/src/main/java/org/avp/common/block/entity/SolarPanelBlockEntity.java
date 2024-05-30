package org.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.block.power.PowerGraph;
import org.avp.api.block.power.PowerProvider;
import org.avp.common.block.entity.data.SolarPanelData;

public class SolarPanelBlockEntity extends BlockEntity implements PowerProvider {

    private final PowerGraph powerGraph;

    public SolarPanelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SolarPanelData.INSTANCE.getHolder().get(), blockPos, blockState);
        this.powerGraph = new PowerGraph();
    }

    @Override
    public PowerGraph getPowerGraph() {
        return powerGraph;
    }

    @Override
    public int providePowerPerTick() {
        return 0; // TODO: Compute based on time of day and angle to sun.
    }
}
