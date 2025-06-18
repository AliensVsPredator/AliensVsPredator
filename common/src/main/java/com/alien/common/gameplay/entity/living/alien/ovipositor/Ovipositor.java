package com.alien.common.gameplay.entity.living.alien.ovipositor;

import com.alien.common.util.AlienHurtUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (AlienHurtUtil.isNonDamagingSource(source)) {
            // Ovipositors should not drown, freeze or suffocate.
            return false;
        }

        return super.hurt(source, amount);
    }
}
