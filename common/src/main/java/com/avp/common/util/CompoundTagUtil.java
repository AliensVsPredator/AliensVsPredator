package com.avp.common.util;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CompoundTagUtil {

    public static @Nullable UUID getUUIDOrNull(CompoundTag compoundTag, String key) {
        return compoundTag.hasUUID(key) ? compoundTag.getUUID(key) : null;
    }

    private CompoundTagUtil() {
        throw new UnsupportedOperationException();
    }
}
