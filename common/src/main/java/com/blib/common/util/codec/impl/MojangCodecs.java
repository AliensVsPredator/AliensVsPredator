package com.blib.common.util.codec.impl;

import com.blib.common.util.codec.CodecUtil;
import com.just.codec.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class MojangCodecs {

    public static final Codec<EntityType<?>> ENTITY_TYPE = CodecUtil.adapt(BuiltInRegistries.ENTITY_TYPE.byNameCodec());

    public static final Codec<ItemStack> ITEM_STACK = CodecUtil.adapt(ItemStack.CODEC);

}
