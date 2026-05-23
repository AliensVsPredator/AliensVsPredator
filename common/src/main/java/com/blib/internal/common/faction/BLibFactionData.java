package com.blib.internal.common.faction;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

import com.blib.api.common.faction.v1.ClaimVisibility;
import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.faction.v1.ProtectionMode;
import com.blib.api.common.util.v1.Dirty;

@ApiStatus.Internal
public class BLibFactionData implements Dirty {

    private static final String KEY_NAME = "name";

    private static final String KEY_COLOR = "color";

    private static final String KEY_CLAIM_VISIBILITY = "claim_visibility";

    private static final String KEY_BLOCK_BREAK_PROTECTION = "block_break_protection";

    private static final String KEY_BLOCK_INTERACT_PROTECTION = "block_interact_protection";

    private static final String KEY_ENTITY_INTERACT_PROTECTION = "entity_interact_protection";

    private static final String KEY_ENTITY_ATTACK_PROTECTION = "nonliving_entity_attack_protection";

    private static final String KEY_ALLOW_EXPLOSIONS = "allow_explosions";

    private static final String KEY_ALLOW_MOB_GRIEFING = "allow_mob_griefing";

    private static final String KEY_ALLOW_PVP = "allow_pvp";

    private static final String KEY_MOD_DATA = "mod_data";

    private String name;

    private int color;

    private ClaimVisibility claimVisibility;

    private ProtectionMode blockBreakProtection;

    private ProtectionMode blockInteractProtection;

    private ProtectionMode entityInteractProtection;

    private ProtectionMode nonLivingEntityAttackProtection;

    private boolean allowExplosions;

    private boolean allowMobGriefing;

    private boolean allowPvp;

    private @Nullable FactionData modData;

    private boolean dirty;

    public BLibFactionData(String name, int color, @Nullable FactionData modData) {
        this.name = name;
        this.color = color;
        this.claimVisibility = ClaimVisibility.PUBLIC;
        this.blockBreakProtection = ProtectionMode.PRIVATE;
        this.blockInteractProtection = ProtectionMode.PRIVATE;
        this.entityInteractProtection = ProtectionMode.PRIVATE;
        this.nonLivingEntityAttackProtection = ProtectionMode.PRIVATE;
        this.allowExplosions = false;
        this.allowMobGriefing = false;
        this.allowPvp = false;
        this.modData = modData;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        markDirty();
    }

