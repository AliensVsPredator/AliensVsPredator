package com.blib.internal.common.territory.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.territory.v1.ChunkClaim;

@ApiStatus.Internal
public final class ChunkClaimSerializer {

    private static final String CLAIMANT_KEY = "claimant";

    private static final String REASON_KEY = "reason";

    private static final String CLAIMED_AT_TICK_KEY = "claimed_at_tick";

    private ChunkClaimSerializer() {}

    public static CompoundTag serialize(ChunkClaim claim) {
        var tag = new CompoundTag();

        tag.put(CLAIMANT_KEY, ClaimantSerializer.serialize(claim.claimant()));
        tag.putString(REASON_KEY, claim.reason().toString());
        tag.putLong(CLAIMED_AT_TICK_KEY, claim.claimedAtTick());

        return tag;
    }

    public static @Nullable ChunkClaim deserialize(CompoundTag tag) {
        var claimant = ClaimantSerializer.deserialize(tag.getCompound(CLAIMANT_KEY));

        if (claimant == null) {
            return null;
        }

        var reason = ResourceLocation.tryParse(tag.getString(REASON_KEY));

        if (reason == null) {
            return null;
        }

        var claimedAtTick = tag.getLong(CLAIMED_AT_TICK_KEY);

        return new ChunkClaim(claimant, reason, claimedAtTick);
    }
}
