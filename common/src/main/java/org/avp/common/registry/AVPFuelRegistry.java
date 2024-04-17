package org.avp.common.registry;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.Tuple;
import org.avp.common.item.AVPItems;

public class AVPFuelRegistry extends AVPDeferredRegistry<Tuple<Holder<Item>, Integer>> {

    public static final AVPFuelRegistry INSTANCE = new AVPFuelRegistry();

    @Override
    protected Holder<Tuple<Holder<Item>, Integer>> createHolder(String registryName, Supplier<Tuple<Holder<Item>, Integer>> supplier) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    public void register() {
        createHolder("fuel_carbon", () -> new Tuple<>(AVPItems.INSTANCE.CARBON, 800));
    }

    private AVPFuelRegistry() {}
}
