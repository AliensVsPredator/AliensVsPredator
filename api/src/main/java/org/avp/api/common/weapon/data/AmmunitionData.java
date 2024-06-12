package org.avp.api.common.weapon.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.avp.api.common.weapon.ammunition.HasAmmunitionBehavior;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public record AmmunitionData(
    int consumedAmmunition,
    int maxAmmunition,
    List<Supplier<Item>> ammunitionSuppliers,
    HasAmmunitionBehavior hasAmmunitionBehavior
) {
    public Supplier<Item> getAmmunitionSupplierByKeyOrFirst(String resourceLocationString) {
        var resourceLocation = new ResourceLocation(resourceLocationString);
        var item = BuiltInRegistries.ITEM.get(resourceLocation);
        return ammunitionSuppliers.stream()
            .filter(supplier -> Objects.equals(supplier.get(), item))
            .findFirst()
            .orElse(ammunitionSuppliers.get(0));
    }
}
