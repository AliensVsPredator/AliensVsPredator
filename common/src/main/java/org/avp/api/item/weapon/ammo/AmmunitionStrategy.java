package org.avp.api.item.weapon.ammo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.avp.api.item.weapon.WeaponItemData;

public abstract class AmmunitionStrategy {

    private final int maxAmmunition;

    public static Builder builder(int maxAmmunition) {
        return new Builder(maxAmmunition);
    }

    protected AmmunitionStrategy(int maxAmmunition) {
        this.maxAmmunition = maxAmmunition;
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

    public static class Builder {

        private final int maxAmmunition;

        private HasAmmunitionBehavior hasAmmunitionBehavior = HasAmmunitionBehavior.NO_OP;

        private Builder(int maxAmmunition) {
            this.maxAmmunition = maxAmmunition;
        }

        public Builder setHasAmmunitionBehavior(HasAmmunitionBehavior hasAmmunitionBehavior) {
            this.hasAmmunitionBehavior = hasAmmunitionBehavior;
            return this;
        }

        public AmmunitionStrategy build() {
            return new AmmunitionStrategy(maxAmmunition) {

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
