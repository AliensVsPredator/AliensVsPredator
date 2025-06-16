package com.alien.common.gameplay.entity.living.alien.xenomorph.queen;

import com.alien.common.gameplay.entity.living.alien.ovipositor.Ovipositor;
import com.alien.common.registry.init.AlienEntityTypes;
import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class OvipositorManager {

    private final Queen queen;

    public OvipositorManager(Queen queen) {
        this.queen = queen;
    }

    public void tick() {
        if (queen.level().isClientSide) {
            return;
        }

        if (hasOvipositor()) {
            getOvipositor().ifSome(ovipositor -> {
                ovipositor.setYRot(queen.getYRot());
                ovipositor.setXRot(queen.getXRot());
                // Body rotation.
                ovipositor.yBodyRot = queen.yBodyRot;
                // Head rotation.
                ovipositor.yHeadRot = queen.yHeadRot;
            });

            queen.getHiveManager().hive().ifSome(hive -> {
                if (hive.getLeadershipManager().isLeader(queen)) {
                    hive.moveCenter(queen.blockPosition());
                }
            });
            return;
        }

        if (!canCreateOvipositor()) {
            return;
        }

        createOvipositor();
    }

    public Vec3 getEggLayingPosition() {
        return getRelativePosition(5.75, 0, -1);
    }

    public @Nullable Ovipositor getOvipositorOrNull() {
        return (Ovipositor) queen.getPassengers()
            .stream()
            .filter(passenger -> passenger.getType() == AlienEntityTypes.OVIPOSITOR.get())
            .findFirst()
            .orElse(null);
    }

    public Option<Ovipositor> getOvipositor() {
        return Option.ofNullable(getOvipositorOrNull());
    }

    public boolean hasOvipositor() {
        return getOvipositorOrNull() != null;
    }

    private void createOvipositor() {
        var ovipositor = AlienEntityTypes.OVIPOSITOR.get().create(queen.level());

        if (ovipositor != null) {
            ovipositor.moveTo(queen.position(), queen.getYRot(), queen.getXRot());
            ovipositor.startRiding(queen, true);

            // Body rotation.
            ovipositor.yBodyRot = queen.yBodyRot;
            // Head rotation.
            ovipositor.yHeadRot = queen.yHeadRot;

            queen.level().addFreshEntity(ovipositor);
        }
    }

    private boolean canCreateOvipositor() {
        return queen.getHiveManager()
            .hive()
            .isSomeAnd(
                hive -> hive.getMembershipManager()
                    .getMembersMatching(entityType -> entityType.is(AVPEntityTypeTags.XENOMORPHS))
                    .size() > 2
            )
            && canOvipositorFit();
    }

    private boolean canOvipositorFit() {
        var leftBottomSupport = getRelativePosition(1.5, 0, 2.5);
        var rightBottomSupport = getRelativePosition(-2, 0, 2);
        var farLeftBottomSupport = getRelativePosition(5, 0, 7);
        var backBottomSupport = getRelativePosition(0, 0, 7);

        return canOvipositorSupportExistAt(leftBottomSupport)
            && canOvipositorSupportExistAt(rightBottomSupport)
            && canOvipositorSupportExistAt(farLeftBottomSupport)
            && canOvipositorSupportExistAt(backBottomSupport)
            && queen.level().getBlockState(BlockPos.containing(getEggLayingPosition())).isAir();
    }

    private Vec3 getRelativePosition(double leftOffset, double upOffset, double backwardOffset) {
        var forward = queen.getLookAngle().normalize();
        var left = new Vec3(forward.z, 0, -forward.x).normalize(); // perpendicular on XZ plane

        var bounds = queen.getBoundingBox();
        var base = new Vec3(
            (bounds.minX + bounds.maxX) / 2.0,
            bounds.minY,
            (bounds.minZ + bounds.maxZ) / 2.0
        );

        // Negative backwardOffset = forward, positive = behind
        return base
            .add(forward.scale(-backwardOffset))
            .add(left.scale(leftOffset))
            .add(0, upOffset, 0);
    }

    private boolean canOvipositorSupportExistAt(Vec3 vec3) {
        var blockPos = BlockPos.containing(vec3);

        var isSupported = false;
        var stepsDown = 0;

        while (!isSupported && stepsDown < 4) {
            blockPos = blockPos.below();
            var blockState = queen.level().getBlockState(blockPos);

            var aboveBlockState = queen.level().getBlockState(blockPos.above());
            isSupported = (aboveBlockState.isAir() || aboveBlockState.canBeReplaced())
                && !(blockState.isAir() || blockState.canBeReplaced());

            stepsDown++;
        }

        return isSupported;
    }
}
