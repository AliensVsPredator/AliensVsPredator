package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.blib.api.common.util.v1.Dirty;

public class FactionRelationships implements WritableFaction, Dirty {

    @ApiStatus.Internal
    @FunctionalInterface
    public interface MembershipListener {

        void onMemberChanged(ResourceLocation factionId, FactionMember member, boolean added);
    }

    private final ResourceLocation id;

    private final Set<FactionMember> members = new LinkedHashSet<>();

    private @Nullable ResourceLocation parentFactionId;

    private @Nullable MembershipListener membershipListener;

    private boolean dirty;

    @ApiStatus.Internal
    public FactionRelationships(ResourceLocation id) {
        this.id = id;
    }

    @ApiStatus.Internal
    public void setMembershipListener(@Nullable MembershipListener listener) {
        this.membershipListener = listener;
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
        if (members.add(member)) {
            markDirty();

            if (membershipListener != null) {
                membershipListener.onMemberChanged(id, member, true);
            }
        }
    }

    @Override
    public void removeMember(FactionMember member) {
        if (members.remove(member)) {
            markDirty();

            if (membershipListener != null) {
                membershipListener.onMemberChanged(id, member, false);
            }
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
