package com.avp.core.common.entity.living.alien.xenomorph.praetorian;

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
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.util.resin.ResinData;

public class Praetorian extends Xenomorph {

    public static AttributeSupplier.Builder createPraetorianAttributes() {
        var container = ConfigProperties.PRAETORIAN_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Monster.createMonsterAttributes());
    }

    private final PraetorianAnimationDispatcher animationDispatcher;

    public Praetorian(EntityType<? extends Praetorian> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new PraetorianAnimationDispatcher(this);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 64, 1, 4 * 20);
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
    public @Nullable ItemStack getPickResult() {
        SpawnEggItem spawnEggItem = null;

        if (isNetherAfflicted()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.NETHER_PRAETORIAN.get());
        }

        if (isAberrant()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.ABERRANT_PRAETORIAN.get());
        }

        return spawnEggItem == null ? super.getPickResult() : new ItemStack(spawnEggItem);
    }

    @Override
    protected @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        if (isNetherAfflicted()) {
            return AVPEntityTypes.NETHER_PRAETORIAN.get().getDefaultLootTable();
        }

        return super.getDefaultLootTable();
    }
}
