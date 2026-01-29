package com.blib.api.common.codec.v1;

import com.just.codec.Codec;
import com.just.codec.schema.CodecSchema;
import com.just.codec.stream.StreamCodec;
import com.just.core.functional.result.Result;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.Supplier;

import com.blib.api.common.codec.v1.adapter.DynamicOpsToCodecSchemaAdapter;
import com.blib.api.common.codec.v1.adapter.J2MCodecAdapter;
import com.blib.api.common.codec.v1.adapter.M2JCodecAdapter;
import com.blib.api.common.codec.v1.stream.adapter.J2MStreamCodecAdapter;
import com.blib.api.common.codec.v1.stream.adapter.M2JStreamCodecAdapter;
import com.blib.api.common.codec.v1.stream.impl.BlockPosStreamCodec;
import com.blib.api.common.codec.v1.stream.impl.DataComponentPatchStreamCodec;
import com.blib.api.common.codec.v1.stream.impl.DataComponentTypeStreamCodec;
import com.blib.api.common.codec.v1.stream.impl.DirectionStreamCodec;
import com.blib.api.common.codec.v1.stream.impl.ItemStackStreamCodec;
import com.blib.api.common.codec.v1.stream.impl.ResourceLocationStreamCodec;
import com.blib.api.common.codec.v1.stream.schema.ByteBufStreamCodecSchema;

public final class BLibCodecs {

    public static final Map<CodecSchema<?>, DynamicOps<?>> CODEC_SCHEMA_TO_DYNAMIC_OPS = Map.of(Schema.NBT, NbtOps.INSTANCE);

    public static final Map<DynamicOps<?>, CodecSchema<?>> DYNAMIC_OPS_TO_CODEC_SCHEMA = Map.of(NbtOps.INSTANCE, Schema.NBT);

    public static final Codec<EntityType<?>> ENTITY_TYPE = toJust(BuiltInRegistries.ENTITY_TYPE.byNameCodec());

    public static final Codec<ItemStack> ITEM_STACK = toJust(ItemStack.CODEC);

    public static <A> Codec<A> toJust(com.mojang.serialization.Codec<A> codec) {
        return new M2JCodecAdapter<>(codec);
    }

    public static <A> com.mojang.serialization.Codec<A> toMojang(Codec<A> codec) {
        return new J2MCodecAdapter<>(codec);
    }

    public static <T> Result<T, DataResult.Error<T>> toJustResult(DataResult<T> dataResult) {
        return dataResult.isSuccess()
            ? Result.ok(dataResult.result().get())
            : Result.err(dataResult.error().get());
    }

    public static <T> DataResult<T> toMojangResult(Result<T, ?> result, Supplier<String> errorSupplier) {
        return result.isOk()
            ? DataResult.success(result.unwrap())
            : DataResult.error(errorSupplier);
    }

    private BLibCodecs() {}

    public static final class Schema {

        public static final CodecSchema<Tag> NBT = new DynamicOpsToCodecSchemaAdapter<>(NbtOps.INSTANCE);

        public static final ByteBufStreamCodecSchema BYTE_BUF = new ByteBufStreamCodecSchema();

        private Schema() {}
    }

    public static final class Stream {

        public static final StreamCodec<BlockPos> BLOCK_POS = new BlockPosStreamCodec();

        public static final StreamCodec<DataComponentPatch> DATA_COMPONENT_PATCH = new DataComponentPatchStreamCodec();

        public static final StreamCodec<DataComponentType<?>> DATA_COMPONENT_TYPE = new DataComponentTypeStreamCodec();

        public static final StreamCodec<Direction> DIRECTION = new DirectionStreamCodec();

        public static final StreamCodec<ItemStack> ITEM_STACK = new ItemStackStreamCodec();

        public static final StreamCodec<ResourceLocation> RESOURCE_LOCATION = new ResourceLocationStreamCodec();

        public static <A> net.minecraft.network.codec.StreamCodec<FriendlyByteBuf, A> toMojang(StreamCodec<A> codec) {
            return new J2MStreamCodecAdapter<>(codec);
        }

        public static <A> StreamCodec<A> fromMojang(net.minecraft.network.codec.StreamCodec<FriendlyByteBuf, A> codec) {
            return new M2JStreamCodecAdapter<>(codec);
        }

        private Stream() {}
    }
}
