package com.blib.common.util.codec.schema;

import com.blib.common.util.codec.adapter.DynamicOpsToCodecSchemaAdapter;
import com.just.codec.schema.CodecSchema;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class CodecSchemas {

    public static final CodecSchema<Tag> NBT = new DynamicOpsToCodecSchemaAdapter<>(NbtOps.INSTANCE);

}
