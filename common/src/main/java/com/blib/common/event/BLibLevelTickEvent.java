package com.blib.common.event;

import net.minecraft.world.level.Level;

public interface BLibLevelTickEvent {

    void invoke(Level level);
}
