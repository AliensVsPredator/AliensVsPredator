package com.blib.event;

import net.minecraft.world.level.Level;

public interface BLibLevelTickEvent {

    void invoke(Level level);
}
