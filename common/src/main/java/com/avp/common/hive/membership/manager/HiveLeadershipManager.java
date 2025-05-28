package com.avp.common.hive.membership.manager;

import com.bvanseg.just.functional.option.Option;
import com.xlib.NBTSerializable;
import com.xlib.util.CompoundTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.avp.common.entity.living.alien.Alien;
import com.avp.common.hive.Hive;

public class HiveLeadershipManager implements NBTSerializable {

    private static final String LEADER_ID_KEY = "HiveLeaderId";

    private final Hive hive;

    private Option<UUID> leaderIdOption;

    public HiveLeadershipManager(Hive hive) {
        this.hive = hive;
        this.leaderIdOption = Option.none();
    }

    public void tick() {
        if (!hive.getMembershipManager().isMember(getLeaderIdOrNull())) {
            // If the hive leader id is no longer present in the hive member data map, clear the leader.
            setLeaderId(null);
        }
    }

    public void removeLeadership(@NotNull Entity entity) {
        removeLeadership(entity.getUUID());
    }

    public void removeLeadership(@NotNull UUID uuid) {
        if (leaderIdOption.contains(uuid)) {
            // If the entity being removed is the leader, then set the leader ID to none.
            this.leaderIdOption = Option.none();
        }
    }

    public boolean isLeader(Entity entity) {
        return leaderIdOption.contains(entity.getUUID());
    }

    public @Nullable UUID getLeaderIdOrNull() {
        return leaderIdOption.unwrapOr(null);
    }

    public Option<Alien> getLeader() {
        return Option.ofNullable(getLeaderOrNull());
    }

    public @Nullable Alien getLeaderOrNull() {
        if (!(hive.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        return leaderIdOption.map(serverLevel::getEntity)
            .filter(entity -> entity instanceof Alien)
            .map(entity -> (Alien) entity)
            .unwrapOr(null);
    }

    public void setLeaderId(@Nullable UUID id) {
        this.leaderIdOption = Option.ofNullable(id);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        this.leaderIdOption = Option.ofNullable(CompoundTagUtil.getUUIDOrNull(compoundTag, LEADER_ID_KEY));
    }

    @Override
    public void save(CompoundTag compoundTag) {
        leaderIdOption.ifSome(leaderId -> compoundTag.putUUID(LEADER_ID_KEY, leaderId));
    }
}
