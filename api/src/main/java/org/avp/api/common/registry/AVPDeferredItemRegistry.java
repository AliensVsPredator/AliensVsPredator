package org.avp.api.common.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.common.data.item.ItemData;
import org.avp.api.common.data.item.ItemModelData;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.Services;

public class AVPDeferredItemRegistry extends AVPDeferredRegistry<Item> {

    private static final Map<BLHolder<Item>, ItemData> DATA_ENTRIES = new LinkedHashMap<>();

    public static Set<Map.Entry<BLHolder<Item>, ItemData>> getDataEntries() {
        return DATA_ENTRIES.entrySet();
    }

    protected BLHolder<Item> createHolder(String registryName) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(), Set.of()));
    }

    protected BLHolder<Item> createHolder(String registryName, Set<TagKey<Item>> tags) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(), tags));
    }

    protected BLHolder<Item> createHolder(String registryName, Item.Properties properties) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(properties), Set.of()));
    }

    protected BLHolder<Item> createHolder(String registryName, Item.Properties properties, Set<TagKey<Item>> tags) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(properties), tags));
    }

    protected BLHolder<Item> createHolder(String registryName, Function<Item.Properties, Item> itemFactory) {
        return createHolder(new ItemData(registryName, ItemModelData.flat(itemFactory), Set.of()));
    }

    protected BLHolder<Item> createHolder(String registryName, ItemModelData itemModelData, Set<TagKey<Item>> itemTagData) {
        return createHolder(new ItemData(registryName, itemModelData, itemTagData));
    }

    protected BLHolder<Item> createHolder(ItemData itemData) {
        var registryName = itemData.registryName();
        var holder = createHolderInternal(registryName, () -> itemData.itemModelData().itemSupplier().get());
        entries.put(registryName, holder);
        DATA_ENTRIES.put(holder, itemData);
        return holder;
    }

    private BLHolder<Item> createHolderInternal(String registryName, Supplier<Item> supplier) {
        return Services.ITEM_SERVICE.createHolder(registryName, supplier);
    }

    /**
     * @deprecated Do not use directly.
     */
    @Deprecated
    @Override
    protected final BLHolder<Item> createHolder(String registryName, Supplier<Item> supplier) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void register() {
        getValues().forEach(Services.ITEM_SERVICE::register);
    }
}
