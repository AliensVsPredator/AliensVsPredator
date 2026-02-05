package com.blib.internal.common.faction.serializer;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.faction.v1.FactionRelationships;

@ApiStatus.Internal
public final class FactionRelationshipsSerializer {

    private static final String KEY_ID = "id";

    private static final String KEY_PARENT = "parent";

    private static final String KEY_ENTITIES = "entities";

    private static final String KEY_SUBFACTIONS = "subfactions";

    private FactionRelationshipsSerializer() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag serialize(FactionRelationships relationships) {
        var tag = new CompoundTag();
        tag.putString(KEY_ID, relationships.getId().toString());

        var parentId = relationships.getParentFactionId();
        tag.putString(KEY_PARENT, parentId != null ? parentId.toString() : "");

        var entityList = new ListTag();
        var subfactionList = new ListTag();

        for (var member : relationships.getMembers()) {
            switch (member) {
                case FactionMember.Entity entityMember ->
                    entityList.add(new IntArrayTag(UUIDUtil.uuidToIntArray(entityMember.uuid())));
                case FactionMember.SubFaction subFactionMember ->
                    subfactionList.add(StringTag.valueOf(subFactionMember.factionId().toString()));
            }
        }

        tag.put(KEY_ENTITIES, entityList);
        tag.put(KEY_SUBFACTIONS, subfactionList);

        return tag;
    }

    public static FactionRelationships deserialize(CompoundTag tag) {
        var id = ResourceLocation.parse(tag.getString(KEY_ID));
        var relationships = new FactionRelationships(id);

        var parentStr = tag.getString(KEY_PARENT);
        if (!parentStr.isEmpty()) {
            relationships.setParentFactionId(ResourceLocation.parse(parentStr));
        }

        var entityList = tag.getList(KEY_ENTITIES, Tag.TAG_INT_ARRAY);
        for (int i = 0; i < entityList.size(); i++) {
            var uuid = UUIDUtil.uuidFromIntArray(entityList.getIntArray(i));
            relationships.addMember(new FactionMember.Entity(uuid));
        }

        var subfactionList = tag.getList(KEY_SUBFACTIONS, Tag.TAG_STRING);
        for (int i = 0; i < subfactionList.size(); i++) {
            relationships.addMember(new FactionMember.SubFaction(ResourceLocation.parse(subfactionList.getString(i))));
        }

        return relationships;
    }
}
