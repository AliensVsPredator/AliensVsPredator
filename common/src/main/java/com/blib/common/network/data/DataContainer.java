package com.blib.common.network.data;

import com.just.codec.stream.StreamCodec;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.blib.BLib;
import com.blib.common.gameplay.model.NBTSerializable;
import com.blib.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.common.registry.DataKeyRegistry;
import com.blib.common.util.codec.stream.schema.StreamCodecSchemas;

public class DataContainer implements NBTSerializable {

    private final Set<DataKey<?>> dirtyKeys;

    private final Map<DataKey<?>, Consumer<?>> onChangeCallbacks;

    private final Map<DataKey<?>, Consumer<?>> onLoadCallbacks;

    private final Map<DataKey<?>, Object> values;

    public DataContainer() {
        this.dirtyKeys = new HashSet<>();
        this.onChangeCallbacks = new HashMap<>();
        this.onLoadCallbacks = new HashMap<>();
        this.values = new HashMap<>();
    }

    public <T> void setOnChangeCallback(DataKey<T> dataKey, Consumer<T> callback) {
        onChangeCallbacks.put(dataKey, callback);
    }

    public <T> void setOnLoadCallback(DataKey<T> dataKey, Consumer<T> callback) {
        onLoadCallbacks.put(dataKey, callback);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(DataKey<T> key) {
        return (T) values.getOrDefault(key, key.initialValue());
    }

    public <T> void set(DataKey<T> key, T value) {
        if (!Objects.equals(values.get(key), value)) {
            values.put(key, value);

            @SuppressWarnings("unchecked")
            var onChangeCallback = (Consumer<T>) onChangeCallbacks.get(key);

            if (onChangeCallback != null) {
                onChangeCallback.accept(value);
            }

            if (key.streamCodec().isSome()) {
                //
                dirtyKeys.add(key);
            }
        }
    }

    public void set(int id, byte[] rawData) {
        @SuppressWarnings("unchecked")
        var key = (DataKey<Object>) DataKeyRegistry.getDataKeyOrNull(id);

        if (key == null) {
            return;
        }

        // TODO: unwrap is terrible here.
        var codec = key.streamCodec().unwrap();

        var byteBuf = Unpooled.wrappedBuffer(rawData);
        var friendlyByteBuf = new FriendlyByteBuf(byteBuf);

        var value = codec.decode(StreamCodecSchemas.BYTE_BUF, friendlyByteBuf);

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
            .filter(key -> key.streamCodec().isSome() && DataKeyRegistry.getIdOrNull(key.id()) != null)
            .map(key -> {
                var id = Objects.requireNonNull(DataKeyRegistry.getIdOrNull(key.id()));
                // Safe to unwrap here due to our earlier filter check.
                @SuppressWarnings("unchecked")
                var codec = (StreamCodec<Object>) key.streamCodec().unwrap();
                var value = get(key);
                var friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());

                codec.encode(StreamCodecSchemas.BYTE_BUF, friendlyByteBuf, value);

                return Map.entry(id, ByteBufUtil.getBytes(friendlyByteBuf));
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var rawDataSyncMap = new RawDataSyncMap(dataMap);

        BLib.MOD.networking()
            .sendToAllClients(
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
            @SuppressWarnings("unchecked")
            var onLoadCallback = (Consumer<Object>) onLoadCallbacks.get(key);

            key.persistenceMetadata().ifSome(persistenceMetadata -> {
                var id = persistenceMetadata.key();

                if (!compoundTag.contains(id)) {
                    return;
                }

                var codec = persistenceMetadata.codec();

                if (codec == Codec.BOOL) {
                    var value = compoundTag.getBoolean(id);
                    values.put(key, value);

                    if (onLoadCallback != null) {
                        onLoadCallback.accept(value);
                    }

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
                        BLib.LOGGER.error("Failed to decode key '{}': Unrecognized Tag type: '{}'", id, tagType);
                        yield null;
                    }
                };

                if (value != null) {
                    @SuppressWarnings("unchecked")
                    var typedKey = (DataKey<Object>) key;
                    values.put(typedKey, value);

                    if (onLoadCallback != null) {
                        onLoadCallback.accept(value);
                    }
                }
            });
        });
    }

    @Override
    public void save(CompoundTag compoundTag) {
        values.forEach((key, value) -> {
            var persistenceMetadataOption = key.persistenceMetadata();

            persistenceMetadataOption.ifSome(persistenceMetadata -> {
                var id = persistenceMetadata.key();

                switch (value) {
                    case Byte b -> compoundTag.putByte(id, b);
                    case Short s -> compoundTag.putShort(id, s);
                    case Integer i -> compoundTag.putInt(id, i);
                    case Boolean bool -> compoundTag.putBoolean(id, bool);
                    case Long l -> compoundTag.putLong(id, l);
                    case Float f -> compoundTag.putFloat(id, f);
                    case Double d -> compoundTag.putDouble(id, d);
                    case String s -> compoundTag.putString(id, s);
                    case null, default -> BLib.LOGGER.error("Failed to encode key '{}': Unrecognized value type. Value: '{}'", id, value);
                }
            });
        });
    }

    public enum SyncType {
        ALL,
        DIRTY,
    }
}
