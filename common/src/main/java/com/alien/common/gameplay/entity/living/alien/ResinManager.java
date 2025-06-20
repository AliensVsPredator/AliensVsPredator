package com.alien.common.gameplay.entity.living.alien;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.level.gameevent.listener.ResinSpreadListener;
import com.alien.common.model.resin.ReadableResinData;
import com.alien.common.model.resin.ResinData;
import com.bvanseg.just.functional.option.Option;
import com.lib.common.gameplay.NBTSerializable;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

import com.avp.AVP;

public class ResinManager implements GameEventListener.Provider<ResinSpreadListener>, NBTSerializable {

    private static final String RESIN_DATA_TAG_KEY = "resinData";

    private final Alien alien;

    private final ReadableResinData baseResinData;

    private final DynamicGameEventListener<ResinSpreadListener> dynamicResinSpreadListener;

    private final ResinSpreadListener resinSpreadListener;

    private ResinData resinData;

    private int ticksSinceLastResinProduction = 0;

    private int ticksSinceAttemptedNodePlacement = 0;

    public ResinManager(Alien alien, ResinData resinData) {
        this.alien = alien;
        this.baseResinData = resinData;
        this.resinData = resinData;
        var positionSource = new EntityPositionSource(alien, 0F);
        var spreadType = new ResinSpreadListener.SpreaderType.Entity(alien);
        this.resinSpreadListener = new ResinSpreadListener(positionSource, spreadType);
        this.dynamicResinSpreadListener = new DynamicGameEventListener<>(resinSpreadListener);
    }

    @Override
    public @NotNull ResinSpreadListener getListener() {
        return resinSpreadListener;
    }

    public void tick() {
        var level = alien.level();

        if (level.isClientSide) {
            return;
        }

        ticksSinceLastResinProduction++;
        ticksSinceAttemptedNodePlacement = Math.max(0, ticksSinceAttemptedNodePlacement - 1);

        if (ticksSinceLastResinProduction < resinData.tickRate()) {
            return;
        }

        var factor = ticksSinceLastResinProduction / resinData.tickRate();
        var accumulatedResin = factor * resinData.resinPerTick();
        resinData.addResin(accumulatedResin);

        ticksSinceLastResinProduction = 0;

        if (
            // If we haven't reached full resin capacity...
            resinData.resin() < resinData.resinMax()
                // OR the resin node placement cooldown is still active...
                || ticksSinceAttemptedNodePlacement > 0
                // OR we can't spread resin at the alien's current position...
                || !canSpreadResinAtAlienPosition()
        ) {
            // Then return, we can't spread resin, yet.
            return;
        }

        var alienVariantType = AlienVariantTypes.getFor(alien);

        // Signal to the nearest resin node that we want to spread resin.
        alien.gameEvent(alienVariantType.resinSpreadEvent().getHolder());

        // If the alien still has resin even after signalling a resin spread event, that means there was no resin node
        // to intercept the event. So we try to place a resin node down here.
        if (resinData.resin() >= resinData.resinMax()) {
            // Try and find a suitable resin node block location.
            var suitableResinNodeBlockPosOption = findSuitableResinNodeBlockPos(level, alienVariantType.resinReplaceableTag());

            if (suitableResinNodeBlockPosOption.isNone()) {
                // Could not find a suitable resin node block position, so reset the node place cooldown and return.
                ticksSinceAttemptedNodePlacement = 20 * 10;
                return;
            }

            // If the resin holder still has more resin, then we place a resin node manually.
            var resinNodeBlockState = alienVariantType.resinNode().get().defaultBlockState();
            // Place the resin node block at the suitable position.
            alien.level().setBlockAndUpdate(suitableResinNodeBlockPosOption.unwrap(), resinNodeBlockState);
        }
    }

    private boolean canSpreadResinAtAlienPosition() {
        // Alien must not be exposed to skylight...
        return alien.level().getBrightness(LightLayer.SKY, alien.blockPosition()) == 0
            // AND alien must not have an attack target...
            && alien.getTarget() == null
            && !alien.isUnderWater()
            // AND alien must have not been hurt for more than 10 seconds...
            && alien.tickCount > alien.getLastHurtTimeInTicks() + (10 * 20)
            // AND alien hive conditions must be met...
            && alien.getHiveManager()
                .hive()
                // Where the alien's hive is not angry AND the alien is within range of the hive...
                .filter(hive -> !hive.isAngry() && hive.getSpaceManager().isEntityWithinHive(alien))
                // AND the alien must be in a hive for the hive conditions to be true.
                .isSome();
    }

    private Option<BlockPos> findSuitableResinNodeBlockPos(Level level, TagKey<Block> replaceableTagKey) {
        var origin = alien.blockPosition();
        var below = origin.below();
        var belowState = level.getBlockState(below);

        if (belowState.is(replaceableTagKey)) {
            return Option.some(below);
        }

        // Use mutable block pos for memory efficiency.
        var targetMutablePos = new BlockPos.MutableBlockPos();
        var belowTargetMutablePos = new BlockPos.MutableBlockPos();

        var radius = 2;

        for (var r = 0; r <= radius; r++) {
            for (var dx = -r; dx <= r; dx++) {
                var dz = r - Math.abs(dx);

                for (var sign : new int[] { 1, -1 }) {
                    var actualDz = dz * sign;

                    for (var dy = -1; dy <= 1; dy++) {
                        targetMutablePos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + actualDz);
                        belowTargetMutablePos.set(targetMutablePos.getX(), targetMutablePos.getY() - 1, targetMutablePos.getZ());

                        var targetStateToReplace = level.getBlockState(targetMutablePos);

                        if (
                            // If the target state is air OR can be replaced...
                            (targetStateToReplace.isAir()
                                || targetStateToReplace.canBeReplaced())
                                // AND if the supporting state beneath the target state is a solid render...
                                && level.getBlockState(belowTargetMutablePos).isSolidRender(level, belowTargetMutablePos)
                        ) {
                            // Then return the target state pos.
                            return Option.some(targetMutablePos.immutable());
                        }
                    }
                }
            }
        }

        return Option.none();
    }

    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        if (alien.level() instanceof ServerLevel serverLevel) {
            biConsumer.accept(dynamicResinSpreadListener, serverLevel);
        }
    }

    public ReadableResinData baseResinData() {
        return baseResinData;
    }

    public ResinData resinData() {
        return resinData;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        ResinData.CODEC.parse(
            new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound(RESIN_DATA_TAG_KEY))
        )
            .resultOrPartial(
                AVP.LOGGER::error
            )
            .ifPresent(resinData -> this.resinData = resinData);
    }

    @Override
    public void save(CompoundTag compoundTag) {
        ResinData.CODEC.encodeStart(NbtOps.INSTANCE, resinData)
            .resultOrPartial(
                AVP.LOGGER::error
            )
            .ifPresent(tag -> compoundTag.put(RESIN_DATA_TAG_KEY, tag));
    }
}
