package com.alien.common.gameplay.hive.membership;

import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.Runner;
import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.HiveSpaceManager;
import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.entity.EntityReserves;
import com.lib.common.gameplay.util.spatial.block.BlockPosVec3;
import com.lib.common.util.codec.schema.CodecSchemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.function.Predicate;

import com.avp.AVP;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class HiveReserveManager implements NBTSerializable {

    private static final String NBT_HIVE_MEMBER_RESERVES = "hiveMemberReserves";

    private static final int FIVE_MINUTES_IN_TICKS = 5 * 60 * 20;

    private final EntityReserves hiveMemberReserves;

    private final Hive hive;

    public HiveReserveManager(Hive hive) {
        this.hiveMemberReserves = new EntityReserves();
        this.hive = hive;
    }

    public void tick() {
        if (hive.ageInTicks() % FIVE_MINUTES_IN_TICKS == 0) {
            var warriorLayer = HiveSpaceManager.HiveLayer.WARRIOR.getSphereLayer();
            // Note that this is XENOMORPHS, not aliens. This is deliberate.
            var numberOfXenomorphsInOuterEdges = (int) hive.getMembershipManager()
                .getLoadedMembers()
                .stream()
                .filter(
                    entity -> entity.getType().is(AVPEntityTypeTags.XENOMORPHS) && !hive.getSpaceManager()
                        .isWithinLayerOrBelow(warriorLayer, new BlockPosVec3(entity.blockPosition()))
                )
                .count();

            if (numberOfXenomorphsInOuterEdges > 0) {
                var half = numberOfXenomorphsInOuterEdges / 2;
                var remainder = numberOfXenomorphsInOuterEdges % 2;

                var isDroneFirst = hive.getRandom().nextBoolean();
                var droneType = Drone.getType(hive.getVariant());
                var runnerType = Runner.getType(hive.getVariant());

                // Always add half to each.
                hiveMemberReserves.add(droneType, half);
                hiveMemberReserves.add(runnerType, half);

                // Randomly assign a remainder (only happens if count is odd, i.e., 1).
                if (remainder > 0) {
                    var extraType = isDroneFirst
                        ? droneType
                        : runnerType;

                    hiveMemberReserves.add(extraType, 1);
                }
            }
        }
    }

    public boolean canSpawn(EntityType<?> entityType) {
        return hiveMemberReserves.getCount(entityType) > 0;
    }

    public void add(EntityType<?> entityType, int count) {
        hiveMemberReserves.add(entityType, count);
    }

    public int getCount(EntityType<?> entityType) {
        return hiveMemberReserves.getCount(entityType);
    }

    public int getCountMatching(Predicate<EntityType<?>> predicate) {
        return hiveMemberReserves.getCountMatching(predicate);
    }

    public List<EntityType<?>> getAvailableEntityTypes() {
        return hiveMemberReserves.getAvailableEntityTypes();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        EntityReserves.CODEC.decode(CodecSchemas.NBT, compoundTag.getCompound(NBT_HIVE_MEMBER_RESERVES))
            .inspectErr(tag -> AVP.LOGGER.error("Failed to load tag '{}'. Tag: {}", NBT_HIVE_MEMBER_RESERVES, tag))
            .ifOk(loadedEntityReserves -> hiveMemberReserves.putAll(loadedEntityReserves.getBackingMap()));
    }

    @Override
    public void save(CompoundTag compoundTag) {
        compoundTag.put(NBT_HIVE_MEMBER_RESERVES, EntityReserves.CODEC.encode(CodecSchemas.NBT, hiveMemberReserves));
    }
}
