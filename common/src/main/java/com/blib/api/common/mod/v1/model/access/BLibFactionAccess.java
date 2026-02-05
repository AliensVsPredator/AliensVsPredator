package com.blib.api.common.mod.v1.model.access;

import com.just.core.functional.result.Result;
import com.just.core.functional.tuple.Tuple2;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.faction.v1.FactionDataError;
import com.blib.api.common.faction.v1.FactionManager;
import com.blib.api.common.faction.v1.FactionRelationships;
import com.blib.api.common.faction.v1.FactionType;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.internal.common.faction.BLibFactionManager;

public class BLibFactionAccess implements FactionManager {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibFactionAccess(BLibMod mod) {
        this.mod = mod;
    }

    @Override
    public <T extends FactionData> Result<Tuple2<FactionRelationships, T>, FactionDataError> getOrCreate(
        ResourceLocation id,
        BLibHolder<FactionType<T>> type
    ) {
        return BLibFactionManager.INSTANCE.getOrCreate(id, type);
    }

    @Override
    public FactionRelationships getRelationships(ResourceLocation id) {
        return BLibFactionManager.INSTANCE.getRelationships(id);
    }

    @Override
    public <T extends FactionData> Result<T, FactionDataError> getData(ResourceLocation id, BLibHolder<FactionType<T>> type) {
        return BLibFactionManager.INSTANCE.getData(id, type);
    }

    @Override
    public Set<ResourceLocation> getFactionIds(UUID entityUuid) {
        return BLibFactionManager.INSTANCE.getFactionIds(entityUuid);
    }

    @Override
    public Set<ResourceLocation> getParentFactionIds(ResourceLocation subfactionId) {
        return BLibFactionManager.INSTANCE.getParentFactionIds(subfactionId);
    }

    @Override
    public boolean remove(ResourceLocation id) {
        return BLibFactionManager.INSTANCE.remove(id);
    }

    @Override
    public Collection<ResourceLocation> getAllIds() {
        return BLibFactionManager.INSTANCE.getAllIds();
    }

    @Override
    public boolean exists(ResourceLocation id) {
        return BLibFactionManager.INSTANCE.exists(id);
    }
}
