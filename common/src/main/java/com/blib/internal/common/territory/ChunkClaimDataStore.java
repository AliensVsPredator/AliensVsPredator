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
import java.util.stream.Collectors;

import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.territory.v1.ChunkClaim;
import com.blib.api.common.territory.v1.Claimant;
import com.blib.internal.common.event.BLibGlobalEvents;
import com.blib.internal.common.territory.serializer.ChunkClaimSerializer;

@ApiStatus.Internal
public class ChunkClaimDataStore implements DataStore {

    private static final String CLAIMS_KEY = "claims";

    private final Set<ChunkClaim> claims;

    private ServerLevel level;

    private ChunkPos chunkPos;

    public ChunkClaimDataStore() {
        this.claims = new HashSet<>();
    }

    public void setContext(ServerLevel level, ChunkPos chunkPos) {
        this.level = level;
        this.chunkPos = chunkPos;
    }

    public boolean addClaim(ChunkClaim claim) {
        if (!claims.add(claim)) {
            return false;
        }

        fireClaimAdded(claim);

        return true;
    }

    public boolean removeClaim(Claimant claimant, net.minecraft.resources.ResourceLocation reason) {
        var removed = claims.removeIf(
            claim -> claim.claimant().equals(claimant) && claim.reason().equals(reason)
        );

        if (removed) {
            fireClaimRemoved(new ChunkClaim(claimant, reason, 0));
        }

        return removed;
    }

    public void removeAllClaims(Claimant claimant) {
        var toRemove = claims.stream()
            .filter(claim -> claim.claimant().equals(claimant))
            .toList();

        for (var claim : toRemove) {
            claims.remove(claim);
            fireClaimRemoved(claim);
        }
    }

    public Set<ChunkClaim> getClaims() {
        return Collections.unmodifiableSet(claims);
    }

    public Set<ChunkClaim> getClaims(net.minecraft.resources.ResourceLocation reason) {
        return claims.stream()
            .filter(claim -> claim.reason().equals(reason))
            .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Claimant> getClaimants() {
        return claims.stream()
            .map(ChunkClaim::claimant)
            .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isClaimedBy(Claimant claimant) {
        return claims.stream().anyMatch(claim -> claim.claimant().equals(claimant));
    }

    public boolean isContested() {
        return getClaimants().size() > 1;
    }

    public boolean isContested(net.minecraft.resources.ResourceLocation reason) {
        var claimantsForReason = claims.stream()
            .filter(claim -> claim.reason().equals(reason))
            .map(ChunkClaim::claimant)
            .collect(Collectors.toSet());

        return claimantsForReason.size() > 1;
    }

    public boolean isEmpty() {
        return claims.isEmpty();
    }

    public int claimCount() {
        return claims.size();
    }

    private void fireClaimAdded(ChunkClaim claim) {
        if (level == null || chunkPos == null) {
            return;
        }

        for (var listener : BLibGlobalEvents.CHUNK_CLAIM_ADDED.listeners()) {
            listener.invoke(level, chunkPos, claim);
        }
    }

    private void fireClaimRemoved(ChunkClaim claim) {
        if (level == null || chunkPos == null) {
            return;
        }

        for (var listener : BLibGlobalEvents.CHUNK_CLAIM_REMOVED.listeners()) {
            listener.invoke(level, chunkPos, claim);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        claims.clear();

        if (!compoundTag.contains(CLAIMS_KEY)) {
            return;
        }

        var listTag = compoundTag.getList(CLAIMS_KEY, Tag.TAG_COMPOUND);

        for (var i = 0; i < listTag.size(); i++) {
            var claim = ChunkClaimSerializer.deserialize(listTag.getCompound(i));

            if (claim != null) {
                claims.add(claim);
            }
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        if (claims.isEmpty()) {
            return;
        }

        var listTag = new ListTag();

        for (var claim : claims) {
            listTag.add(ChunkClaimSerializer.serialize(claim));
        }

        compoundTag.put(CLAIMS_KEY, listTag);
    }
}
