package com.blib.internal.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

import com.blib.api.common.worldgen.v1.StructureTemplatePoolAccessor;

@Mixin(StructureTemplatePool.class)
public interface MixinStructureTemplatePool_Accessor extends StructureTemplatePoolAccessor {

    @Accessor(value = "templates")
    @Override
    ObjectArrayList<StructurePoolElement> getElements();

    @Accessor(value = "rawTemplates")
    @Override
    List<Pair<StructurePoolElement, Integer>> getElementCounts();

    @Accessor(value = "rawTemplates")
    @Mutable
    @Override
    void setElementCounts(List<Pair<StructurePoolElement, Integer>> list);
}
