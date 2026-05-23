package com.blib.internal.common.faction.serializer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.internal.common.faction.FactionRelationshipTable;

@ApiStatus.Internal
public final class FactionRelationshipTableSerializer {

    private static final String KEY_FIRST = "first";

    private static final String KEY_SECOND = "second";

    private static final String KEY_STATE = "state";

    private FactionRelationshipTableSerializer() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag serialize(FactionRelationshipTable table) {
        var rootTag = new CompoundTag();
        var edgeList = new ListTag();

        for (var entry : table.getAllEdges().entrySet()) {
            var edgeTag = new CompoundTag();
            edgeTag.putString(KEY_FIRST, entry.getKey().first().toString());
            edgeTag.putString(KEY_SECOND, entry.getKey().second().toString());
            edgeTag.putString(KEY_STATE, entry.getValue().name());
            edgeList.add(edgeTag);
        }

        rootTag.put("edges", edgeList);

        return rootTag;
    }

    public static void deserialize(CompoundTag tag, FactionRelationshipTable table) {
        if (!tag.contains("edges")) {
            return;
        }

        var edgeList = tag.getList("edges", CompoundTag.TAG_COMPOUND);

        for (var i = 0; i < edgeList.size(); i++) {
            var edgeTag = edgeList.getCompound(i);
            var first = ResourceLocation.parse(edgeTag.getString(KEY_FIRST));
            var second = ResourceLocation.parse(edgeTag.getString(KEY_SECOND));
            var state = RelationshipState.valueOf(edgeTag.getString(KEY_STATE));

            table.setRelationship(first, second, state);
        }

        table.clearDirty();
    }
}
