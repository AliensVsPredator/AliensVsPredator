package com.blib.internal.common.event;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.event.v1.BLibChunkClaimAddedEvent;
import com.blib.api.common.event.v1.BLibChunkClaimRemovedEvent;
import com.blib.api.common.event.v1.BLibChunkLoadEvent;
import com.blib.api.common.event.v1.BLibChunkSaveEvent;
import com.blib.api.common.event.v1.BLibChunkUnloadEvent;
import com.blib.api.common.event.v1.BLibEntityLoadEvent;
import com.blib.api.common.event.v1.BLibEntityRemoveEvent;
import com.blib.api.common.event.v1.BLibEntityTickEvent;
import com.blib.api.common.event.v1.BLibFactionCreatedEvent;
import com.blib.api.common.event.v1.BLibFactionDataChangedEvent;
import com.blib.api.common.event.v1.BLibFactionMemberChangedEvent;
import com.blib.api.common.event.v1.BLibFactionRelationshipChangedEvent;
import com.blib.api.common.event.v1.BLibFactionRemoveEvent;
import com.blib.api.common.event.v1.BLibFactionsLoadedEvent;
import com.blib.api.common.event.v1.BLibLevelSaveEvent;
import com.blib.api.common.event.v1.BLibServerSaveEvent;
import com.blib.api.common.event.v1.handle.BLibGlobalEventHandle;

@ApiStatus.Internal
public final class BLibGlobalEvents {

    public static final BLibGlobalEventHandle<BLibChunkClaimAddedEvent> CHUNK_CLAIM_ADDED = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibChunkClaimRemovedEvent> CHUNK_CLAIM_REMOVED = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibChunkLoadEvent> CHUNK_LOAD = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibChunkSaveEvent> CHUNK_SAVE = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibChunkUnloadEvent> CHUNK_UNLOAD = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibEntityLoadEvent> ENTITY_LOAD = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibEntityRemoveEvent> ENTITY_REMOVE = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibEntityTickEvent> ENTITY_TICK = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibFactionCreatedEvent> FACTION_CREATED = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibFactionDataChangedEvent> FACTION_DATA_CHANGED = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibFactionMemberChangedEvent> FACTION_MEMBER_CHANGED = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibFactionRelationshipChangedEvent> FACTION_RELATIONSHIP_CHANGED =
        new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibFactionRemoveEvent> FACTION_REMOVE = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibFactionsLoadedEvent> FACTIONS_LOADED = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibLevelSaveEvent> LEVEL_SAVE = new BLibGlobalEventHandle<>();

    public static final BLibGlobalEventHandle<BLibServerSaveEvent> SERVER_SAVE = new BLibGlobalEventHandle<>();

    private BLibGlobalEvents() {}
}
