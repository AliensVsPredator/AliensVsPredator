package com.avp.common.registry.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.service.Services;

public class AVPVillagerPoiTypes {

    public static final AVPDeferredHolder<PoiType> COMMISSARY_POI = register("commissary_poi", AVPBlocks.BLUEPRINT_BLOCK, 1, 1);

    private static AVPDeferredHolder<PoiType> register(String id, Supplier<Block> blockSupplier, int ticketCount, int searchDistance) {
        return Services.REGISTRY.register(
            BuiltInRegistries.POINT_OF_INTEREST_TYPE,
            id,
            () -> new PoiType(new HashSet<>(blockSupplier.get().getStateDefinition().getPossibleStates()), ticketCount, searchDistance)
        );
    }

    public static void initialize() {}
}
