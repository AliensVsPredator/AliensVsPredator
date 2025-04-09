package com.avp.common.entity.living.alien.xenomorph.queen;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.block.AVPBlockTags;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.util.AlienVariantUtil;
import com.avp.common.util.resin.ResinData;

public class Queen extends Xenomorph {

    public static AttributeSupplier.Builder createQueenAttributes() {
        return applyFrom(AVP.config.statsConfigs.QUEEN_STATS, Monster.createMonsterAttributes());
    }

    private final QueenAnimationDispatcher animationDispatcher;

    public Queen(EntityType<? extends Queen> entityType, Level level) {
        super(entityType, level);
        this.attackDelayTicks = 20;
        this.animationDispatcher = new QueenAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.QUEEN_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getAberrantType() {
        return AVPEntityTypes.ABERRANT_QUEEN;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getIrradiatedType() {
        return null;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getNetherType() {
        return AVPEntityTypes.NETHER_QUEEN;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getDefaultType() {
        return AVPEntityTypes.QUEEN;
    }

    @Override
    public float maxUpStep() {
        return 2.5F;
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
        var dispatcher = animationDispatcher;
        var isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();
        Runnable animFunction;

        if (isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            animFunction = dispatcher::walk;
        } else {
            // TODO: idle crawl
            animFunction = dispatcher::idle;
        }

        animFunction.run();
    }

    @Override
    public void runAttackAnimations() {
        var isClawAttack = random.nextBoolean();

        if (isClawAttack) {
            animationDispatcher.clawAttack();
        } else {
            animationDispatcher.tailAttack();
        }
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
            var blockState = level().getBlockState(belowBlockPos);
            var resinNode = AlienVariantUtil.getResinNodeForType(this).getBlock();

            if (!blockState.is(resinNode) && !blockState.is(AVPBlockTags.ACID_IMMUNE)) {
                level().setBlockAndUpdate(belowBlockPos, AlienVariantUtil.getResinNodeForType(this));
            }
        }
    }
}
