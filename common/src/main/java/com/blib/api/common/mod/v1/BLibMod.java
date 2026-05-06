package com.blib.api.common.mod.v1;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.blib.api.BLibAPI;
import com.blib.api.common.mod.v1.exception.BLibModInitializationException;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.mod.v1.model.Version;
import com.blib.api.common.mod.v1.model.access.BLibEventAccess;
import com.blib.api.common.mod.v1.model.access.BLibFactionAccess;
import com.blib.api.common.mod.v1.model.access.BLibFactoryAccess;
import com.blib.api.common.mod.v1.model.access.BLibModStateAccess;
import com.blib.api.common.mod.v1.model.access.BLibNetworkAccess;
import com.blib.api.common.mod.v1.model.access.BLibRegistryAccess;
import com.blib.api.common.mod.v1.model.access.BLibReputationAccess;
import com.blib.api.common.mod.v1.model.access.BLibResourceAccess;
import com.blib.api.common.mod.v1.model.access.BLibStorageAccess;
import com.blib.api.common.mod.v1.model.access.BLibTerritoryAccess;
import com.blib.internal.service.BLibInternalServices;

public class BLibMod implements BLibModStateAccess {

    private final String id;

    private final BLibEventAccess eventAccess;

    private final BLibFactionAccess factionAccess;

    private final BLibFactoryAccess factoryAccess;

    private final BLibNetworkAccess networkAccess;

    private final BLibRegistryAccess registryAccess;

    private final BLibReputationAccess reputationAccess;

    private final BLibResourceAccess resourceAccess;

    private final BLibStorageAccess storageAccess;

    private final BLibTerritoryAccess territoryAccess;

    /**
     * Resolved lazily on first {@link #version()} call rather than in the constructor. Resolving eagerly forces
     * the BLib mod-loader service to be ready at every site that touches a {@link BLibMod} instance — including
     * {@link com.blib.mod.BLib#MOD}'s static initializer. On NeoForge, {@code MainTarget} is constructed early
     * enough that {@code ModList.get()} returns {@code null} at that point, so any class that touches
     * {@code BLib} (e.g. {@code BLib.LOGGER} from the MRT mixin) would NPE during {@code <clinit>}.
     * <p>
     * Re-resolved on each call until non-null is returned, so an early caller getting {@code null} doesn't
     * permanently cache that — the next call after mod loading completes will pick up the real version.
     */
    private @Nullable Version version;

    private volatile BLibModState state;

    public BLibMod(String id) {
        this.id = id;
        this.eventAccess = new BLibEventAccess(this);
        this.factionAccess = new BLibFactionAccess(this);
        this.factoryAccess = new BLibFactoryAccess(this);
        this.networkAccess = new BLibNetworkAccess(this);
        this.registryAccess = new BLibRegistryAccess(this);
        this.reputationAccess = new BLibReputationAccess(this);
        this.resourceAccess = new BLibResourceAccess(this);
        this.storageAccess = new BLibStorageAccess(this);
        this.territoryAccess = new BLibTerritoryAccess(this);
        this.state = BLibModState.UNINITIALIZED;
    }

    @Override
    public BLibModState state() {
        return state;
    }

    public void initialize() {
        initialize(() -> {});
    }

    public void initialize(Runnable runnable) {
        if (state != BLibModState.UNINITIALIZED) {
            throw new BLibModInitializationException(
                "Attempted to initialize a mod that is either initializing or already initialized. Mod State: %s".formatted(state)
            );
        }

        BLibInternalServices.MOD.initialize(this, () -> {
            this.state = BLibModState.INITIALIZING;
            runnable.run();
            BLibInternalServices.MOD.postInitialize(this);
            this.state = BLibModState.INITIALIZED;
        });
    }

    public boolean isLoaded() {
        return BLibAPI.isModLoaded(id);
    }

    public BLibEventAccess events() {
        return eventAccess;
    }

    public BLibFactionAccess factions() {
        return factionAccess;
    }

    public BLibFactoryAccess factories() {
        return factoryAccess;
    }

    public String id() {
        return id;
    }

    public BLibNetworkAccess networking() {
        return networkAccess;
    }

    public BLibRegistryAccess registries() {
        return registryAccess;
    }

    public BLibReputationAccess reputation() {
        return reputationAccess;
    }

    public BLibResourceAccess resources() {
        return resourceAccess;
    }

    public BLibStorageAccess storage() {
        return storageAccess;
    }

    public BLibTerritoryAccess territory() {
        return territoryAccess;
    }

    public synchronized @Nullable Version version() {
        if (version == null) {
            version = BLibAPI.getModVersion(id);
        }
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BLibMod mod)) {
            return false;
        }

        return Objects.equals(id, mod.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
