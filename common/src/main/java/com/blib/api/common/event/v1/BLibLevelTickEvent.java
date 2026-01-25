package com.blib.api.common.event.v1;

import net.minecraft.world.level.Level;

public interface BLibLevelTickEvent {

    void invoke(Level level);
}
