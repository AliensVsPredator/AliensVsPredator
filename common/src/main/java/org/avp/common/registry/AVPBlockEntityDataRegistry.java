package org.avp.common.registry;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.avp.api.data.block_entity.BlockEntityData;
import org.avp.common.data.block_entity.SolarPanelData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AVPBlockEntityDataRegistry {

    public static final AVPBlockEntityDataRegistry INSTANCE = new AVPBlockEntityDataRegistry();

    private final Map<String, BlockEntityData<?>> entries = new HashMap<>();

    public Collection<BlockEntityData<?>> getEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    private void addEntry(BlockEntityData<? extends BlockEntity> blockEntityData) {
        entries.put(blockEntityData.getRegistryName(), blockEntityData);
    }

    public void register() {
        addEntry(SolarPanelData.INSTANCE);
    }

    private AVPBlockEntityDataRegistry() {}
}
