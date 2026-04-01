package com.blib.internal.common.faction.serializer;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedHashSet;

import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.faction.v1.FactionMembership;

@ApiStatus.Internal
public final class FactionMembershipSerializer {

    private static final String KEY_ID = "id";

    private static final String KEY_ENTITIES = "entities";

    private FactionMembershipSerializer() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag serialize(FactionMembership membership) {
        var tag = new CompoundTag();
        tag.putString(KEY_ID, membership.getId().toString());

        var entityList = new ListTag();

        for (var member : membership.getMembers()) {
            if (member instanceof FactionMember.Entity entityMember) {
                entityList.add(new IntArrayTag(UUIDUtil.uuidToIntArray(entityMember.uuid())));
            }
        }

        tag.put(KEY_ENTITIES, entityList);

        return tag;
    }

    public static FactionMembership deserialize(CompoundTag tag) {
        var id = ResourceLocation.parse(tag.getString(KEY_ID));
        var members = new LinkedHashSet<FactionMember>();

        var entityList = tag.getList(KEY_ENTITIES, Tag.TAG_INT_ARRAY);

        for (var i = 0; i < entityList.size(); i++) {
            var uuid = UUIDUtil.uuidFromIntArray(entityList.getIntArray(i));
            members.add(new FactionMember.Entity(uuid));
        }

        return new FactionMembership(id, members);
    }
}
