package com.blib.common.registry.key;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class BLibStructureProcessorListKeys {

    public static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(
        Registries.PROCESSOR_LIST,
        ResourceLocation.withDefaultNamespace("empty")
    );

    private BLibStructureProcessorListKeys() {}
}
