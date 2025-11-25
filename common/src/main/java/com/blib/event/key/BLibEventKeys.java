package com.blib.event.key;

import com.blib.event.BLibLevelTickEvent;

public class BLibEventKeys {

    public static final BLibEventKey<BLibLevelTickEvent.Pre> LEVEL_TICK_PRE = new BLibEventKey<>("level_tick_pre");

    public static final BLibEventKey<BLibLevelTickEvent.Post> LEVEL_TICK_POST = new BLibEventKey<>("level_tick_post");
}
