package com.blib.internal.common.event;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.event.v1.BLibChunkSaveEvent;
import com.blib.api.common.event.v1.BLibChunkUnloadEvent;
import com.blib.api.common.event.v1.BLibEntityRemoveEvent;
import com.blib.api.common.event.v1.BLibEntityTickEvent;
import com.blib.api.common.event.v1.BLibLevelSaveEvent;
import com.blib.api.common.event.v1.BLibServerSaveEvent;
import com.blib.api.common.event.v1.handle.BLibGlobalEventHandle;

@ApiStatus.Internal
public final class BLibGlobalEvents {

    public static final BLibGlobalEventHandle<BLibChunkSaveEvent> CHUNK_SAVE = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibChunkUnloadEvent> CHUNK_UNLOAD = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibEntityRemoveEvent> ENTITY_REMOVE = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibEntityTickEvent> ENTITY_TICK = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibLevelSaveEvent> LEVEL_SAVE = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibServerSaveEvent> SERVER_SAVE = new BLibGlobalEventHandle<>();

    private BLibGlobalEvents() {}
}
