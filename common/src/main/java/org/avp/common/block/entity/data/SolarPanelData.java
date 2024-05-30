package org.avp.common.block.entity.data;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.avp.api.Holder;
import org.avp.api.block.entity.data.BlockEntityData;
import org.avp.common.block.AVPMachineBlocks;
import org.avp.common.block.entity.SolarPanelBlockEntity;
import org.avp.common.registry.AVPSimpleDeferredBlockEntityTypeRegistry;

public class SolarPanelData extends BlockEntityData<SolarPanelBlockEntity> {

    public static final SolarPanelData INSTANCE = new SolarPanelData();

    @Override
    protected Holder<BlockEntityType<SolarPanelBlockEntity>> createHolder() {
        return AVPSimpleDeferredBlockEntityTypeRegistry.INSTANCE.createHolder(
            "solar_panel",
            SolarPanelBlockEntity::new,
            AVPMachineBlocks.INSTANCE.solarPanel
        );
    }
}
