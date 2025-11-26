package com.blib.event.key;

import com.blib.event.BLibLevelTickEvent;
import com.blib.event.BLibTagsUpdatedEvent;

public class BLibEventKeys {

    public static final BLibEventKey<BLibLevelTickEvent.Pre> LEVEL_TICK_PRE = new BLibEventKey<>("level_tick_pre");

    public static final BLibEventKey<BLibLevelTickEvent.Post> LEVEL_TICK_POST = new BLibEventKey<>("level_tick_post");

    public static final BLibEventKey<BLibTagsUpdatedEvent> TAGS_UPDATED = new BLibEventKey<>("tags_updated");
}
