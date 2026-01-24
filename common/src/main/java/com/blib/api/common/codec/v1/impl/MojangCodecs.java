package com.blib.api.common.codec.v1.impl;

import com.just.codec.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import com.blib.api.common.codec.v1.CodecUtil;

public class MojangCodecs {

    public static final Codec<EntityType<?>> ENTITY_TYPE = CodecUtil.adapt(BuiltInRegistries.ENTITY_TYPE.byNameCodec());

    public static final Codec<ItemStack> ITEM_STACK = CodecUtil.adapt(ItemStack.CODEC);

}
