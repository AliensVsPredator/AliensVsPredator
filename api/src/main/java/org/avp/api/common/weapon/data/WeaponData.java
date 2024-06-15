package org.avp.api.common.weapon.data;

import java.util.List;
import java.util.Objects;

public abstract class WeaponData {

    private final List<FireModeData> fireModeDataList = createFireModeData();

    protected abstract List<FireModeData> createFireModeData();

    public abstract int getDurability();

    public final List<FireModeData> getFireModeDataList() {
        return fireModeDataList;
    }

    public final FireModeData getFireModeByIdOrFirst(String identifier) {
        return this.getFireModeDataList()
            .stream()
            .filter(fireModeData -> Objects.equals(fireModeData.identifier(), identifier))
            .findFirst()
            .or(() -> this.getFireModeDataList().stream().findFirst())
            .orElseThrow();
    }
}
