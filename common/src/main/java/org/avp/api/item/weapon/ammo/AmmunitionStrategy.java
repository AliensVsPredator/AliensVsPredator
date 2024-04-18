package org.avp.api.item.weapon.ammo;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.avp.api.item.weapon.WeaponItemData;

public abstract class AmmunitionStrategy {

    private final int maxAmmunition;

    private final List<Supplier<Item>> ammunitionSuppliers;

    public static Builder builder(
        int maxAmmunition,
        List<Supplier<Item>> ammunitionSuppliers
    ) {
        return new Builder(maxAmmunition, ammunitionSuppliers);
    }

    protected AmmunitionStrategy(
        int maxAmmunition,
        List<Supplier<Item>> ammunitionSuppliers
    ) {
        this.maxAmmunition = maxAmmunition;
        this.ammunitionSuppliers = ammunitionSuppliers;
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

    public List<Supplier<Item>> getAmmunitionSuppliers() {
        return ammunitionSuppliers;
    }

    public Supplier<Item> getAmmunitionSupplierByKeyOrFirst(String resourceLocationString) {
        var resourceLocation = new ResourceLocation(resourceLocationString);
        var item = BuiltInRegistries.ITEM.get(resourceLocation);
        return ammunitionSuppliers.stream()
            .filter(supplier -> Objects.equals(supplier.get(), item))
            .findFirst()
            .orElse(ammunitionSuppliers.get(0));
    }

    public static class Builder {

        private final int maxAmmunition;

        private final List<Supplier<Item>> ammunitionSuppliers;

        private HasAmmunitionBehavior hasAmmunitionBehavior = HasAmmunitionBehavior.NO_OP;

        private Builder(int maxAmmunition, List<Supplier<Item>> ammunitionSuppliers) {
            this.maxAmmunition = maxAmmunition;
            this.ammunitionSuppliers = ammunitionSuppliers;
        }

        public Builder setHasAmmunitionBehavior(HasAmmunitionBehavior hasAmmunitionBehavior) {
            this.hasAmmunitionBehavior = hasAmmunitionBehavior;
            return this;
        }

        public AmmunitionStrategy build() {
            return new AmmunitionStrategy(maxAmmunition, ammunitionSuppliers) {

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
