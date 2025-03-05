package com.avp.common.block.entity.resin_node;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MultifaceBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

import com.avp.common.block.entity.resin_node.behavior.SpreadBehavior;

public class ChargeCursor {

    private static final Codec<Set<Direction>> DIRECTION_SET = Direction.CODEC.listOf()
        .xmap(list -> Sets.newEnumSet(list, Direction.class), Lists::newArrayList);

    public static final Codec<ChargeCursor> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(ChargeCursor::getPos),
            Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(ChargeCursor::getCharge),
            Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(ChargeCursor::getDecayDelay),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay").orElse(0).forGetter(chargeCursor -> chargeCursor.updateDelay),
            DIRECTION_SET.lenientOptionalFieldOf("facings").forGetter(chargeCursor -> Optional.ofNullable(chargeCursor.getFacingData()))
        )
            .apply(instance, ChargeCursor::new)
    );

    private BlockPos pos;

    int charge;

    private int updateDelay;

    private int decayDelay;

    @Nullable
    private Set<Direction> facings;

    private ChargeCursor(BlockPos blockPos, int charge, int decayDelay, int updateDelay, Optional<Set<Direction>> facingsOptional) {
        this.pos = blockPos;
        this.charge = charge;
        this.decayDelay = decayDelay;
        this.updateDelay = updateDelay;
        this.facings = facingsOptional.orElse(null);
    }

    public ChargeCursor(BlockPos blockPos, int i) {
        this(blockPos, i, 1, 0, Optional.empty());
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

    public void update(
        LevelAccessor levelAccessor,
        BlockPos nodePos,
        RandomSource randomSource,
        ResinSpreader resinSpreader,
        boolean bl
    ) {
        if (!shouldUpdate(levelAccessor, nodePos, resinSpreader.isWorldGeneration)) {
            return;
        }

        if (updateDelay > 0) {
            updateDelay--;
            return;
        }

        var blockState = levelAccessor.getBlockState(pos);
        var resinBehavior = ChargeCursorUtil.getSpreadBehavior(blockState);

        if (
            bl && resinBehavior.attemptSpreadVein(
                nodePos,
                levelAccessor,
                pos,
                blockState,
                facings,
                resinSpreader.isWorldGeneration()
            )
        ) {
            if (resinBehavior.canChangeBlockStateOnSpread()) {
                blockState = levelAccessor.getBlockState(pos);
                resinBehavior = ChargeCursorUtil.getSpreadBehavior(blockState);
            }

            levelAccessor.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        this.charge = resinBehavior.attemptUseCharge(this, levelAccessor, nodePos, randomSource, resinSpreader, bl);

        if (charge <= 0) {
            resinBehavior.onDischarged(levelAccessor, blockState, pos, randomSource);
        } else {
            var validMovementPos = ChargeCursorUtil.getValidMovementPos(levelAccessor, pos, randomSource);

            if (validMovementPos != null) {
                resinBehavior.onDischarged(levelAccessor, blockState, pos, randomSource);
                this.pos = validMovementPos.immutable();

                if (
                    resinSpreader.isWorldGeneration() && !pos.closerThan(
                        new Vec3i(nodePos.getX(), pos.getY(), nodePos.getZ()),
                        15.0
                    )
                ) {
                    this.charge = 0;
                    return;
                }

                blockState = levelAccessor.getBlockState(validMovementPos);
            }

            if (blockState.getBlock() instanceof SpreadBehavior) {
                this.facings = MultifaceBlock.availableFaces(blockState);
            }

            this.decayDelay = resinBehavior.updateDecayDelay(decayDelay);
            this.updateDelay = resinBehavior.getResinSpreadDelay();
        }
    }

    private boolean shouldUpdate(LevelAccessor levelAccessor, BlockPos blockPos, boolean isWorldGeneration) {
        if (charge <= 0) {
            return false;
        } else if (isWorldGeneration) {
            return true;
        } else {
            return levelAccessor instanceof ServerLevel serverLevel && serverLevel.shouldTickBlocksAt(blockPos);
        }
    }

    void mergeWith(ChargeCursor chargeCursor) {
        this.charge = charge + chargeCursor.charge;
        chargeCursor.charge = 0;
        this.updateDelay = Math.min(updateDelay, chargeCursor.updateDelay);
    }
}
