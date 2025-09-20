package com.lib.common.util.codec.schema;

import com.just.codec.schema.CodecSchema;
import com.lib.common.util.codec.adapter.DynamicOpsToCodecSchemaAdapter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class CodecSchemas {

    public static final CodecSchema<Tag> NBT = new DynamicOpsToCodecSchemaAdapter<>(NbtOps.INSTANCE);

}
