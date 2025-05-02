package com.avp.common.entity.living.alien.xenomorph.praetorian;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.util.resin.ResinData;

public class Praetorian extends Xenomorph {

    public static AttributeSupplier.Builder createPraetorianAttributes() {
        return applyFrom(AVP.config.statsConfigs.PRAETORIAN_STATS, Monster.createMonsterAttributes());
    }

    private final PraetorianAnimationDispatcher animationDispatcher;

    public Praetorian(EntityType<? extends Praetorian> entityType, Level level) {
        super(entityType, level);
        this.attackDelayTicks = 10;
        this.animationDispatcher = new PraetorianAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.PRAETORIAN_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getAberrantType() {
        return TempAVPEntityTypes.ABERRANT_PRAETORIAN.get();
    }

    @Override
    public @Nullable EntityType<? extends Alien> getIrradiatedType() {
        return TempAVPEntityTypes.IRRADIATED_PRAETORIAN.get();
    }

    @Override
    public @Nullable EntityType<? extends Alien> getNetherType() {
        return TempAVPEntityTypes.NETHER_PRAETORIAN.get();
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 64, 1, AVP.config.statsConfigs.PRAETORIAN_STATS.nestTickrate);
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.PRAETORIAN_STATS.healthRegenPerSecond;
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

    // Praetorians are too large to be pushed by fluids.
    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    // Praetorians are too large to be pushed.
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public int maxJellyToGrowth() {
        return 9;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            becomeIrradiated();
        }
    }

    public PraetorianAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }
}
