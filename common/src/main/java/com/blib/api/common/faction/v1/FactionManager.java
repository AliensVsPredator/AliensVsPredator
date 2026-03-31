package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import com.blib.api.common.registry.v1.BLibHolder;

public interface FactionManager {

    <T extends FactionData> Faction<T> getOrCreate(ResourceLocation id, BLibHolder<FactionDataType<T>> type);

    default <T extends FactionData> Faction<T> getOrCreate(FactionDataKey<T> key) {
        return getOrCreate(key.id(), key.type());
    }

    @Nullable
    Faction<?> get(ResourceLocation id);

    Set<ResourceLocation> getFactionIds(UUID entityUuid);

    Set<ResourceLocation> getFactionsByTag(TagKey<FactionDataType<?>> tag);

    boolean remove(ResourceLocation id);

    Collection<ResourceLocation> getAllIds();

    boolean exists(ResourceLocation id);
}
