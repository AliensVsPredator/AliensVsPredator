package com.blib.internal.common.faction.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.faction.v1.FactionData;

@ApiStatus.Internal
public final class FactionDataSerializer {

    private static final String KEY_TYPE = "type";

    private static final String KEY_DATA = "data";

    private FactionDataSerializer() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag serialize(FactionData factionData, ResourceLocation typeId) {
        var tag = new CompoundTag();
        tag.putString(KEY_TYPE, typeId.toString());

        var dataTag = new CompoundTag();
        factionData.save(dataTag);
        tag.put(KEY_DATA, dataTag);

        return tag;
    }

    public static ResourceLocation deserializeTypeId(CompoundTag tag) {
        return ResourceLocation.parse(tag.getString(KEY_TYPE));
    }

    public static CompoundTag deserializeData(CompoundTag tag) {
        return tag.getCompound(KEY_DATA);
    }
}
