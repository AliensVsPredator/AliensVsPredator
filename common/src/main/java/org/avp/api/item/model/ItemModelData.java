package org.avp.api.item.model;

import net.minecraft.world.item.Item;
import org.avp.api.item.model.type.ItemModelDataType;

import java.util.function.Function;
import java.util.function.Supplier;

public record ItemModelData(
    Supplier<Item> itemSupplier,
    Function<Item, ItemModelDataType> itemModelDataTypeFactory
) {

    public static ItemModelData none() {
        return new ItemModelData(
            () -> new Item(new Item.Properties()),
            $ -> new ItemModelDataType.None()
        );
    }

    public static ItemModelData none(Supplier<Item> itemFactory) {
        return new ItemModelData(itemFactory, $ -> new ItemModelDataType.None());
    }

    public static ItemModelData flat() {
        return flat(new Item.Properties());
    }

    public static ItemModelData flat(Item.Properties properties) {
        return new ItemModelData(
            () -> new Item(properties),
            ItemModelDataType.Flat::new
        );
    }

    public static ItemModelData flat(Function<Item.Properties, Item> itemFactory) {
        return new ItemModelData(
            () -> itemFactory.apply(new Item.Properties()),
            ItemModelDataType.Flat::new
        );
    }
}
