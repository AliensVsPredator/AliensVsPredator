package com.avp.common.entity.living.alien.xenomorph.warrior;

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

import com.avp.AVP;
import com.avp.common.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.util.resin.ResinData;

public class Warrior extends Xenomorph {

    private final WarriorAnimationDispatcher animationDispatcher;

    public Warrior(EntityType<? extends Warrior> entityType, Level level) {
        super(entityType, level);
        this.attackDelayTicks = 7;
        this.animationDispatcher = new WarriorAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.WARRIOR_STATS;
    }

    public static AttributeSupplier.Builder createWarriorAttributes() {
        return applyFrom(AVP.config.statsConfigs.WARRIOR_STATS, Monster.createMonsterAttributes());
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

        playSound(AVPSoundEvents.ENTITY_XENOMORPH_ATTACK, getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

        if (isClawAttack) {
            animationDispatcher.clawAttack();
        } else {
            animationDispatcher.tailAttack();
        }
    }

    private void runLungeAnimation() {
        playSound(AVPSoundEvents.ENTITY_XENOMORPH_LUNGE, getSoundVolume(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        animationDispatcher.lunge();
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        SpawnEggItem spawnEggItem = null;

        if (isNetherAfflicted()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.NETHER_WARRIOR);
        }

        if (isAberrant()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.ABERRANT_WARRIOR);
        }

        if (isIrradiated()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.IRRADIATED_WARRIOR);
        }

        return spawnEggItem == null ? super.getPickResult() : new ItemStack(spawnEggItem);
    }

    @Override
    protected @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        if (isNetherAfflicted()) {
            return AVPEntityTypes.NETHER_WARRIOR.getDefaultLootTable();
        }

        return super.getDefaultLootTable();
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
}