    public int color() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markDirty();
    }

    public ClaimVisibility claimVisibility() {
        return claimVisibility;
    }

    public void setClaimVisibility(ClaimVisibility claimVisibility) {
        this.claimVisibility = claimVisibility;
        markDirty();
    }

    public ProtectionMode blockBreakProtection() {
        return blockBreakProtection;
    }

    public void setBlockBreakProtection(ProtectionMode blockBreakProtection) {
        this.blockBreakProtection = blockBreakProtection;
        markDirty();
    }

    public ProtectionMode blockInteractProtection() {
        return blockInteractProtection;
    }

    public void setBlockInteractProtection(ProtectionMode blockInteractProtection) {
        this.blockInteractProtection = blockInteractProtection;
        markDirty();
    }

    public ProtectionMode entityInteractProtection() {
        return entityInteractProtection;
    }

    public void setEntityInteractProtection(ProtectionMode entityInteractProtection) {
        this.entityInteractProtection = entityInteractProtection;
        markDirty();
    }

    public ProtectionMode nonLivingEntityAttackProtection() {
        return nonLivingEntityAttackProtection;
    }

    public void setNonLivingEntityAttackProtection(ProtectionMode nonLivingEntityAttackProtection) {
        this.nonLivingEntityAttackProtection = nonLivingEntityAttackProtection;
        markDirty();
    }

    public boolean allowExplosions() {
        return allowExplosions;
    }

    public void setAllowExplosions(boolean allowExplosions) {
        this.allowExplosions = allowExplosions;
        markDirty();
    }

    public boolean allowMobGriefing() {
        return allowMobGriefing;
    }

    public void setAllowMobGriefing(boolean allowMobGriefing) {
        this.allowMobGriefing = allowMobGriefing;
        markDirty();
    }

    public boolean allowPvp() {
        return allowPvp;
    }

    public void setAllowPvp(boolean allowPvp) {
        this.allowPvp = allowPvp;
        markDirty();
    }

    public @Nullable FactionData modData() {
        return modData;
    }

    public void setModData(FactionData modData) {
        this.modData = modData;
    }

    public void save(CompoundTag tag) {
        tag.putString(KEY_NAME, name);
        tag.putInt(KEY_COLOR, color);
        tag.putString(KEY_CLAIM_VISIBILITY, claimVisibility.name());
        tag.putString(KEY_BLOCK_BREAK_PROTECTION, blockBreakProtection.name());
        tag.putString(KEY_BLOCK_INTERACT_PROTECTION, blockInteractProtection.name());
        tag.putString(KEY_ENTITY_INTERACT_PROTECTION, entityInteractProtection.name());
        tag.putString(KEY_ENTITY_ATTACK_PROTECTION, nonLivingEntityAttackProtection.name());
        tag.putBoolean(KEY_ALLOW_EXPLOSIONS, allowExplosions);
        tag.putBoolean(KEY_ALLOW_MOB_GRIEFING, allowMobGriefing);
        tag.putBoolean(KEY_ALLOW_PVP, allowPvp);

        if (modData != null) {
            var modDataTag = new CompoundTag();
            modData.save(modDataTag);
            tag.put(KEY_MOD_DATA, modDataTag);
        }
    }

    public void load(CompoundTag tag) {
        name = tag.getString(KEY_NAME);
        color = tag.getInt(KEY_COLOR);

        if (tag.contains(KEY_CLAIM_VISIBILITY)) {
            try {
                claimVisibility = ClaimVisibility.valueOf(tag.getString(KEY_CLAIM_VISIBILITY));
            } catch (IllegalArgumentException e) {
                claimVisibility = ClaimVisibility.PUBLIC;
            }
        }

        if (tag.contains(KEY_BLOCK_BREAK_PROTECTION)) {
            try {
                blockBreakProtection = ProtectionMode.valueOf(tag.getString(KEY_BLOCK_BREAK_PROTECTION));
            } catch (IllegalArgumentException e) {
                blockBreakProtection = ProtectionMode.PRIVATE;
            }
        }

        if (tag.contains(KEY_BLOCK_INTERACT_PROTECTION)) {
            try {
                blockInteractProtection = ProtectionMode.valueOf(tag.getString(KEY_BLOCK_INTERACT_PROTECTION));
            } catch (IllegalArgumentException e) {
                blockInteractProtection = ProtectionMode.PRIVATE;
            }
        }

        if (tag.contains(KEY_ENTITY_INTERACT_PROTECTION)) {
            try {
                entityInteractProtection = ProtectionMode.valueOf(tag.getString(KEY_ENTITY_INTERACT_PROTECTION));
            } catch (IllegalArgumentException e) {
                entityInteractProtection = ProtectionMode.PRIVATE;
            }
        }

        if (tag.contains(KEY_ENTITY_ATTACK_PROTECTION)) {
            try {
                nonLivingEntityAttackProtection = ProtectionMode.valueOf(tag.getString(KEY_ENTITY_ATTACK_PROTECTION));
            } catch (IllegalArgumentException e) {
                nonLivingEntityAttackProtection = ProtectionMode.PRIVATE;
            }
        }

        if (tag.contains(KEY_ALLOW_EXPLOSIONS)) {
            allowExplosions = tag.getBoolean(KEY_ALLOW_EXPLOSIONS);
        }

        if (tag.contains(KEY_ALLOW_MOB_GRIEFING)) {
            allowMobGriefing = tag.getBoolean(KEY_ALLOW_MOB_GRIEFING);
        }

        if (tag.contains(KEY_ALLOW_PVP)) {
            allowPvp = tag.getBoolean(KEY_ALLOW_PVP);
        }

        if (modData != null && tag.contains(KEY_MOD_DATA)) {
            modData.load(tag.getCompound(KEY_MOD_DATA));
        }
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty || (modData != null && modData.isDirty());
    }

    @Override
    public void clearDirty() {
        dirty = false;

        if (modData != null) {
            modData.clearDirty();
        }
    }

    public static int randomColor() {
        var hue = (float) Math.random();

        return Color.HSBtoRGB(hue, 0.7f, 0.9f) & 0xFFFFFF;
    }
}
