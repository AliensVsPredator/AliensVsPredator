package com.blib.mod.common.gameplay.goap;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.UUID;

@ApiStatus.Internal
public final class GOAPDebugTrackingState {

    private final List<UUID> entityUuids;

    private int selectedIndex;

    private boolean worldStateAutoPage = true;

    private int worldStatePage;

    GOAPDebugTrackingState(List<UUID> entityUuids, int selectedIndex) {
        this.entityUuids = entityUuids;
        this.selectedIndex = selectedIndex;
    }

    public List<UUID> entityUuids() {
        return entityUuids;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public boolean worldStateAutoPage() {
        return worldStateAutoPage;
    }

    public void setWorldStateAutoPage(boolean worldStateAutoPage) {
        this.worldStateAutoPage = worldStateAutoPage;
    }

    public int worldStatePage() {
        return worldStatePage;
    }

    public void setWorldStatePage(int worldStatePage) {
        this.worldStatePage = worldStatePage;
    }
}
