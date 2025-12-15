package com.blib.common.util.codec.stream.impl;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import com.blib.common.gameplay.model.DataComponentPatchAccessor;
import com.blib.internal.mixin.MixinDataComponentPatch;

public class MojangStreamCodecs {

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

            return MixinDataComponentPatch.blib$construct(map);
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
}
