package com.blib.internal.common.faction.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.common.faction.BLibFactionData;

@ApiStatus.Internal
public final class FactionDataSerializer {

    private static final String KEY_TYPE = "type";

    private static final String KEY_BLIB_DATA = "blib_data";

    private FactionDataSerializer() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag serialize(BLibFactionData internalData, ResourceLocation typeId) {
        var tag = new CompoundTag();
        tag.putString(KEY_TYPE, typeId.toString());

        var blibDataTag = new CompoundTag();
        internalData.save(blibDataTag);
        tag.put(KEY_BLIB_DATA, blibDataTag);

        return tag;
    }

    public static ResourceLocation deserializeTypeId(CompoundTag tag) {
        return ResourceLocation.parse(tag.getString(KEY_TYPE));
    }

    public static CompoundTag deserializeBlibData(CompoundTag tag) {
        return tag.getCompound(KEY_BLIB_DATA);
    }
}
