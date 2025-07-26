package com.alien.common.gameplay.entity.living.alien.ovipositor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.avp.common.registry.tag.AVPDamageTypesTags;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class Ovipositor extends Mob {

    public static AttributeSupplier.Builder createOvipositorAttributes() {
        return createMobAttributes()
            .add(Attributes.MAX_HEALTH, 100)
            .add(Attributes.MOVEMENT_SPEED, 0);
    }

    public Ovipositor(EntityType<? extends Ovipositor> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float amount) {
        if (damageSource.is(AVPDamageTypesTags.DOES_NOT_HURT_ALIENS)) {
            return false;
        }

        return super.hurt(damageSource, amount);
    }

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    protected final boolean canRide(@NotNull Entity vehicle) {
        return super.canRide(vehicle) && vehicle.getType().is(AVPEntityTypeTags.QUEENS);
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    protected final boolean canAddPassenger(@NotNull Entity passenger) {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {}

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
}
