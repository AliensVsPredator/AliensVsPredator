package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.blib.api.common.event.v1.BLibFactionDataChangedEvent;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.internal.common.event.BLibGlobalEvents;
import com.blib.internal.common.faction.BLibFactionData;

public class Faction<T extends FactionData> {

    private final ResourceLocation id;

    private final ResourceLocation typeId;

    private final FactionMembership membership;

    private final BLibFactionData internalData;

    @ApiStatus.Internal
    public Faction(
        ResourceLocation id,
        ResourceLocation typeId,
        FactionMembership membership,
        BLibFactionData internalData
    ) {
        this.id = id;
        this.typeId = typeId;
        this.membership = membership;
        this.internalData = internalData;
    }

    public ResourceLocation id() {
        return id;
    }

    public ResourceLocation typeId() {
        return typeId;
    }

    public String name() {
        return internalData.name();
    }

    public void setName(String name) {
        var oldName = internalData.name();
        internalData.setName(name);
        if (!Objects.equals(oldName, name)) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.NAME);
        }
    }

    public int color() {
        return internalData.color();
    }

    public void setColor(int color) {
        var oldColor = internalData.color();
        internalData.setColor(color);
        if (oldColor != color) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.COLOR);
        }
    }

    public ClaimVisibility claimVisibility() {
        return internalData.claimVisibility();
    }

    public void setClaimVisibility(ClaimVisibility claimVisibility) {
        var oldClaimVisibility = internalData.claimVisibility();
        internalData.setClaimVisibility(claimVisibility);
        if (oldClaimVisibility != claimVisibility) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.CLAIM_VISIBILITY);
        }
    }

    public ProtectionMode blockBreakProtection() {
        return internalData.blockBreakProtection();
    }

    public void setBlockBreakProtection(ProtectionMode blockBreakProtection) {
        var oldBlockBreakProtection = internalData.blockBreakProtection();
        internalData.setBlockBreakProtection(blockBreakProtection);
        if (oldBlockBreakProtection != blockBreakProtection) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.BLOCK_BREAK_PROTECTION);
        }
    }

    public ProtectionMode blockInteractProtection() {
        return internalData.blockInteractProtection();
    }

    public void setBlockInteractProtection(ProtectionMode blockInteractProtection) {
        var oldBlockInteractProtection = internalData.blockInteractProtection();
        internalData.setBlockInteractProtection(blockInteractProtection);
        if (oldBlockInteractProtection != blockInteractProtection) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.BLOCK_INTERACT_PROTECTION);
        }
    }

    public ProtectionMode entityInteractProtection() {
        return internalData.entityInteractProtection();
    }

    public void setEntityInteractProtection(ProtectionMode entityInteractProtection) {
        var oldEntityInteractProtection = internalData.entityInteractProtection();
        internalData.setEntityInteractProtection(entityInteractProtection);
        if (oldEntityInteractProtection != entityInteractProtection) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.ENTITY_INTERACT_PROTECTION);
        }
    }

    public ProtectionMode nonLivingEntityAttackProtection() {
        return internalData.nonLivingEntityAttackProtection();
    }

    public void setNonLivingEntityAttackProtection(ProtectionMode nonLivingEntityAttackProtection) {
        var oldNonLivingEntityAttackProtection = internalData.nonLivingEntityAttackProtection();
        internalData.setNonLivingEntityAttackProtection(nonLivingEntityAttackProtection);
        if (oldNonLivingEntityAttackProtection != nonLivingEntityAttackProtection) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.NONLIVING_ENTITY_ATTACK_PROTECTION);
        }
    }

    public boolean allowPvp() {
        return internalData.allowPvp();
    }

    public void setAllowPvp(boolean allowPvp) {
        var oldAllowPvp = internalData.allowPvp();
        internalData.setAllowPvp(allowPvp);
        if (oldAllowPvp != allowPvp) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.ALLOW_PVP);
        }
    }

    public boolean allowExplosions() {
        return internalData.allowExplosions();
    }

    public void setAllowExplosions(boolean allowExplosions) {
        var oldAllowExplosions = internalData.allowExplosions();
        internalData.setAllowExplosions(allowExplosions);
        if (oldAllowExplosions != allowExplosions) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.ALLOW_EXPLOSIONS);
        }
    }

    public boolean allowMobGriefing() {
        return internalData.allowMobGriefing();
    }

    public void setAllowMobGriefing(boolean allowMobGriefing) {
        var oldAllowMobGriefing = internalData.allowMobGriefing();
        internalData.setAllowMobGriefing(allowMobGriefing);
        if (oldAllowMobGriefing != allowMobGriefing) {
            fireDataChanged(BLibFactionDataChangedEvent.Kind.ALLOW_MOB_GRIEFING);
        }
    }

    public FactionMembership membership() {
        return membership;
    }

    @SuppressWarnings("unchecked")
    public @Nullable T data() {
        return (T) internalData.modData();
    }

    public boolean isType(TagKey<FactionDataType<?>> tag) {
        var holder = BLibBuiltInRegistries.FACTION_DATA_TYPES.getHolder(typeId);

        return holder.isPresent() && holder.get().is(tag);
    }

    @ApiStatus.Internal
    public BLibFactionData internalData() {
        return internalData;
    }

    private void fireDataChanged(BLibFactionDataChangedEvent.Kind kind) {
        for (var listener : BLibGlobalEvents.FACTION_DATA_CHANGED.listeners()) {
            listener.invoke(id, kind);
        }
    }
}
