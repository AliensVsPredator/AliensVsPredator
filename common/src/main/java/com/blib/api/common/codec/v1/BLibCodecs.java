package com.blib.api.common.codec.v1;

import com.just.codec.Codec;
import com.just.codec.schema.CodecSchema;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import com.just.core.functional.result.Result;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.blib.api.common.codec.v1.adapter.DynamicOpsToCodecSchemaAdapter;
import com.blib.api.common.codec.v1.stream.adapter.J2MStreamCodecAdapter;
import com.blib.api.common.codec.v1.stream.adapter.M2JStreamCodecAdapter;
import com.blib.api.common.codec.v1.stream.schema.ByteBufStreamCodecSchema;
import com.blib.api.common.data_component.v1.DataComponentPatchAccessor;

/**
 * Central API for codec operations in BLib.
 * <p>
 * Provides pre-built codecs, schema access, and adapter utilities for bridging between Mojang's serialization system
 * and the Just codec library.
 */
public final class BLibCodecs {

    private BLibCodecs() {}

    // ===== INTERNAL MAPPINGS =====

    private static final Map<CodecSchema<?>, DynamicOps<?>> CODEC_SCHEMA_TO_DYNAMIC_OPS = Map.of(Schema.NBT, NbtOps.INSTANCE);

    private static final Map<DynamicOps<?>, CodecSchema<?>> DYNAMIC_OPS_TO_CODEC_SCHEMA = Map.of(NbtOps.INSTANCE, Schema.NBT);

    // ===== REGULAR CODECS (Just library) =====

    public static final Codec<EntityType<?>> ENTITY_TYPE = adapt(BuiltInRegistries.ENTITY_TYPE.byNameCodec());

    public static final Codec<ItemStack> ITEM_STACK = adapt(ItemStack.CODEC);

    // ===== CODEC ADAPTERS =====

    /**
     * Adapts a Mojang codec to a Just codec.
     */
    public static <A> Codec<A> adapt(com.mojang.serialization.Codec<A> codec) {
        return new Codec<>() {

            @Override
            public <T> T encode(CodecSchema<T> codecSchema, A value) {
                @SuppressWarnings("unchecked")
                var ops = (DynamicOps<T>) CODEC_SCHEMA_TO_DYNAMIC_OPS.get(codecSchema);
                var result = codec.encodeStart(ops, value);

                return result.isSuccess()
                    ? result.result().get()
                    : null;
            }

            @Override
            public <T> Result<A, T> decode(CodecSchema<T> codecSchema, T input) {
                @SuppressWarnings("unchecked")
                var ops = (DynamicOps<T>) CODEC_SCHEMA_TO_DYNAMIC_OPS.get(codecSchema);
                var result = codec.decode(ops, input);

                return result.isSuccess()
                    ? Result.ok(result.result().get().getFirst())
                    : Result.err(input);
            }
        };
    }

