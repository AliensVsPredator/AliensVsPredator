package com.alien.common.gameplay.entity.living.alien.ovipositor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.avp.common.registry.tag.AVPDamageTypesTags;

public class Ovipositor extends Mob {

    public Ovipositor(EntityType<? extends Ovipositor> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean attackable() {
        return false;
    }

    // TODO: Replace with a proper tag.
    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float amount) {
        if (damageSource.is(AVPDamageTypesTags.DOES_NOT_HURT_ALIENS)) {
            return false;
        }

        return super.hurt(damageSource, amount);
    }
}
