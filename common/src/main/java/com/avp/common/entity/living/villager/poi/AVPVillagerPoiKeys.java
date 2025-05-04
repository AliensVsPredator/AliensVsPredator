package com.avp.common.entity.living.villager.poi;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import com.avp.AVPResources;

public class AVPVillagerPoiKeys {

    public static final ResourceKey<PoiType> COMMISSARY_POI_KEY = create("commissary_poi");

    private static ResourceKey<PoiType> create(String id) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, AVPResources.location(id));
    }

    public static void initialize() {}
}
