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
import com.avp.common.entity.living.alien.AlienVariantTypes;

public class ResonatorBlockEntity extends BlockEntity {

    private int tickCounter = 0;

    private static int animationTickCounter = 0;

    private static boolean isAnimating = false;

    protected final ResonatorAnimDispatcher animDispatcher;

    private final Map<Item, Integer> resinBallCounts = new HashMap<>();

    public ResonatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(AVPBlockEntityTypes.RESONATOR.get(), pos, blockState);
        this.animDispatcher = new ResonatorAnimDispatcher();
    }

    @SuppressWarnings("unused")
    public static void serverTick(
        Level level,
        BlockPos blockPos,
        BlockState blockState,
        ResonatorBlockEntity resonatorBlockEntity
    ) {
        if (level.isClientSide()) {
            return;
        }

        if (!(level.hasNeighborSignal(blockPos) || level.hasNeighborSignal(blockPos.above()))) {
            resonatorBlockEntity.animDispatcher.unpowered(resonatorBlockEntity);
            isAnimating = false;
            animationTickCounter = 0;
            return;
        }

        if (!isAnimating) {
            if (animationTickCounter == 0) {
                resonatorBlockEntity.animDispatcher.powerUp(resonatorBlockEntity);
            }
            if (animationTickCounter >= 15) {
                resonatorBlockEntity.animDispatcher.powered(resonatorBlockEntity);
                isAnimating = true;
            } else {
                animationTickCounter++;
            }
        }

        resonatorBlockEntity.incrementTickCounter();

        var tickValue = AVP.config.blockConfigs.RESONATOR_REPLACE_TICKS;

        if (resonatorBlockEntity.getTickCounter() % tickValue != 0) {
            return;
        }

        var radius = AVP.config.blockConfigs.RESONATOR_REPLACE_RADIUS;

        var resinBallsGained = new AtomicInteger(0);

        BlockPos.betweenClosedStream(blockPos.offset(-radius, -radius, -radius), blockPos.offset(radius, radius, radius))
            .forEach(currentPos -> {
                var currentState = level.getBlockState(currentPos);

                AlienVariantTypes.getFor(currentState)
                    .ifSome(alienVariantType -> {
                        // TODO: Use variant-specific tag here.
                        if (currentState.is(AVPBlockTags.RESIN_VEINS)) {
                            level.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());

                            var resinBallItem = alienVariantType.resinBall().get();
                            resonatorBlockEntity.addResinBallItem(resinBallItem);

                            resinBallsGained.incrementAndGet();

                            if (resinBallsGained.get() > 0) {
                                resonatorBlockEntity.setChanged();
                            }

                            return;
                        }

                        // TODO: Use variant-specific tag here.
                        if (currentState.is(AVPBlockTags.RESIN)) {
                            // TODO: This is not a safe assumption to make!
                            var isDeepstone = currentPos.getY() <= 0;
                            var replacementBlock = isDeepstone ? Blocks.DEEPSLATE : Blocks.STONE;

                            level.setBlockAndUpdate(currentPos, replacementBlock.defaultBlockState());

                            var resinBallItem = alienVariantType.resinBall().get();
                            resonatorBlockEntity.addResinBallItem(resinBallItem);

                            resinBallsGained.incrementAndGet();

                            if (resinBallsGained.get() > 0) {
                                resonatorBlockEntity.setChanged();
                            }
                        }
                    });
            });
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
