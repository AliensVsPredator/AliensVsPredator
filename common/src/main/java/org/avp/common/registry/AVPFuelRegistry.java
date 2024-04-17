package org.avp.common.registry;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.api.Tuple;
import org.avp.common.item.AVPItems;

public class AVPFuelRegistry extends AVPDeferredRegistry<Tuple<GameObject<Item>, Integer>> {

    public static final AVPFuelRegistry INSTANCE = new AVPFuelRegistry();

    @Override
    protected GameObject<Tuple<GameObject<Item>, Integer>> createHolder(String registryName, Supplier<Tuple<GameObject<Item>, Integer>> supplier) {
        return new GameObject<>(registryName, supplier);
    }

    @Override
    public void register() {
        createHolder("fuel_carbon", () -> new Tuple<>(AVPItems.INSTANCE.CARBON, 800));
    }

    private AVPFuelRegistry() {
        throw new UnsupportedOperationException();
    }
}
