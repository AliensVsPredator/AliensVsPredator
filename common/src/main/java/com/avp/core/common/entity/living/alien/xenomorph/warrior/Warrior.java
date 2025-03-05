package com.avp.core.common.entity.living.alien.xenomorph.warrior;

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

public class Warrior extends Xenomorph {

    public static AttributeSupplier.Builder createWarriorAttributes() {
        var container = ConfigProperties.WARRIOR_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Monster.createMonsterAttributes());
    }

    private final WarriorAnimationDispatcher animationDispatcher;

    public Warrior(EntityType<? extends Warrior> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new WarriorAnimationDispatcher(this);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 32, 1, 2 * 20);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.1F, 20 * 5, 1, 15).setOnLungeCallback(this::runLungeAnimation));
    }

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
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.NETHER_WARRIOR.get());
        }

        if (isAberrant()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.ABERRANT_WARRIOR.get());
        }

        return spawnEggItem == null ? super.getPickResult() : new ItemStack(spawnEggItem);
    }

    @Override
    protected @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        if (isNetherAfflicted()) {
            return AVPEntityTypes.NETHER_WARRIOR.get().getDefaultLootTable();
        }

        return super.getDefaultLootTable();
    }
}
