package com.blib.internal.common.reputation.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationKey;

@ApiStatus.Internal
public final class ReputationDataSerializer {

    private static final String KEY_FACTIONS = "factions";

    private static final String KEY_ENTITIES = "entities";

    private ReputationDataSerializer() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag serialize(ReputationData data) {
        var tag = new CompoundTag();

        var factionsTag = new CompoundTag();
        var entitiesTag = new CompoundTag();

        for (var entry : data.getAll().entrySet()) {
            switch (entry.getKey()) {
                case ReputationKey.Faction(var factionId) -> factionsTag.putInt(factionId.toString(), entry.getValue());
                case ReputationKey.Entity(var uuid) -> entitiesTag.putInt(uuid.toString(), entry.getValue());
            }
        }

        if (!factionsTag.isEmpty()) {
            tag.put(KEY_FACTIONS, factionsTag);
        }

        if (!entitiesTag.isEmpty()) {
            tag.put(KEY_ENTITIES, entitiesTag);
        }

        return tag;
    }

    public static ReputationData deserialize(ReputationKey reputationKey, CompoundTag tag) {
        var data = new ReputationData(reputationKey);

        if (tag.contains(KEY_FACTIONS)) {
            var factionsTag = tag.getCompound(KEY_FACTIONS);

            for (var key : factionsTag.getAllKeys()) {
                var factionId = ResourceLocation.parse(key);
                var value = factionsTag.getInt(key);
                data.getReputationsInternal().put(new ReputationKey.Faction(factionId), value);
            }
        }

        if (tag.contains(KEY_ENTITIES)) {
            var entitiesTag = tag.getCompound(KEY_ENTITIES);

            for (var key : entitiesTag.getAllKeys()) {
                var uuid = UUID.fromString(key);
                var value = entitiesTag.getInt(key);
                data.getReputationsInternal().put(new ReputationKey.Entity(uuid), value);
            }
        }

        return data;
    }
}
