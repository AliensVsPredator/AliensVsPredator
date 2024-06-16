package org.avp.common.data.block_entity;

import net.minecraft.world.level.block.entity.BlockEntityType;

import org.avp.api.common.data.block_entity.BlockEntityData;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.game.block_entity.SolarPanelBlockEntity;
import org.avp.common.registry.block.AVPMachineBlockRegistry;
import org.avp.common.registry.block_entity.AVPSimpleDeferredBlockEntityTypeRegistry;

public class SolarPanelData extends BlockEntityData<SolarPanelBlockEntity> {

    public static final SolarPanelData INSTANCE = new SolarPanelData();

    @Override
    protected BLHolder<BlockEntityType<SolarPanelBlockEntity>> createHolder() {
        return AVPSimpleDeferredBlockEntityTypeRegistry.INSTANCE.createHolder(
            "solar_panel",
            SolarPanelBlockEntity::new,
            AVPMachineBlockRegistry.INSTANCE.solarPanel
        );
    }
}
