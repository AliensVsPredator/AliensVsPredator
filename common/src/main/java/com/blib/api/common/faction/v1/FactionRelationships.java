package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.blib.api.common.util.v1.Dirty;
import com.blib.internal.common.faction.BLibFactionManager;

public class FactionRelationships implements WritableFactionRelationships, Dirty {

    private final ResourceLocation id;

    private final Set<FactionMember> members;

    private boolean dirty;

    @ApiStatus.Internal
    public FactionRelationships(ResourceLocation id) {
        this(id, new LinkedHashSet<>());
    }

    @ApiStatus.Internal
    public FactionRelationships(ResourceLocation id, Set<FactionMember> members) {
        this.id = id;
        this.members = members;
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
    public boolean addMember(FactionMember member) {
        if (member instanceof FactionMember.SubFaction(var factionId)) {
            if (!BLibFactionManager.INSTANCE.exists(factionId)) {
                return false;
            }
        }

        if (members.add(member)) {
            markDirty();
            BLibFactionManager.INSTANCE.onMemberChanged(id, member, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean addEntity(Entity entity) {
        var member = FactionMember.entity(entity);

        if (members.add(member)) {
            markDirty();
            BLibFactionManager.INSTANCE.onEntityMemberAdded(id, member, entity);
            return true;
        }

        return false;
    }

    @Override
    public boolean removeMember(FactionMember member) {
        if (members.remove(member)) {
            markDirty();
            BLibFactionManager.INSTANCE.onMemberChanged(id, member, false);
            return true;
        }

        return false;
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
