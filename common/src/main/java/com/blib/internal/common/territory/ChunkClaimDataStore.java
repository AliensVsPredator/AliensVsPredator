package com.blib.internal.common.territory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.blib.api.common.storage.v1.DataStore;
import com.blib.internal.common.event.BLibGlobalEvents;

@ApiStatus.Internal
public class ChunkClaimDataStore implements DataStore {

    private static final String CLAIMANTS_KEY = "claimants";

    private final Set<ResourceLocation> claimants;

    private ServerLevel level;

    private ChunkPos chunkPos;

    public ChunkClaimDataStore() {
        this.claimants = new HashSet<>();
    }

    public void setContext(ServerLevel level, ChunkPos chunkPos) {
        this.level = level;
        this.chunkPos = chunkPos;
    }

    public boolean addClaim(ResourceLocation factionId) {
        if (!claimants.add(factionId)) {
            return false;
        }

        fireClaimAdded(factionId);

        return true;
    }

    public boolean removeClaim(ResourceLocation factionId) {
        if (!claimants.remove(factionId)) {
            return false;
        }

        fireClaimRemoved(factionId);

        return true;
    }

    public boolean transferClaim(ResourceLocation from, ResourceLocation to) {
        if (!claimants.remove(from)) {
            return false;
        }

        claimants.add(to);
        fireClaimRemoved(from);
        fireClaimAdded(to);

        return true;
    }

    public Set<ResourceLocation> getClaimants() {
        return Collections.unmodifiableSet(claimants);
    }

    public boolean isClaimedBy(ResourceLocation factionId) {
        return claimants.contains(factionId);
    }

    public boolean isContested() {
        return claimants.size() > 1;
    }

    public boolean isEmpty() {
        return claimants.isEmpty();
    }

    public int claimCount() {
        return claimants.size();
    }

    private void fireClaimAdded(ResourceLocation factionId) {
        if (level == null || chunkPos == null) {
            return;
        }

        for (var listener : BLibGlobalEvents.CHUNK_CLAIM_ADDED.listeners()) {
            listener.invoke(level, chunkPos, factionId);
        }
    }

    private void fireClaimRemoved(ResourceLocation factionId) {
        if (level == null || chunkPos == null) {
            return;
        }

        for (var listener : BLibGlobalEvents.CHUNK_CLAIM_REMOVED.listeners()) {
            listener.invoke(level, chunkPos, factionId);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        claimants.clear();

        if (!compoundTag.contains(CLAIMANTS_KEY)) {
            return;
        }

        var listTag = compoundTag.getList(CLAIMANTS_KEY, Tag.TAG_STRING);

        for (var i = 0; i < listTag.size(); i++) {
            var factionId = ResourceLocation.tryParse(listTag.getString(i));

            if (factionId != null) {
                claimants.add(factionId);
            }
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        if (claimants.isEmpty()) {
            return;
        }

        var listTag = new ListTag();

        for (var factionId : claimants) {
            listTag.add(StringTag.valueOf(factionId.toString()));
        }

        compoundTag.put(CLAIMANTS_KEY, listTag);
    }
}
