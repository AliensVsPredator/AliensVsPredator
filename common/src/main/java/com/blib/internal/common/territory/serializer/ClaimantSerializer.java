package com.blib.internal.common.territory.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.territory.v1.Claimant;

@ApiStatus.Internal
public final class ClaimantSerializer {

    private static final String TYPE_KEY = "type";

    private static final String ID_KEY = "id";

    private static final String ENTITY_TYPE = "entity";

    private static final String FACTION_TYPE = "faction";

    private ClaimantSerializer() {}

    public static CompoundTag serialize(Claimant claimant) {
        var tag = new CompoundTag();

        switch (claimant) {
            case Claimant.EntityClaimant(var entityId) -> {
                tag.putString(TYPE_KEY, ENTITY_TYPE);
                tag.putUUID(ID_KEY, entityId);
            }
            case Claimant.FactionClaimant(var factionId) -> {
                tag.putString(TYPE_KEY, FACTION_TYPE);
                tag.putString(ID_KEY, factionId.toString());
            }
        }

        return tag;
    }

    public static @Nullable Claimant deserialize(CompoundTag tag) {
        if (!tag.contains(TYPE_KEY)) {
            return null;
        }

        var type = tag.getString(TYPE_KEY);

        return switch (type) {
            case ENTITY_TYPE -> new Claimant.EntityClaimant(tag.getUUID(ID_KEY));
            case FACTION_TYPE -> {
                var factionId = ResourceLocation.tryParse(tag.getString(ID_KEY));
                yield factionId == null ? null : new Claimant.FactionClaimant(factionId);
            }
            default -> null;
        };
    }
}