    /**
     * Adapts a Just codec to a Mojang codec.
     */
    public static <A> com.mojang.serialization.Codec<A> adapt(Codec<A> codec) {
        return new com.mojang.serialization.Codec<>() {

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                @SuppressWarnings("unchecked")
                var codecSchema = (CodecSchema<T>) DYNAMIC_OPS_TO_CODEC_SCHEMA.get(ops);
                var result = codec.decode(codecSchema, input);

                return result.isOk()
                    ? DataResult.success(new Pair<>(result.unwrap(), input))
                    : DataResult.error(() -> "Failed to decode");
            }

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                @SuppressWarnings("unchecked")
                var codecSchema = (CodecSchema<T>) DYNAMIC_OPS_TO_CODEC_SCHEMA.get(ops);
                return DataResult.success(codec.encode(codecSchema, input));
            }
        };
    }

    // ===== RESULT CONVERTERS =====

    /**
     * Converts a Mojang DataResult to a Just Result.
     */
    public static <T> Result<T, DataResult.Error<T>> toJustResult(DataResult<T> dataResult) {
        return dataResult.isSuccess()
            ? Result.ok(dataResult.result().get())
            : Result.err(dataResult.error().get());
    }

    /**
     * Converts a Just Result to a Mojang DataResult.
     */
    public static <T> DataResult<T> toMojangResult(Result<T, ?> result, Supplier<String> errorSupplier) {
        return result.isOk()
            ? DataResult.success(result.unwrap())
            : DataResult.error(errorSupplier);
    }

    // ===== SCHEMAS =====

    /**
     * Codec schemas for serialization formats.
     */
    public static final class Schema {

        private Schema() {}

        /**
         * Codec schema for NBT serialization.
         */
        public static final CodecSchema<Tag> NBT = new DynamicOpsToCodecSchemaAdapter<>(NbtOps.INSTANCE);

        /**
         * Stream codec schema for ByteBuf serialization.
         */
        public static final ByteBufStreamCodecSchema BYTE_BUF = new ByteBufStreamCodecSchema();
    }

    // ===== STREAM CODECS =====

    /**
     * Stream codecs for network serialization.
     */
    public static final class Stream {

        private Stream() {}

        // ===== PRE-BUILT STREAM CODECS =====

        public static final StreamCodec<BlockPos> BLOCK_POS = new StreamCodec<>() {

            @Override
            public @NotNull <T> BlockPos decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
                return BlockPos.of(streamCodecSchema.readLong(input));
            }

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull BlockPos value) {
                streamCodecSchema.writeLong(input, value.asLong());
            }
        };

        public static final StreamCodec<DataComponentPatch> DATA_COMPONENT_PATCH = new StreamCodec<>() {

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T input, @NotNull DataComponentPatch patch) {
                if (patch.isEmpty()) {
                    schema.writeInt(input, 0);
                    schema.writeInt(input, 0);
                    return;
                }

                var present = new ArrayList<Map.Entry<DataComponentType<?>, Optional<?>>>();
                var removed = new ArrayList<DataComponentType<?>>();
                var map = ((DataComponentPatchAccessor) (Object) patch).blib$getMap();

                for (var entry : map.reference2ObjectEntrySet()) {
                    if (entry.getValue().isPresent()) {
                        present.add(entry);
                    } else {
                        removed.add(entry.getKey());
                    }
                }

                schema.writeInt(input, present.size());
                schema.writeInt(input, removed.size());

                for (var entry : present) {
                    @SuppressWarnings("unchecked")
                    var type = (DataComponentType<Object>) entry.getKey();
                    var value = entry.getValue().orElseThrow();

                    schema.write(input, DATA_COMPONENT_TYPE, type);

                    encodeComponent(input, type, value);
                }

                for (var type : removed) {
                    schema.write(input, DATA_COMPONENT_TYPE, type);
                }
            }

            @Override
            public <T> @NotNull DataComponentPatch decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
                int presentCount = schema.readInt(input);
                int removedCount = schema.readInt(input);

                if (presentCount == 0 && removedCount == 0) {
                    return DataComponentPatch.EMPTY;
                }

                var map = new Reference2ObjectArrayMap<DataComponentType<?>, Optional<?>>(Math.min(presentCount + removedCount, 65536));

                for (int i = 0; i < presentCount; i++) {
                    var type = schema.read(input, DATA_COMPONENT_TYPE);
                    var value = decodeComponent(input, type);
                    map.put(type, Optional.ofNullable(value));
                }

                for (int i = 0; i < removedCount; i++) {
                    var type = schema.read(input, DATA_COMPONENT_TYPE);
                    map.put(type, Optional.empty());
                }

                return DataComponentPatchAccessor.blib$construct(map);
            }

            private static <A, T> void encodeComponent(T input, DataComponentType<A> type, A value) {
                if (input instanceof RegistryFriendlyByteBuf byteBuf) {
                    type.streamCodec().encode(byteBuf, value);
                    return;
                }

                throw new UnsupportedOperationException();
            }

            private static <A, T> @Nullable A decodeComponent(T input, DataComponentType<A> type) {
                if (input instanceof RegistryFriendlyByteBuf byteBuf) {
                    return type.streamCodec().decode(byteBuf);
                }

                return null;
            }
        };

        public static final StreamCodec<DataComponentType<?>> DATA_COMPONENT_TYPE = new StreamCodec<>() {

            @Override
            public @NotNull <T> DataComponentType<?> decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
                var typeId = streamCodecSchema.readVarInt(input);
                return BuiltInRegistries.DATA_COMPONENT_TYPE.byIdOrThrow(typeId);
            }

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull DataComponentType<?> value) {
                var typeId = BuiltInRegistries.DATA_COMPONENT_TYPE.getIdOrThrow(value);
                streamCodecSchema.writeVarInt(input, typeId);
            }
        };

        public static final StreamCodec<Direction> DIRECTION = new StreamCodec<>() {

            @Override
            public @NotNull <T> Direction decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
                return Direction.from3DDataValue(streamCodecSchema.readByte(input));
            }

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull Direction value) {
                streamCodecSchema.writeByte(input, (byte) value.get3DDataValue());
            }
        };

        public static final StreamCodec<ItemStack> ITEM_STACK = new StreamCodec<>() {

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull ItemStack value) {
                if (value.isEmpty()) {
                    streamCodecSchema.writeVarInt(input, 0);
                } else {
                    streamCodecSchema.writeVarInt(input, value.getCount());

                    var itemId = BuiltInRegistries.ITEM.getIdOrThrow(value.getItem());
                    streamCodecSchema.writeVarInt(input, itemId);

                    streamCodecSchema.write(input, DATA_COMPONENT_PATCH, value.getComponentsPatch());
                }
            }

            @Override
            public @NotNull <T> ItemStack decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
                var size = streamCodecSchema.readVarInt(input);

                if (size <= 0) {
                    return ItemStack.EMPTY;
                }

                var itemHolder = BuiltInRegistries.ITEM.asHolderIdMap().byIdOrThrow(streamCodecSchema.readVarInt(input));

                var datacomponentpatch = DATA_COMPONENT_PATCH.decode(streamCodecSchema, input);

                return new ItemStack(itemHolder, size, datacomponentpatch);
            }
        };

        public static final StreamCodec<ResourceLocation> RESOURCE_LOCATION = new StreamCodec<>() {

            @Override
            public @NotNull <T> ResourceLocation decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
                return ResourceLocation.parse(streamCodecSchema.read(input, StreamCodecs.STRING_UTF8));
            }

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull ResourceLocation value) {
                streamCodecSchema.write(input, StreamCodecs.STRING_UTF8, value.toString());
            }
        };

        // ===== STREAM CODEC ADAPTERS =====

        /**
         * Adapts a Just stream codec to a Minecraft stream codec.
         */
        public static <A> net.minecraft.network.codec.StreamCodec<FriendlyByteBuf, A> toMinecraft(StreamCodec<A> codec) {
            return new J2MStreamCodecAdapter<>(codec);
        }

        /**
         * Adapts a Minecraft stream codec to a Just stream codec.
         */
        public static <A> StreamCodec<A> fromMinecraft(net.minecraft.network.codec.StreamCodec<FriendlyByteBuf, A> codec) {
            return new M2JStreamCodecAdapter<>(codec);
        }
    }
}
