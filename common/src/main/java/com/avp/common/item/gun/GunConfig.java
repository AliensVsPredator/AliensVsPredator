package com.avp.common.item.gun;

import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public record GunConfig(
    @Nullable Supplier<ItemLike> ammunitionItemSupplier,
    int durability,
    List<FireModeConfig> fireModeConfigs,
    int maximumAmmunition,
    int reloadAmount,
    int reloadTimeInTicks
) {

    public @NotNull FireModeConfig getDefaultFireMode() {
        return fireModeConfigs.getFirst();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<FireModeConfig> fireModeConfigs;

        private Supplier<ItemLike> ammunitionItemSupplier;

        private int durability;

        private int maximumAmmunition;

        private int reloadAmount;

        private int reloadTimeInTicks;

        private Builder() {
            this.fireModeConfigs = new ArrayList<>();
            this.reloadAmount = 1;
        }

        public Builder withAmmunitionItemSupplier(Supplier<ItemLike> ammunitionItemSupplier) {
            this.ammunitionItemSupplier = ammunitionItemSupplier;
            return this;
        }

        public Builder withDurability(int durability) {
            this.durability = durability;
            return this;
        }

        public Builder withFireMode(FireModeConfig fireModeConfig) {
            fireModeConfigs.add(fireModeConfig);
            return this;
        }

        public Builder withMaximumAmmunition(int defaultMaxAmmunition) {
            this.maximumAmmunition = defaultMaxAmmunition;
            return this;
        }

        // TODO: Reload data should probably be in an object.
        public Builder withReloadTimeInTicks(int reloadTimeInTicks) {
            this.reloadTimeInTicks = reloadTimeInTicks;
            return this;
        }

        // TODO: Reload data should probably be in an object.
        public Builder withReloadAmount(int reloadAmount) {
            this.reloadAmount = reloadAmount;
            return this;
        }

        public GunConfig build() {
            if (fireModeConfigs.isEmpty()) {
                throw new IllegalStateException("At least 1 fire mode for guns is required.");
            }

            return new GunConfig(
                ammunitionItemSupplier,
                durability,
                Collections.unmodifiableList(fireModeConfigs),
                maximumAmmunition,
                reloadAmount,
                reloadTimeInTicks
            );
        }
    }
}
