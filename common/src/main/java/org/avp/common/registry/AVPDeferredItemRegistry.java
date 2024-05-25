package org.avp.common.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.avp.api.Holder;
import org.avp.api.Tuple;
import org.avp.api.item.ItemData;
import org.avp.api.item.model.ItemModelData;
import org.avp.common.service.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class AVPDeferredItemRegistry extends AVPDeferredRegistry<Item> {

    private static final List<Tuple<Holder<Item>, ItemData>> DATA_ENTRIES = new ArrayList<>();

    public static List<Tuple<Holder<Item>, ItemData>> getDataEntries() {
        return DATA_ENTRIES;
    }

    protected Holder<Item> createHolder(String registryName) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(), Set.of()));
    }

    protected Holder<Item> createHolder(String registryName, Set<TagKey<Item>> tags) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(), tags));
    }

    protected Holder<Item> createHolder(String registryName, Item.Properties properties) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(properties), Set.of()));
    }

    protected Holder<Item> createHolder(String registryName, Item.Properties properties, Set<TagKey<Item>> tags) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(properties), tags));
    }

    protected Holder<Item> createHolder(String registryName, Function<Item.Properties, Item> itemFactory) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(itemFactory), Set.of()));
    }

    protected Holder<Item> createHolder(String registryName, ItemModelData itemModelData, Set<TagKey<Item>> itemTagData) {
        return createHolder(new ItemData(registryName, itemModelData, itemTagData));
    }

    protected Holder<Item> createHolder(ItemData itemData) {
        var registryName = itemData.registryName();
        var holder = createHolderInternal(registryName,() -> itemData.itemModelData().itemSupplier().get());
        entries.put(registryName, holder);
        DATA_ENTRIES.add(new Tuple<>(holder, itemData));
        return holder;
    }

    private Holder<Item> createHolderInternal(String registryName, Supplier<Item> supplier) {
        return Services.ITEM_SERVICE.createHolder(registryName, supplier);
    }

    /**
     * @deprecated Do not use directly.
     */
    @Deprecated
    @Override
    protected final Holder<Item> createHolder(String registryName, Supplier<Item> supplier) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void register() {
        getValues().forEach(Services.ITEM_SERVICE::register);
    }
}
