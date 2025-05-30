package com.alien.common.gameplay.block.entity.resin.node;

import com.alien.common.gameplay.block.entity.resin.node.behavior.VeinSpreadBehavior;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MultifaceBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

import com.avp.common.registry.init.AVPSoundEvents;

public class ChargeCursor {

    private static final Codec<Set<Direction>> DIRECTION_SET = Direction.CODEC.listOf()
        .xmap(list -> Sets.newEnumSet(list, Direction.class), Lists::newArrayList);

    public static final Codec<ChargeCursor> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(ChargeCursor::getPos),
            Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(ChargeCursor::getCharge),
            Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(ChargeCursor::getDecayDelay),
            Codec.intRange(0, Integer.MAX_VALUE)
                .fieldOf("update_delay")
                .orElse(0)
                .forGetter(chargeCursor -> chargeCursor.updateDelayInTicks),
            DIRECTION_SET.lenientOptionalFieldOf("facings").forGetter(chargeCursor -> Optional.ofNullable(chargeCursor.getFacingData()))
        )
            .apply(instance, ChargeCursor::new)
    );

    private BlockPos pos;

    int charge;

    private int updateDelayInTicks;

    private int decayDelay;

    @Nullable
    private Set<Direction> facings;

    private ChargeCursor(BlockPos blockPos, int charge, int decayDelay, int updateDelayInTicks, Optional<Set<Direction>> facingsOptional) {
        this.pos = blockPos;
        this.charge = charge;
        this.decayDelay = decayDelay;
        this.updateDelayInTicks = updateDelayInTicks;
        this.facings = facingsOptional.orElse(null);
    }

    public ChargeCursor(BlockPos blockPos, int i) {
        this(blockPos, i, 1, 0, Optional.empty());
    }

    public void update(
        LevelAccessor levelAccessor,
        BlockPos nodePos,
        RandomSource randomSource,
        ResinSpreader resinSpreader
    ) {
        if (!shouldUpdate(levelAccessor, nodePos)) {
            // If this cursor shouldn't update, then return.
            return;
        }

        if (updateDelayInTicks > 0) {
            // If the cursor still has an active cooldown, then return.
            updateDelayInTicks--;
            return;
        }

        var blockState = levelAccessor.getBlockState(pos);
        // Gets the spread behavior of the block's block state. If the block state does not define any spread behavior,
        // then the default resin spread behavior is used.
        var resinBehavior = ChargeCursorUtil.getSpreadBehavior(blockState);

        if (resinBehavior.attemptSpreadVein(nodePos, levelAccessor, pos, blockState, facings)) {
            if (resinBehavior.canChangeBlockStateOnSpread()) {
                blockState = levelAccessor.getBlockState(pos);
                resinBehavior = ChargeCursorUtil.getSpreadBehavior(blockState);
            }

            levelAccessor.playSound(null, pos, AVPSoundEvents.BLOCK_RESIN_SPREAD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        this.charge = resinBehavior.attemptUseCharge(this, levelAccessor, nodePos, randomSource, resinSpreader);

        if (charge > 0) {
            var validMovementPos = ChargeCursorUtil.getValidMovementPos(levelAccessor, pos, randomSource);

            if (validMovementPos != null) {
                this.pos = validMovementPos.immutable();
                blockState = levelAccessor.getBlockState(validMovementPos);
            }

            if (blockState.getBlock() instanceof VeinSpreadBehavior) {
                this.facings = MultifaceBlock.availableFaces(blockState);
            }

            this.decayDelay = resinBehavior.updateDecayDelay(decayDelay);
            this.updateDelayInTicks = resinBehavior.getResinSpreadDelayInTicks();
        }
    }

    private boolean shouldUpdate(LevelAccessor levelAccessor, BlockPos blockPos) {
        // If the cursor still has charge to use...
        return charge > 0
            // AND the cursor is executing server-side...
            && levelAccessor instanceof ServerLevel serverLevel
            // AND the chunk for the given block position can simulate ticks, then this cursor can update.
            && serverLevel.shouldTickBlocksAt(blockPos);
    }

    /* package-private */ void mergeWith(ChargeCursor chargeCursor) {
        // Absorb the given cursor's charge into this cursor.
        this.charge = charge + chargeCursor.charge;
        // Set the given cursor's charge to 0 since we just absorbed it.
        chargeCursor.charge = 0;
        // Set this cursor's update delay to whichever cursor's update delay is currently the shortest amount of time.
        this.updateDelayInTicks = Math.min(updateDelayInTicks, chargeCursor.updateDelayInTicks);
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getCharge() {
        return charge;
    }

    public int getDecayDelay() {
        return decayDelay;
    }

    @Nullable
    public Set<Direction> getFacingData() {
        return facings;
    }
}
