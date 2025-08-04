package com.human.common.gameplay.block.entity;

import com.alien.common.data.AlienVariantTypes;
import com.human.common.gameplay.block.entity.power.PowerConsumerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.avp.AVP;
import com.avp.common.registry.init.AVPBlockEntityTypes;
import com.avp.common.registry.tag.AVPBlockTags;

public class ResonatorBlockEntity extends PowerConsumerBlockEntity {

    private int tickCounter;

    private final ResonatorAnimationDispatcher animationDispatcher;

    private final Map<Item, Integer> resinBallCounts;

    public ResonatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(AVPBlockEntityTypes.RESONATOR.get(), pos, blockState);
        this.tickCounter = 0;
        this.animationDispatcher = new ResonatorAnimationDispatcher();
        this.resinBallCounts = new HashMap<>();
    }

    @Override
    public long getRequestedPower() {
        return 1000;
    }

    @Override
    public void unpoweredTick(Level level, BlockPos blockPos, BlockState blockState) {
        animationDispatcher.unpowered(this);
    }

    @Override
    public void poweredTick(Level level, BlockPos blockPos, BlockState blockState) {
        animationDispatcher.powered(this);

        tickCounter++;

        var tickValue = AVP.config.blockConfigs.RESONATOR_REPLACE_TICKS;

        if (tickCounter % tickValue != 0) {
            return;
        }

        var radius = AVP.config.blockConfigs.RESONATOR_REPLACE_RADIUS;

        BlockPos.betweenClosedStream(blockPos.offset(-radius, -radius, -radius), blockPos.offset(radius, radius, radius))
            .forEach(currentPos -> {
                var currentState = level.getBlockState(currentPos);

                AlienVariantTypes.getFor(currentState)
                    .ifSome(alienVariantType -> {
                        // TODO: Use variant-specific tag here.
                        if (currentState.is(AVPBlockTags.RESIN_VEINS)) {
                            level.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());

                            var resinBallItem = alienVariantType.resinBall().get();
                            addResinBallItem(resinBallItem);

                            setChanged();

                            return;
                        }

                        // TODO: Use variant-specific tag here.
                        if (currentState.is(AVPBlockTags.RESIN)) {
                            // TODO: This is not a safe assumption to make!
                            var replacementBlock = currentPos.getY() <= 0 ? Blocks.DEEPSLATE : Blocks.STONE;

                            level.setBlockAndUpdate(currentPos, replacementBlock.defaultBlockState());

                            var resinBallItem = alienVariantType.resinBall().get();
                            addResinBallItem(resinBallItem);

                            setChanged();
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
}
