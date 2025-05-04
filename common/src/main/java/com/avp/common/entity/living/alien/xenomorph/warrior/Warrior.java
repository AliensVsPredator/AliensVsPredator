package com.avp.common.entity.living.alien.xenomorph.warrior;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.util.resin.ResinData;

public class Warrior extends Xenomorph {

    public static AttributeSupplier.Builder createWarriorAttributes() {
        return applyFrom(AVP.config.statsConfigs.WARRIOR_STATS, Monster.createMonsterAttributes());
    }

    private final WarriorAnimationDispatcher animationDispatcher;

    public Warrior(EntityType<? extends Warrior> entityType, Level level) {
        super(entityType, level);
        this.attackDelayTicks = 7;
        this.animationDispatcher = new WarriorAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.WARRIOR_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Xenomorph> getAberrantType() {
        return AVPEntityTypes.ABERRANT_WARRIOR.get();
    }

    @Override
    public @Nullable EntityType<? extends Xenomorph> getIrradiatedType() {
        return AVPEntityTypes.IRRADIATED_WARRIOR.get();
    }

    @Override
    public @Nullable EntityType<? extends Xenomorph> getNetherType() {
        return AVPEntityTypes.NETHER_WARRIOR.get();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.WARRIOR_STATS.healthRegenPerSecond;
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 32, 1, AVP.config.statsConfigs.WARRIOR_STATS.nestTickrate);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.1F, 20 * 5, 6, 15).setOnLungeCallback(this::runLungeAnimation));
    }

    @Override
    public void runAttackAnimations() {
        var isClawAttack = random.nextBoolean();

        playSound(AVPSoundEvents.ENTITY_XENOMORPH_ATTACK.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

        if (isClawAttack) {
            animationDispatcher.clawAttack();
        } else {
            animationDispatcher.tailAttack();
        }
    }

    private void runLungeAnimation() {
        playSound(AVPSoundEvents.ENTITY_XENOMORPH_LUNGE.get(), getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        animationDispatcher.lunge();
    }

    @Override
    public int maxJellyToGrowth() {
        return 4;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            becomeIrradiated();
        }
    }

    public WarriorAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }
}
