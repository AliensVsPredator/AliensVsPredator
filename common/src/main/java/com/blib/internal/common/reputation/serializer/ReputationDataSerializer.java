package com.blib.internal.common.reputation.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationKey;

@ApiStatus.Internal
public final class ReputationDataSerializer {

    private static final String KEY_TYPE = "type";

    private static final String KEY_REPUTATIONS = "reputations";

    private static final String TYPE_FACTION = "faction";

    private static final String TYPE_ENTITY = "entity";

    private static final String FACTION_PREFIX = "faction:";

    private static final String ENTITY_PREFIX = "entity:";

    private ReputationDataSerializer() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag serialize(ReputationData data) {
        var tag = new CompoundTag();

        switch (data.getKey()) {
            case ReputationKey.Faction faction -> tag.putString(KEY_TYPE, TYPE_FACTION);
            case ReputationKey.Entity entity -> tag.putString(KEY_TYPE, TYPE_ENTITY);
        }

        var reputationsTag = new CompoundTag();

        for (var entry : data.getAll().entrySet()) {
            var targetKey = serializeReputationKey(entry.getKey());
            reputationsTag.putInt(targetKey, entry.getValue());
        }

        tag.put(KEY_REPUTATIONS, reputationsTag);

        return tag;
    }

    public static ReputationData deserialize(String reputationKeyString, CompoundTag tag) {
        var reputationKey = deserializeReputationKey(reputationKeyString);
        var data = new ReputationData(reputationKey);
        var reputationsTag = tag.getCompound(KEY_REPUTATIONS);

        for (var key : reputationsTag.getAllKeys()) {
            var target = deserializeReputationKey(key);
            var value = reputationsTag.getInt(key);
            data.getReputationsInternal().put(target, value);
        }

        return data;
    }

    public static String serializeReputationKey(ReputationKey reputationKey) {
        return switch (reputationKey) {
            case ReputationKey.Faction(var factionId) -> FACTION_PREFIX + factionId.toString();
            case ReputationKey.Entity(var uuid) -> ENTITY_PREFIX + uuid.toString();
        };
    }

    public static ReputationKey deserializeReputationKey(String key) {
        if (key.startsWith(FACTION_PREFIX)) {
            var factionId = ResourceLocation.parse(key.substring(FACTION_PREFIX.length()));
            return new ReputationKey.Faction(factionId);
        } else if (key.startsWith(ENTITY_PREFIX)) {
            var uuid = UUID.fromString(key.substring(ENTITY_PREFIX.length()));
            return new ReputationKey.Entity(uuid);
        } else {
            throw new IllegalArgumentException("Unknown reputation key format: " + key);
        }
    }
}
