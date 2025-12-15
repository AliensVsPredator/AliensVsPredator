package com.blib.common.registry;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.blib.common.network.data.DataKey;

public class DataKeyRegistry {

    private static final Map<Integer, ResourceLocation> ID_TO_RESOURCE_LOCATION = new HashMap<>();

    private static final Map<ResourceLocation, Integer> RESOURCE_LOCATION_TO_ID = new HashMap<>();

    private static final Map<ResourceLocation, DataKey<?>> RESOURCE_LOCATION_TO_DATA_KEY = new HashMap<>();

    private static final AtomicInteger NEXT_FREE_ID = new AtomicInteger(0);

    public static <T> DataKey<T> register(ResourceLocation resourceLocation, DataKey<T> dataKey) {
        var id = NEXT_FREE_ID.getAndIncrement();
        ID_TO_RESOURCE_LOCATION.put(id, resourceLocation);
        RESOURCE_LOCATION_TO_ID.put(resourceLocation, id);
        RESOURCE_LOCATION_TO_DATA_KEY.put(resourceLocation, dataKey);
        return dataKey;
    }

    public static @Nullable ResourceLocation getResourceLocationOrNull(int id) {
        return ID_TO_RESOURCE_LOCATION.get(id);
    }

    public static @Nullable Integer getIdOrNull(ResourceLocation resourceLocation) {
        return RESOURCE_LOCATION_TO_ID.get(resourceLocation);
    }

    public static @Nullable DataKey<?> getDataKeyOrNull(int id) {
        var resourceLocation = getResourceLocationOrNull(id);

        return resourceLocation == null
            ? null
            : getDataKeyOrNull(resourceLocation);
    }

    public static @Nullable DataKey<?> getDataKeyOrNull(ResourceLocation resourceLocation) {
        return RESOURCE_LOCATION_TO_DATA_KEY.get(resourceLocation);
    }
}
