package org.avp.api.item.weapon.ammo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.avp.api.item.weapon.WeaponItemData;

import java.util.function.Supplier;

public abstract class AmmunitionStrategy {

    private final int maxAmmunition;

    private final Supplier<Item> standardAmmunitionSupplier;

    public static Builder builder(int maxAmmunition, Supplier<Item> standardAmmunitionSupplier) {
        return new Builder(maxAmmunition, standardAmmunitionSupplier);
    }

    protected AmmunitionStrategy(
        int maxAmmunition,
        Supplier<Item> standardAmmunitionSupplier
    ) {
        this.maxAmmunition = maxAmmunition;
        this.standardAmmunitionSupplier = standardAmmunitionSupplier;
    }

    public abstract boolean hasAmmunition(
        ServerLevel serverLevel,
        ServerPlayer serverPlayer,
        ItemStack itemStack,
        WeaponItemData weaponItemData
    );

    public int getMaxAmmunition() {
        return maxAmmunition;
    }

    public Supplier<Item> getStandardAmmunitionSupplier() {
        return standardAmmunitionSupplier;
    }

    public static class Builder {

        private final int maxAmmunition;

        private final Supplier<Item> standardAmmunitionSupplier;

        private HasAmmunitionBehavior hasAmmunitionBehavior = HasAmmunitionBehavior.NO_OP;

        private Builder(int maxAmmunition, Supplier<Item> standardAmmunitionSupplier) {
            this.maxAmmunition = maxAmmunition;
            this.standardAmmunitionSupplier = standardAmmunitionSupplier;
        }

        public Builder setHasAmmunitionBehavior(HasAmmunitionBehavior hasAmmunitionBehavior) {
            this.hasAmmunitionBehavior = hasAmmunitionBehavior;
            return this;
        }

        public AmmunitionStrategy build() {
            return new AmmunitionStrategy(maxAmmunition, standardAmmunitionSupplier) {

                @Override
                public boolean hasAmmunition(
                    ServerLevel serverLevel,
                    ServerPlayer serverPlayer,
                    ItemStack itemStack,
                    WeaponItemData weaponItemData
                ) {
                    return hasAmmunitionBehavior.hasAmmunition(serverLevel, serverPlayer, itemStack, weaponItemData);
                }
            };
        }
    }
}
