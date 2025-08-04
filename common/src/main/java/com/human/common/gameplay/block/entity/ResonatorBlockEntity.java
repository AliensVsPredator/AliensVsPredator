package com.human.common.gameplay.block.entity;

import com.human.common.gameplay.block.entity.power.PowerConsumerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.avp.common.registry.init.AVPBlockEntityTypes;

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
        this.tickCounter++;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public ResonatorAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }
}
