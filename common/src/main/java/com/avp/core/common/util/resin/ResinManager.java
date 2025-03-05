package com.avp.core.common.util.resin;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.avp.core.AVP;
import com.avp.core.common.block.AVPBlockTags;
import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.level.gameevent.AVPGameEvents;
import com.avp.core.common.level.gameevent.listener.ResinSpreadListener;

public class ResinManager implements GameEventListener.Provider<ResinSpreadListener> {

    private static final String RESIN_DATA_TAG_KEY = "resinData";

    private final Alien alien;

    private final ReadableResinData baseResinData;

    private final DynamicGameEventListener<ResinSpreadListener> dynamicResinSpreadListener;

    private final ResinSpreadListener resinSpreadListener;

    private @Nullable Supplier<Integer> bonusResinProvider;

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

        if (ticksSinceLastResinProduction >= resinData.tickRate()) {
            var factor = ticksSinceLastResinProduction / resinData.tickRate();
            var accumulatedResin = factor * resinData.resinPerTick();
            resinData.addResin(accumulatedResin);

            if (bonusResinProvider != null) {
                var bonusResin = bonusResinProvider.get();
                resinData.addResin(bonusResin);
            }

            ticksSinceLastResinProduction = 0;

            if (resinData.resin() >= resinData.resinMax()) {

                if (ticksSinceAttemptedNodePlacement > 0) {
                    return;
                }

                var belowPos = alien.blockPosition().below();
                var belowState = level.getBlockState(belowPos);

                if (alien.level().getBrightness(LightLayer.SKY, alien.blockPosition()) > 0) {
                    return;
                }

                var hive = alien.hiveManager().hiveOrNull();

                if (hive == null) {
                    return;
                }

                if (
                    alien.getTarget() != null || alien.tickCount <= alien.lastHurtTimeInTicks() + (10 * 20) || hive.isAngry() || !hive
                        .isEntityWithinHive(alien)
                ) {
                    return;
                }

                // TODO: Verify that this works
                alien.gameEvent(Holder.direct(AVPGameEvents.XENOMORPH_RESIN_SPREAD.get()));

                if (resinData.resin() >= resinData.resinMax()) {
                    if (!belowState.is(BlockTags.SCULK_REPLACEABLE) || belowState.is(AVPBlockTags.RESIN)) {
                        ticksSinceAttemptedNodePlacement = 20 * 10;
                        return;
                    }

                    // If the resin holder still has more resin, then we place a resin node manually.
                    var resinNodeBlock = alien.isNetherAfflicted() ? AVPBlocks.NETHER_RESIN_NODE.get() : AVPBlocks.RESIN_NODE.get();
                    alien.level().setBlockAndUpdate(belowPos, resinNodeBlock.defaultBlockState());
                }
            }
        }
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

    public ResinManager setBonusResinProvider(Supplier<Integer> bonusResinProvider) {
        this.bonusResinProvider = bonusResinProvider;
        return this;
    }

    public void load(CompoundTag compoundTag) {
        ResinData.CODEC.parse(
            new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound(RESIN_DATA_TAG_KEY))
        )
            .resultOrPartial(
                AVP.LOGGER::error
            )
            .ifPresent(resinData -> this.resinData = resinData);
    }

    public void save(CompoundTag compoundTag) {
        ResinData.CODEC.encodeStart(NbtOps.INSTANCE, resinData)
            .resultOrPartial(
                AVP.LOGGER::error
            )
            .ifPresent(tag -> compoundTag.put(RESIN_DATA_TAG_KEY, tag));
    }
}
