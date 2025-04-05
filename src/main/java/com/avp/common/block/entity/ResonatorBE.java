package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.avp.AVP;
import com.avp.common.block.AVPBlockTags;
import com.avp.common.util.AlienVariantUtil;

public class ResonatorBE extends BlockEntity {

    private int tickCounter = 0;

    private static int animationTickCounter = 0;

    private static boolean isAnimating = false;

    protected final ResonatorAnimDispatcher animDispatcher;

    private final Map<Item, Integer> resinBallCounts = new HashMap<>();

    public ResonatorBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityTypes.RESONATOR_BE, pos, blockState);
        this.animDispatcher = new ResonatorAnimDispatcher();
    }

    @SuppressWarnings("unused")
    public static void serverTick(
        Level level,
        BlockPos blockPos,
        BlockState blockState,
        ResonatorBE resonatorBE
    ) {
        if (level.isClientSide()) {
            return;
        }

        if (!(level.hasNeighborSignal(blockPos) || level.hasNeighborSignal(blockPos.above()))) {
            resonatorBE.animDispatcher.unpowered(resonatorBE);
            isAnimating = false;
            animationTickCounter = 0;
            return;
        }

        if (!isAnimating) {
            if (animationTickCounter == 0) {
                resonatorBE.animDispatcher.powerUp(resonatorBE);
            }
            if (animationTickCounter >= 15) {
                resonatorBE.animDispatcher.powered(resonatorBE);
                isAnimating = true;
            } else {
                animationTickCounter++;
            }
        }

        resonatorBE.incrementTickCounter();

        var tickValue = AVP.config.blockConfigs.RESONATOR_REPLACE_TICKS;

        if (resonatorBE.getTickCounter() % tickValue != 0) {
            return;
        }

        var radius = AVP.config.blockConfigs.RESONATOR_REPLACE_RADIUS;

        var resinBallsGained = new AtomicInteger(0);

        BlockPos.betweenClosedStream(blockPos.offset(-radius, -radius, -radius), blockPos.offset(radius, radius, radius))
            .filter(currentPos -> {
                var currentState = level.getBlockState(currentPos);

                if (currentState.is(AVPBlockTags.RESIN_VEINS)) {
                    level.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());

                    var resinBallItem = AlienVariantUtil.getResinBallForType(currentState);
                    resonatorBE.addResinBallItem(resinBallItem);

                    resinBallsGained.incrementAndGet();

                    if (resinBallsGained.get() > 0) {
                        resonatorBE.setChanged();
                    }
                    return true;
                }

                if (currentState.is(AVPBlockTags.RESIN)) {
                    var isDeepstone = currentPos.getY() <= 0;
                    var replacementBlock = isDeepstone ? Blocks.DEEPSLATE : Blocks.STONE;

                    level.setBlockAndUpdate(currentPos, replacementBlock.defaultBlockState());

                    var resinBallItem = AlienVariantUtil.getResinBallForType(currentState);
                    resonatorBE.addResinBallItem(resinBallItem);

                    resinBallsGained.incrementAndGet();

                    if (resinBallsGained.get() > 0) {
                        resonatorBE.setChanged();
                    }
                    return true;
                }

                return false;
            })
            .findFirst();
    }

    public void addResinBallItem(Item resinBallItem) {
        resinBallCounts.merge(resinBallItem, 1, Integer::sum);
        setChanged();
    }

    public Map<Item, Integer> getResinBallCounts() {
        return Collections.unmodifiableMap(resinBallCounts);
    }

    public void onRightClick(Player player) {
        if (level == null || level.isClientSide) {
            return;
        }

        if (!resinBallCounts.isEmpty()) {
            for (var entry : resinBallCounts.entrySet()) {
                var resinBallItem = entry.getKey();
                var count = entry.getValue();

                if (count > 0) {
                    var resinBallStack = new ItemStack(resinBallItem, count);
                    var resinBallEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), resinBallStack);
                    level.addFreshEntity(resinBallEntity);
                }
            }
            resinBallCounts.clear();
            setChanged();
        }
    }

    public void incrementTickCounter() {
        tickCounter++;
    }

    public int getTickCounter() {
        return tickCounter;
    }

}
