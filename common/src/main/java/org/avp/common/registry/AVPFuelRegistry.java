package org.avp.common.registry;

import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.function.Supplier;

import org.avp.api.common.registry.AVPDeferredRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.registry.holder.AVPHolder;
import org.avp.common.registry.item.AVPItemRegistry;

public class AVPFuelRegistry extends AVPDeferredRegistry<Map.Entry<BLHolder<Item>, Integer>> {

    public static final AVPFuelRegistry INSTANCE = new AVPFuelRegistry();

    @Override
    protected BLHolder<Map.Entry<BLHolder<Item>, Integer>> createHolder(
        String registryName,
        Supplier<Map.Entry<BLHolder<Item>, Integer>> supplier
    ) {
        var holder = new AVPHolder<>(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    @Override
    public void register() {
        createHolder("fuel_carbon", () -> Map.entry(AVPItemRegistry.INSTANCE.carbon, 800));
    }

    private AVPFuelRegistry() {}
}
