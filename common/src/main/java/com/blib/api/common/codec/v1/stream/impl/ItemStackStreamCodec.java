package com.blib.api.common.codec.v1.stream.impl;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;

public class ItemStackStreamCodec implements StreamCodec<ItemStack> {

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull ItemStack value) {
        if (value.isEmpty()) {
            streamCodecSchema.writeVarInt(input, 0);
        } else {
            streamCodecSchema.writeVarInt(input, value.getCount());

            var itemId = BuiltInRegistries.ITEM.getIdOrThrow(value.getItem());
            streamCodecSchema.writeVarInt(input, itemId);

            streamCodecSchema.write(input, BLibCodecs.Stream.DATA_COMPONENT_PATCH, value.getComponentsPatch());
        }
    }

    @Override
    public @NotNull <T> ItemStack decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
        var size = streamCodecSchema.readVarInt(input);

        if (size <= 0) {
            return ItemStack.EMPTY;
        }

        var itemHolder = BuiltInRegistries.ITEM.asHolderIdMap().byIdOrThrow(streamCodecSchema.readVarInt(input));

        var datacomponentpatch = BLibCodecs.Stream.DATA_COMPONENT_PATCH.decode(streamCodecSchema, input);

        return new ItemStack(itemHolder, size, datacomponentpatch);
    }

}
