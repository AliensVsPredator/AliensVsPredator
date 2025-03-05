package com.avp.common.entity.living.alien.xenomorph.queen;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.avp.AVP;
import com.avp.common.config.ConfigProperties;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.util.resin.ResinData;

public class Queen extends Xenomorph {

    public static AttributeSupplier.Builder createQueenAttributes() {
        var container = ConfigProperties.QUEEN_ATTRIBUTES;
        return container.applyFrom(AVP.STATS_CONFIG, Monster.createMonsterAttributes());
    }

    public Queen(EntityType<? extends Queen> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 128, 1, 8 * 20);
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
}
