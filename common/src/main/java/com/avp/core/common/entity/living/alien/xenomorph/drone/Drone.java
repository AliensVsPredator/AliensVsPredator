package com.avp.core.common.entity.living.alien.xenomorph.drone;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.core.AVP;
import com.avp.core.common.ai.goal.combat.LungeAtTargetGoal;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.sound.AVPSoundEvents;
import com.avp.core.common.util.resin.ResinData;

public class Drone extends Xenomorph {

    public static AttributeSupplier.Builder createDroneAttributes() {
        var container = ConfigProperties.DRONE_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Monster.createMonsterAttributes());
    }

    private final DroneAnimationDispatcher animationDispatcher;

    public Drone(EntityType<? extends Drone> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new DroneAnimationDispatcher(this);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 16, 1, 20);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.05F, 20 * 7, 1, 12).setOnLungeCallback(this::runLungeAnimation));
    }

    @Override
    public void runPassiveAnimations() {
        var dispatcher = animationDispatcher;
        var isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();
        var isCrawling = crawlingManager.isCrawling();
        Runnable animFunction;

        if (isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            animFunction = isCrawling ? dispatcher::crawl : dispatcher::walk;
        } else {
            // TODO: idle crawl
            animFunction = isCrawling ? dispatcher::crawlHold : dispatcher::idle;
        }

        animFunction.run();
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
    public @Nullable ItemStack getPickResult() {
        SpawnEggItem spawnEggItem = null;

        if (isNetherAfflicted()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.NETHER_DRONE.get());
        }

        if (isAberrant()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.ABERRANT_DRONE.get());
        }

        return spawnEggItem == null ? super.getPickResult() : new ItemStack(spawnEggItem);
    }

    @Override
    protected @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        if (isNetherAfflicted()) {
            return AVPEntityTypes.NETHER_DRONE.get().getDefaultLootTable();
        }

        return super.getDefaultLootTable();
    }
}
