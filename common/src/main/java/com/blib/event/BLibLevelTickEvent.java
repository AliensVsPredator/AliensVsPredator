package com.blib.event;

import net.minecraft.world.level.Level;

public interface BLibLevelTickEvent {

    Level level();

    record Pre(Level level) implements BLibLevelTickEvent {}

    record Post(Level level) implements BLibLevelTickEvent {}
}
