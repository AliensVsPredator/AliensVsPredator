package com.blib.api.common.codec.v1.schema;

import com.just.codec.schema.CodecSchema;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import com.blib.api.common.codec.v1.adapter.DynamicOpsToCodecSchemaAdapter;

public class CodecSchemas {

    public static final CodecSchema<Tag> NBT = new DynamicOpsToCodecSchemaAdapter<>(NbtOps.INSTANCE);

}
