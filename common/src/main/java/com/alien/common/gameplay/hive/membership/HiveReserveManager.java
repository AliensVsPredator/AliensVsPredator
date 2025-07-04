package com.alien.common.gameplay.hive.membership;

import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.hive.Hive;
import com.alien.common.gameplay.hive.HiveSpaceManager;
import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.entity.EntityReserves;
import com.lib.common.gameplay.util.spatial.block.BlockPosVec3;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;

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
            var anyXenomorphsInOuterEdges = hive.getMembershipManager()
                .getLoadedMembers()
                .stream()
                .anyMatch(
                    entity -> entity.getType().is(AVPEntityTypeTags.XENOMORPHS) && !hive.getSpaceManager()
                        .isWithinLayerOrBelow(warriorLayer, new BlockPosVec3(entity.blockPosition()))
                );

            if (anyXenomorphsInOuterEdges) {
                var droneType = Drone.getType(hive.getVariant());
                hiveMemberReserves.add(droneType, 1);
            }
        }
    }

    public boolean canSpawn(EntityType<?> entityType) {
        return hiveMemberReserves.getCount(entityType) > 0;
    }

    public void increase(EntityType<?> entityType) {
        hiveMemberReserves.add(entityType, 1);
    }

    public void decrease(EntityType<?> entityType) {
        hiveMemberReserves.add(entityType, -1);
    }

    public int getCountMatching(Predicate<EntityType<?>> predicate) {
        return hiveMemberReserves.getCountMatching(predicate);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(NBT_HIVE_MEMBER_RESERVES)) {
            EntityReserves.CODEC.parse(
                new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound(NBT_HIVE_MEMBER_RESERVES))
            )
                .resultOrPartial(
                    AVP.LOGGER::error
                )
                .ifPresent(loadedEntityReserves -> hiveMemberReserves.putAll(loadedEntityReserves.getBackingMap()));
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        EntityReserves.CODEC.encodeStart(NbtOps.INSTANCE, hiveMemberReserves)
            .resultOrPartial(
                AVP.LOGGER::error
            )
            .ifPresent(tag -> compoundTag.put(NBT_HIVE_MEMBER_RESERVES, tag));
    }
}
