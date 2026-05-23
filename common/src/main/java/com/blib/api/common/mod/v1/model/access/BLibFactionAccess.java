package com.blib.api.common.mod.v1.model.access;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import com.blib.api.common.faction.v1.Faction;
import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.faction.v1.FactionDataType;
import com.blib.api.common.faction.v1.FactionManager;
import com.blib.api.common.faction.v1.RelationshipState;
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
    public <T extends FactionData> Faction<T> getOrCreate(ResourceLocation id, BLibHolder<FactionDataType<T>> type) {
        return BLibFactionManager.INSTANCE.getOrCreate(id, type);
    }

    @Override
    public @Nullable Faction<?> get(ResourceLocation id) {
        return BLibFactionManager.INSTANCE.get(id);
    }

    @Override
    public Set<ResourceLocation> getFactionIds(UUID entityUuid) {
        return BLibFactionManager.INSTANCE.getFactionIds(entityUuid);
    }

    @Override
    public Set<ResourceLocation> getFactionsByTag(TagKey<FactionDataType<?>> tag) {
        return BLibFactionManager.INSTANCE.getFactionsByTag(tag);
    }

    @Override
    public RelationshipState getRelationship(ResourceLocation factionA, ResourceLocation factionB) {
        return BLibFactionManager.INSTANCE.getRelationship(factionA, factionB);
    }

    @Override
    public void setRelationship(ResourceLocation factionA, ResourceLocation factionB, RelationshipState state) {
        BLibFactionManager.INSTANCE.setRelationship(factionA, factionB, state);
    }

    @Override
    public Set<ResourceLocation> getFactionsWithState(ResourceLocation factionId, RelationshipState state) {
        return BLibFactionManager.INSTANCE.getFactionsWithState(factionId, state);
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
