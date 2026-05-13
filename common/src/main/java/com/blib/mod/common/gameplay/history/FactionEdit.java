package com.blib.mod.common.gameplay.history;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import com.blib.api.common.faction.v1.ClaimVisibility;
import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.faction.v1.ProtectionMode;
import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.internal.common.faction.BLibFactionManager;
import com.blib.mod.common.network.packet.C2SUpdateFactionFieldPayload;

/**
 * Faction edit captured for undo/redo. {@link Kind} discriminates which gesture this was; field nullability follows the
 * kind:
 * <ul>
 * <li>{@code FIELD}: {@code fieldOrdinal}, {@code beforeValue}, {@code afterValue} populated.</li>
 * <li>{@code RELATIONSHIP}: {@code otherFactionId}, {@code beforeRelOrdinal}, {@code afterRelOrdinal} populated.</li>
 * <li>{@code ADD_MEMBER} / {@code REMOVE_MEMBER}: {@code memberUuid} populated.</li>
 * </ul>
 * Create / delete are NOT covered in v1 — resurrecting a deleted faction would require capturing the full member list,
 * relationships, custom data fields, and shard state, which is out of scope. Best-effort solution: do the create/delete
 * gestures with no undo entry pushed.
 */
@ApiStatus.Internal
public record FactionEdit(
    ResourceLocation factionId,
    Kind kind,
    int fieldOrdinal,
    @Nullable String beforeValue,
    @Nullable String afterValue,
    @Nullable ResourceLocation otherFactionId,
    int beforeRelOrdinal,
    int afterRelOrdinal,
    @Nullable UUID memberUuid,
    String description,
    long timestamp
) implements EditorAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactionEdit.class);

    public enum Kind {
        FIELD,
        RELATIONSHIP,
        ADD_MEMBER,
        REMOVE_MEMBER
    }

    public static final String TYPE_ID = "faction_edit";

    @Override
    public String typeId() {
        return TYPE_ID;
    }

    @Override
    public long estimatedBytes() {
        return 256L;
    }

    @Override
    public void revert(MinecraftServer server) {
        switch (kind) {
            case FIELD -> applyFieldValue(server, beforeValue);
            case RELATIONSHIP -> applyRelationship(server, beforeRelOrdinal);
            case ADD_MEMBER -> applyMemberOp(server, false);
            case REMOVE_MEMBER -> applyMemberOp(server, true);
        }
    }

    @Override
    public void redo(MinecraftServer server) {
        switch (kind) {
            case FIELD -> applyFieldValue(server, afterValue);
            case RELATIONSHIP -> applyRelationship(server, afterRelOrdinal);
            case ADD_MEMBER -> applyMemberOp(server, true);
            case REMOVE_MEMBER -> applyMemberOp(server, false);
        }
    }

    private void applyFieldValue(MinecraftServer server, @Nullable String value) {
        if (value == null) {
            return;
        }
        var faction = BLibFactionManager.INSTANCE.get(factionId);
        if (faction == null) {
            return;
        }
        var fields = C2SUpdateFactionFieldPayload.Field.values();
        if (fieldOrdinal < 0 || fieldOrdinal >= fields.length) {
            return;
        }
        switch (fields[fieldOrdinal]) {
            case NAME -> faction.setName(value);
            case COLOR -> {
                try {
                    faction.setColor(Integer.decode(value));
                } catch (NumberFormatException ignored) {
                    LOGGER.warn("[BLib] FactionEdit.applyFieldValue: bad color value {}", value);
                }
            }
            case CLAIM_VISIBILITY -> parseEnum(ClaimVisibility.class, value)
                .ifPresent(faction::setClaimVisibility);
            case BLOCK_BREAK_PROTECTION -> parseEnum(ProtectionMode.class, value)
                .ifPresent(faction::setBlockBreakProtection);
            case BLOCK_INTERACT_PROTECTION -> parseEnum(ProtectionMode.class, value)
                .ifPresent(faction::setBlockInteractProtection);
            case ENTITY_INTERACT_PROTECTION -> parseEnum(ProtectionMode.class, value)
                .ifPresent(faction::setEntityInteractProtection);
            case NONLIVING_ENTITY_ATTACK_PROTECTION -> parseEnum(ProtectionMode.class, value)
                .ifPresent(faction::setNonLivingEntityAttackProtection);
            case ALLOW_PVP -> faction.setAllowPvp(Boolean.parseBoolean(value));
            case ALLOW_EXPLOSIONS -> faction.setAllowExplosions(Boolean.parseBoolean(value));
            case ALLOW_MOB_GRIEFING -> faction.setAllowMobGriefing(Boolean.parseBoolean(value));
        }
        BLibFactionManager.INSTANCE.pushInspectionToAllClients(server, factionId);
        BLibFactionManager.INSTANCE.pushDirectoryToAllClients(server);
    }

    private void applyRelationship(MinecraftServer server, int ordinal) {
        if (otherFactionId == null) {
            return;
        }
        var states = RelationshipState.values();
        if (ordinal < 0 || ordinal >= states.length) {
            return;
        }
        BLibFactionManager.INSTANCE.setRelationship(factionId, otherFactionId, states[ordinal]);
        BLibFactionManager.INSTANCE.pushDirectoryToAllClients(server);
    }

    private void applyMemberOp(MinecraftServer server, boolean add) {
        if (memberUuid == null) {
            return;
        }
        var faction = BLibFactionManager.INSTANCE.get(factionId);
        if (faction == null) {
            return;
        }
        var member = FactionMember.entity(memberUuid);
        var changed = add ? faction.membership().addMember(member) : faction.membership().removeMember(member);
        if (changed) {
            BLibFactionManager.INSTANCE.pushDirectoryToAllClients(server);
            BLibFactionManager.INSTANCE.pushMembersToAllClients(server, factionId);
        }
    }

    private static <E extends Enum<E>> Optional<E> parseEnum(Class<E> enumClass, String value) {
        try {
            return Optional.of(Enum.valueOf(enumClass, value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
