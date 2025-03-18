package com.avp.common.entity.living.alien.xenomorph.queen;

import com.avp.common.entity.type.AVPEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.avp.AVP;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.util.resin.ResinData;
import org.jetbrains.annotations.Nullable;

public class Queen extends Xenomorph {

    public Queen(EntityType<? extends Queen> entityType, Level level) {
        super(entityType, level);
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
    public void runPassiveAnimations() {}

    @Override
    public void runAttackAnimations() {
        // TODO:
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
}
