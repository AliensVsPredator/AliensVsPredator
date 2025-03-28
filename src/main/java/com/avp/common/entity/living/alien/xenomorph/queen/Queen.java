package com.avp.common.entity.living.alien.xenomorph.queen;

import com.avp.common.util.AlienVariantUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.block.AVPBlocks;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.util.resin.ResinData;

public class Queen extends Xenomorph {

    private final QueenAnimationDispatcher animationDispatcher;

    public Queen(EntityType<? extends Queen> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new QueenAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.QUEEN_STATS;
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        SpawnEggItem spawnEggItem = null;

        if (isNetherAfflicted()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.NETHER_QUEEN);
        }

        if (isAberrant()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.ABERRANT_QUEEN);
        }

        return spawnEggItem == null ? super.getPickResult() : new ItemStack(spawnEggItem);
    }

    public static AttributeSupplier.Builder createQueenAttributes() {
        return applyFrom(AVP.config.statsConfigs.QUEEN_STATS, Monster.createMonsterAttributes());
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.QUEEN_STATS.healthRegenPerSecond;
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 128, 1, AVP.config.statsConfigs.QUEEN_STATS.nestTickrate);
    }

    @Override
    public void runPassiveAnimations() {
//        var dispatcher = animationDispatcher;
//        var isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();
//        var isCrawling = crawlingManager.isCrawling();
//        Runnable animFunction;
//
//        if (isUnderWater()) {
//            // TODO: idle swim
//            animFunction = dispatcher::swim;
//        } else if (isMovingOnGround) {
//            animFunction = isCrawling ? dispatcher::crawl : dispatcher::walk;
//        } else {
//            // TODO: idle crawl
//            animFunction = isCrawling ? dispatcher::crawlHold : dispatcher::idle;
//        }
//
//        animFunction.run();
    }

    @Override
    public void runAttackAnimations() {
//        var isClawAttack = random.nextBoolean();
//
//        if (isClawAttack) {
//            animationDispatcher.clawAttack();
//        } else {
//            animationDispatcher.tailAttack();
//        }
    }

    // Queens are too large to be pushed by fluids.
    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    // Queens are too large to be pushed.
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public int maxJellyToGrowth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && tickCount < 2) {
            var belowBlockPos = blockPosition().below();
            if (!level().getBlockState(belowBlockPos).is(AlienVariantUtil.getResinNodeForType(this).getBlock())) {
                level().setBlockAndUpdate(belowBlockPos, AlienVariantUtil.getResinNodeForType(this));
            }
        }
    }
}
