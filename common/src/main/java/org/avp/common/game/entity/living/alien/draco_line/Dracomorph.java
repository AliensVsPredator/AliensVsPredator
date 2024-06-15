package org.avp.common.game.entity.living.alien.draco_line;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import org.avp.common.ai.AIUtils;
import org.avp.common.game.entity.living.alien.base_line.Queen;
import org.avp.common.game.entity.type.Boss;

public class Dracomorph extends Monster implements Boss, GeoEntity {

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
        this.getDisplayName(),
        BossEvent.BossBarColor.GREEN,
        BossEvent.BossBarOverlay.PROGRESS
    ).setDarkenScreen(true);

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Dracomorph(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        AIUtils.addAlienAI(this, goalSelector, targetSelector);
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Dracomorph.class, true, Dracomorph.class::isInstance));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Queen.class, true, Queen.class::isInstance));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO:
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public ServerBossEvent getBossEvent() {
        return bossEvent;
    }
}
