package org.avp.common.registry;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.registry.AVPDeferredRegistry;
import org.avp.api.registry.holder.BLHolder;
import org.avp.api.util.Tuple;
import org.avp.common.registry.holder.AVPHolder;
import org.avp.common.registry.item.AVPItemRegistry;

public class AVPFuelRegistry extends AVPDeferredRegistry<Tuple<BLHolder<Item>, Integer>> {

    public static final AVPFuelRegistry INSTANCE = new AVPFuelRegistry();

    @Override
    protected BLHolder<Tuple<BLHolder<Item>, Integer>> createHolder(String registryName, Supplier<Tuple<BLHolder<Item>, Integer>> supplier) {
        var holder = new AVPHolder<>(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    @Override
    public void register() {
        createHolder("fuel_carbon", () -> new Tuple<>(AVPItemRegistry.INSTANCE.carbon, 800));
    }

    private AVPFuelRegistry() {}
}
