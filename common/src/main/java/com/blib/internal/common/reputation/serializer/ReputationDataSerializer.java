package com.blib.internal.common.reputation.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationSubject;

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

        switch (data.getSubject()) {
            case ReputationSubject.Faction faction -> tag.putString(KEY_TYPE, TYPE_FACTION);
            case ReputationSubject.Entity entity -> tag.putString(KEY_TYPE, TYPE_ENTITY);
        }

        var reputationsTag = new CompoundTag();

        for (var entry : data.getAll().entrySet()) {
            var targetKey = serializeSubjectKey(entry.getKey());
            reputationsTag.putInt(targetKey, entry.getValue());
        }

        tag.put(KEY_REPUTATIONS, reputationsTag);

        return tag;
    }

    public static ReputationData deserialize(String subjectKey, CompoundTag tag) {
        var reputationSubject = deserializeSubjectKey(subjectKey);
        var data = new ReputationData(reputationSubject);
        var reputationsTag = tag.getCompound(KEY_REPUTATIONS);

        for (var key : reputationsTag.getAllKeys()) {
            var target = deserializeSubjectKey(key);
            var value = reputationsTag.getInt(key);
            data.getReputationsInternal().put(target, value);
        }

        return data;
    }

    public static String serializeSubjectKey(ReputationSubject reputationSubject) {
        return switch (reputationSubject) {
            case ReputationSubject.Faction(var factionId) -> FACTION_PREFIX + factionId.toString();
            case ReputationSubject.Entity(var uuid) -> ENTITY_PREFIX + uuid.toString();
        };
    }

    public static ReputationSubject deserializeSubjectKey(String key) {
        if (key.startsWith(FACTION_PREFIX)) {
            var factionId = ResourceLocation.parse(key.substring(FACTION_PREFIX.length()));
            return new ReputationSubject.Faction(factionId);
        } else if (key.startsWith(ENTITY_PREFIX)) {
            var uuid = UUID.fromString(key.substring(ENTITY_PREFIX.length()));
            return new ReputationSubject.Entity(uuid);
        } else {
            throw new IllegalArgumentException("Unknown reputation subject key format: " + key);
        }
    }
}
