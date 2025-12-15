package com.blib.common.util.codec.schema;

import com.just.codec.schema.CodecSchema;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import com.blib.common.util.codec.adapter.DynamicOpsToCodecSchemaAdapter;

public class CodecSchemas {

    public static final CodecSchema<Tag> NBT = new DynamicOpsToCodecSchemaAdapter<>(NbtOps.INSTANCE);

}
