package com.alien.common.gameplay.entity.living.alien.xenomorph.queen;

import com.alien.common.gameplay.entity.living.alien.ovipositor.Ovipositor;
import com.alien.common.registry.init.AlienEntityTypes;
import com.bvanseg.just.functional.option.Option;
import com.lib.common.data.Cooldown;
import com.lib.common.gameplay.NBTSerializable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.common.util.EntityUtil;

public class OvipositorManager implements NBTSerializable {

    private final Cooldown ovipositorCreationCooldown;

    private final Queen queen;

    private boolean hadOvipositorLastTick;

    public OvipositorManager(Queen queen) {
        this.ovipositorCreationCooldown = Cooldown.withCooldownTime("ovipositorCreationCooldownInTicks", Duration.ofMinutes(1));
        this.queen = queen;
    }

    public void tick() {
        if (queen.level().isClientSide) {
            return;
        }

        ovipositorCreationCooldown.tick();

        var hasOvipositor = hasOvipositor();

        if (!hasOvipositor && hadOvipositorLastTick) {
            ovipositorCreationCooldown.reset();
        }

        this.hadOvipositorLastTick = hasOvipositor;

        if (hasOvipositor) {
            getOvipositor().ifSome(ovipositor -> {
                ovipositor.setYRot(queen.getYRot());
                ovipositor.setXRot(queen.getXRot());
                // Body rotation.
                ovipositor.yBodyRot = queen.yBodyRot;
                // Head rotation.
                ovipositor.yHeadRot = queen.yHeadRot;
            });
            return;
        }

        if (!canCreateOvipositor()) {
            return;
        }

        createOvipositor();
        ovipositorCreationCooldown.reset();
    }

    public Vec3 getEggLayingPosition() {
        return EntityUtil.getRelativePosition(queen, 6, 0, 2.5);
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
        return queen.getTarget() == null
            && !queen.isIrradiated()
            && !queen.isPoisoned()
            && !ovipositorCreationCooldown.isActive()
            && queen.getHiveManager()
                .hive()
                .isSomeAnd(
                    hive -> hive.isAlive()
                        && !hive.isAngry()
                        && hive.getMembershipManager()
                            .getMembersMatching(entityType -> entityType.is(AVPEntityTypeTags.XENOMORPHS))
                            .size() > 2
                )
            && canOvipositorFit();
    }

    private boolean canOvipositorFit() {
        var leftBottomSupport = EntityUtil.getRelativePosition(queen, 1.5, 0, 2.5);
        var rightBottomSupport = EntityUtil.getRelativePosition(queen, -2, 0, 2);
        var farLeftBottomSupport = EntityUtil.getRelativePosition(queen, 5.7, 0, 8.25);
        var backBottomSupport = EntityUtil.getRelativePosition(queen, 0, 0, 7);

        return canOvipositorSupportExistAt(leftBottomSupport)
            && canOvipositorSupportExistAt(rightBottomSupport)
            && canOvipositorSupportExistAt(farLeftBottomSupport)
            && canOvipositorSupportExistAt(backBottomSupport)
            && isEggLayingPositionValid();
    }

    private boolean isEggLayingPositionValid() {
        var eggLayingPosition = getEggLayingPosition();
        var blockState = queen.level().getBlockState(BlockPos.containing(eggLayingPosition));
        var isClearForEgg = blockState.isAir() || blockState.canBeReplaced();

        return isClearForEgg && EntityUtil.canMobSeeBlock(queen, eggLayingPosition);
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

        return isSupported && EntityUtil.canMobSeeBlock(queen, vec3);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        ovipositorCreationCooldown.load(compoundTag);
    }

    @Override
    public void save(CompoundTag compoundTag) {
        ovipositorCreationCooldown.save(compoundTag);
    }
}
