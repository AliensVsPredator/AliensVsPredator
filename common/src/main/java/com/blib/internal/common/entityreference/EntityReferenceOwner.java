package com.blib.internal.common.entityreference;

import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.UUID;

@ApiStatus.Internal
public interface EntityReferenceOwner {

    String id();

    boolean referencesEntityUuid(UUID uuid);

    Set<UUID> referencedEntityUuids();

    void removeEntityReference(UUID uuid);
}
