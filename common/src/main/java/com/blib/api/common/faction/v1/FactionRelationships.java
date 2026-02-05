package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.blib.api.common.util.v1.Dirty;
import com.blib.internal.common.faction.BLibFactionManager;

public class FactionRelationships implements WritableFaction, Dirty {

    private final ResourceLocation id;

    private final Set<FactionMember> members = new LinkedHashSet<>();

    private @Nullable ResourceLocation parentFactionId;

    private boolean dirty;

    @ApiStatus.Internal
    public FactionRelationships(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean hasMember(FactionMember member) {
        return members.contains(member);
    }

    @Override
    public Set<FactionMember> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public void addMember(FactionMember member) {
        if (member instanceof FactionMember.SubFaction(var factionId)) {
            if (!BLibFactionManager.INSTANCE.exists(factionId)) {
                throw new IllegalArgumentException(
                    "Cannot add subfaction member referencing non-existent faction '" + factionId + "'"
                );
            }
        }

        if (members.add(member)) {
            markDirty();
            BLibFactionManager.INSTANCE.onMemberChanged(id, member, true);
        }
    }

    @Override
    public void removeMember(FactionMember member) {
        if (members.remove(member)) {
            markDirty();
            BLibFactionManager.INSTANCE.onMemberChanged(id, member, false);
        }
    }

    public @Nullable ResourceLocation getParentFactionId() {
        return parentFactionId;
    }

    public void setParentFactionId(@Nullable ResourceLocation parentFactionId) {
        this.parentFactionId = parentFactionId;
        markDirty();
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    @ApiStatus.Internal
    public void clearDirty() {
        dirty = false;
    }
}
