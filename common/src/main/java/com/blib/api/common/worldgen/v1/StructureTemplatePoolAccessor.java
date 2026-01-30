package com.blib.api.common.worldgen.v1;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

import java.util.List;

public interface StructureTemplatePoolAccessor {

    ObjectArrayList<StructurePoolElement> getElements();

    List<Pair<StructurePoolElement, Integer>> getElementCounts();

    void setElementCounts(List<Pair<StructurePoolElement, Integer>> list);
}
