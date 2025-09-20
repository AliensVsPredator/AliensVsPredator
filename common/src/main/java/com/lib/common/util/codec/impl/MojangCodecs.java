package com.lib.common.util.codec.impl;

import com.just.codec.Codec;
import com.lib.common.util.codec.CodecUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public class MojangCodecs {

    public static final Codec<EntityType<?>> ENTITY_TYPE = CodecUtil.adapt(BuiltInRegistries.ENTITY_TYPE.byNameCodec());

}
