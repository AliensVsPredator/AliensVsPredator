package com.lib.common.network;

import com.lib.common.gameplay.NBTSerializable;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

import com.avp.AVP;
import com.avp.common.network.packet.S2CEntityDataSyncPayload;
import com.avp.service.Services;

public class DataContainer implements NBTSerializable {

    private final Set<DataKey<?>> dirtyKeys;

    private final List<DataKey<?>> idToKey;

    private final Map<DataKey<?>, Integer> keyToId;

    private final Map<DataKey<?>, Object> values;

    public DataContainer() {
        this.dirtyKeys = new HashSet<>();
        this.idToKey = new ArrayList<>();
        this.keyToId = new HashMap<>();
        this.values = new HashMap<>();
    }

    public <T> DataAccessor.Builder<T> builder(String id) {
        return new DataAccessor.Builder<>(id, this);
    }

    public <T> DataAccessor<T> define(DataKey<T> key, T initialValue) {
        if (keyToId.containsKey(key)) {
            throw new IllegalStateException("Key already defined: " + key);
        }

        var id = idToKey.size();
        idToKey.add(key);
        keyToId.put(key, id);
        values.put(key, initialValue);

        return new DataAccessor<>(this, key, initialValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(DataKey<T> key) {
        return (T) values.get(key);
    }

    public <T> void set(DataKey<T> key, T value) {
        if (!Objects.equals(values.get(key), value)) {
            values.put(key, value);
            key.onChange().accept(value);

            if (key.streamCodec().isSome()) {
                //
                dirtyKeys.add(key);
            }
        }
    }

    public void set(int id, byte[] rawData) {
        @SuppressWarnings("unchecked")
        var key = (DataKey<Object>) idToKey.get(id);

        if (key == null) {
            return;
        }

        // TODO: unwrap is terrible here.
        @SuppressWarnings("unchecked")
        var codec = (StreamCodec<FriendlyByteBuf, Object>) key.streamCodec().unwrap();

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
            .filter(key -> key.streamCodec().isSome())
            .map(dataKey -> {
                var id = keyToId.get(dataKey);
                // Safe to unwrap here due to our earlier filter check.
                @SuppressWarnings("unchecked")
                var codec = (StreamCodec<FriendlyByteBuf, Object>) dataKey.streamCodec().unwrap();
                var value = get(dataKey);
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

    @Override
    public void load(CompoundTag compoundTag) {
        values.keySet().forEach(key -> {
            var id = key.id();

            key.codec().ifSome(codec -> {
                if (!compoundTag.contains(id)) {
                    return;
                }

                if (codec == Codec.BOOL) {
                    values.put(key, compoundTag.getBoolean(id));
                    return;
                }

                var tagType = compoundTag.getTagType(id);
                var value = switch (tagType) {
                    case Tag.TAG_BYTE -> compoundTag.getByte(id);
                    case Tag.TAG_SHORT -> compoundTag.getShort(id);
                    case Tag.TAG_INT -> compoundTag.getInt(id);
                    case Tag.TAG_LONG -> compoundTag.getLong(id);
                    case Tag.TAG_FLOAT -> compoundTag.getFloat(id);
                    case Tag.TAG_DOUBLE -> compoundTag.getDouble(id);
                    case Tag.TAG_STRING -> compoundTag.getString(id);
                    case Tag.TAG_BYTE_ARRAY -> compoundTag.getByteArray(id);
                    case Tag.TAG_INT_ARRAY -> compoundTag.getIntArray(id);
                    case Tag.TAG_LONG_ARRAY -> compoundTag.getLongArray(id);
                    default -> {
                        AVP.LOGGER.error("Failed to decode key '{}': Unrecognized Tag type: '{}'", id, tagType);
                        yield null;
                    }
                };

                if (value != null) {
                    @SuppressWarnings("unchecked")
                    var castKey = (DataKey<Object>) key;
                    values.put(castKey, value);
                }
            });
        });
    }

    @Override
    public void save(CompoundTag compoundTag) {
        values.forEach((key, value) -> {
            var id = key.id();
            var codecOption = key.codec();

            codecOption.ifSome(codec -> {
                switch (value) {
                    case Byte b -> compoundTag.putByte(id, b);
                    case Short s -> compoundTag.putShort(id, s);
                    case Integer i -> compoundTag.putInt(id, i);
                    case Boolean bool -> compoundTag.putBoolean(id, bool);
                    case Long l -> compoundTag.putLong(id, l);
                    case Float f -> compoundTag.putFloat(id, f);
                    case Double d -> compoundTag.putDouble(id, d);
                    case String s -> compoundTag.putString(id, s);
                    case null, default -> AVP.LOGGER.error("Failed to encode key '{}': Unrecognized value type. Value: '{}'", id, value);
                }
            });
        });
    }

    public enum SyncType {
        ALL,
        DIRTY,
    }
}
