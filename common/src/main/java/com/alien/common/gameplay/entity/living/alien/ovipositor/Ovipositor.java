package com.alien.common.gameplay.entity.living.alien.ovipositor;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class Ovipositor extends Mob {

    public Ovipositor(EntityType<? extends Ovipositor> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean attackable() {
        return false;
    }
}
