package com.lib.common.network;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.avp.common.network.packet.S2CEntityDataSyncPayload;
import com.avp.service.Services;

public class SyncedDataContainer {

    private final Set<AbstractSyncedDataKey<?>> dirtyKeys;

    private final List<AbstractSyncedDataKey<?>> idToKey;

    private final Map<AbstractSyncedDataKey<?>, Integer> keyToId;

    private final Map<AbstractSyncedDataKey<?>, Object> values;

    public SyncedDataContainer() {
        this.dirtyKeys = new HashSet<>();
        this.idToKey = new ArrayList<>();
        this.keyToId = new HashMap<>();
        this.values = new HashMap<>();
    }

    public <T> SyncedDataAccessor<T> define(AbstractSyncedDataKey<T> key, T initialValue) {
        if (keyToId.containsKey(key)) {
            throw new IllegalStateException("Key already defined: " + key);
        }

        var id = idToKey.size();
        idToKey.add(key);
        keyToId.put(key, id);
        values.put(key, initialValue);

        return new SyncedDataAccessor<>(this, key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(AbstractSyncedDataKey<T> key) {
        return (T) values.get(key);
    }

    public <T> void set(AbstractSyncedDataKey<T> key, T value) {
        if (!Objects.equals(values.get(key), value)) {
            values.put(key, value);
            dirtyKeys.add(key);
        }
    }

    public void set(int id, byte[] rawData) {
        @SuppressWarnings("unchecked")
        var key = (AbstractSyncedDataKey<Object>) idToKey.get(id);

        if (key == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        var codec = (StreamCodec<FriendlyByteBuf, Object>) key.codec();

        var byteBuf = Unpooled.wrappedBuffer(rawData);
        var friendlyByteBuf = new FriendlyByteBuf(byteBuf);

        var value = codec.decode(friendlyByteBuf);

        // 5. Store the value
        set(key, value);
    }

    public void syncToClient(Entity entity, SyncType syncType) {
        if (entity.level().isClientSide) {
            return;
        }

        if (syncType == SyncType.DIRTY && !isDirty()) {
            return;
        }

        var dataMap = values.keySet()
            .stream()
            .filter(key -> syncType != SyncType.DIRTY || dirtyKeys.contains(key))
            .map(syncedDataKey -> {
                var id = keyToId.get(syncedDataKey);
                @SuppressWarnings("unchecked")
                var codec = (StreamCodec<FriendlyByteBuf, Object>) syncedDataKey.codec();
                var value = get(syncedDataKey);
                var friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());

                codec.encode(friendlyByteBuf, value);

                return Map.entry(id, ByteBufUtil.getBytes(friendlyByteBuf));
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var rawDataSyncMap = new RawDataSyncMap(dataMap);

        Services.SERVER_NETWORKING.sendToAllClients(
            entity.level().getServer(),
            new S2CEntityDataSyncPayload(entity.getId(), rawDataSyncMap)
        );

        dirtyKeys.clear();
    }

    public boolean isDirty() {
        return !dirtyKeys.isEmpty();
    }

    public enum SyncType {
        ALL,
        DIRTY,
    }
}
