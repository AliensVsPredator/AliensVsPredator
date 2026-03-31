package com.blib.internal.common.territory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.territory.v1.Claimant;
import com.blib.internal.common.event.BLibGlobalEvents;
import com.blib.internal.common.territory.serializer.ClaimantSerializer;

@ApiStatus.Internal
public class ChunkClaimDataStore implements DataStore {

    private static final String CLAIMANTS_KEY = "claimants";

    private final Set<Claimant> claimants;

    private ServerLevel level;

    private ChunkPos chunkPos;

    public ChunkClaimDataStore() {
        this.claimants = new HashSet<>();
    }

    public void setContext(ServerLevel level, ChunkPos chunkPos) {
        this.level = level;
        this.chunkPos = chunkPos;
    }

    public boolean addClaim(Claimant claimant) {
        if (!claimants.add(claimant)) {
            return false;
        }

        fireClaimAdded(claimant);

        return true;
    }

    public boolean removeClaim(Claimant claimant) {
        if (!claimants.remove(claimant)) {
            return false;
        }

        fireClaimRemoved(claimant);

        return true;
    }

    public boolean transferClaim(Claimant from, Claimant to) {
        if (!claimants.remove(from)) {
            return false;
        }

        claimants.add(to);
        fireClaimRemoved(from);
        fireClaimAdded(to);

        return true;
    }

    public Set<Claimant> getClaimants() {
        return Collections.unmodifiableSet(claimants);
    }

    public boolean isClaimedBy(Claimant claimant) {
        return claimants.contains(claimant);
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

    private void fireClaimAdded(Claimant claimant) {
        if (level == null || chunkPos == null) {
            return;
        }

        for (var listener : BLibGlobalEvents.CHUNK_CLAIM_ADDED.listeners()) {
            listener.invoke(level, chunkPos, claimant);
        }
    }

    private void fireClaimRemoved(Claimant claimant) {
        if (level == null || chunkPos == null) {
            return;
        }

        for (var listener : BLibGlobalEvents.CHUNK_CLAIM_REMOVED.listeners()) {
            listener.invoke(level, chunkPos, claimant);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        claimants.clear();

        if (!compoundTag.contains(CLAIMANTS_KEY)) {
            return;
        }

        var listTag = compoundTag.getList(CLAIMANTS_KEY, Tag.TAG_COMPOUND);

        for (var i = 0; i < listTag.size(); i++) {
            var claimant = ClaimantSerializer.deserialize(listTag.getCompound(i));

            if (claimant != null) {
                claimants.add(claimant);
            }
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        if (claimants.isEmpty()) {
            return;
        }

        var listTag = new ListTag();

        for (var claimant : claimants) {
            listTag.add(ClaimantSerializer.serialize(claimant));
        }

        compoundTag.put(CLAIMANTS_KEY, listTag);
    }
}
