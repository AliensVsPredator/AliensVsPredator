package com.blib.api.common.faction.v1;

import com.just.core.functional.result.Result;
import com.just.core.functional.tuple.Tuple2;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import com.blib.api.common.registry.v1.BLibHolder;

public interface FactionManager {

    <T extends FactionData> Result<Tuple2<FactionRelationships, T>, FactionDataError> getOrCreate(
        ResourceLocation id,
        BLibHolder<FactionDataType<T>> type
    );

    default <T extends FactionData> Result<Tuple2<FactionRelationships, T>, FactionDataError> getOrCreate(FactionDataKey<T> key) {
        return getOrCreate(key.id(), key.type());
    }

    FactionRelationships getRelationships(ResourceLocation id);

    <T extends FactionData> Result<T, FactionDataError> getData(ResourceLocation id, BLibHolder<FactionDataType<T>> type);

    default <T extends FactionData> Result<T, FactionDataError> getData(FactionDataKey<T> key) {
        return getData(key.id(), key.type());
    }

    Set<ResourceLocation> getFactionIds(UUID entityUuid);

    Set<ResourceLocation> getParentFactionIds(ResourceLocation subfactionId);

    boolean remove(ResourceLocation id);

    Collection<ResourceLocation> getAllIds();

    boolean exists(ResourceLocation id);
}
