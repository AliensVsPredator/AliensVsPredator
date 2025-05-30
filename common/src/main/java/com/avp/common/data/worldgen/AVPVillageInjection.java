package com.avp.common.data.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;

import com.avp.common.registry.key.AVPStructureProcessorListKeys;
import com.avp.mixin.StructurePoolAccessor;

public class AVPVillageInjection {

    public static void addBuildingToPool(
        Registry<StructureTemplatePool> templatePoolRegistry,
        Registry<StructureProcessorList> processorListRegistry,
        ResourceLocation poolRL,
        String nbtPieceRL,
        int weight
    ) {
        if (processorListRegistry.getHolder(AVPStructureProcessorListKeys.EMPTY_PROCESSOR_LIST_KEY).isEmpty()) {
            return;
        }

        var emptyProcessorList = processorListRegistry.getHolder(AVPStructureProcessorListKeys.EMPTY_PROCESSOR_LIST_KEY).get();
        var pool = templatePoolRegistry.get(poolRL);

        if (pool == null) {
            return;
        }

        var piece = SinglePoolElement.legacy(nbtPieceRL, emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID);

        for (var i = 0; i < weight; i++) {
            ((StructurePoolAccessor) pool).getElements().add(piece);
        }

        var listOfPieceEntries = new ArrayList<>(((StructurePoolAccessor) pool).getElementCounts());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructurePoolAccessor) pool).setElementCounts(listOfPieceEntries);
    }
}
