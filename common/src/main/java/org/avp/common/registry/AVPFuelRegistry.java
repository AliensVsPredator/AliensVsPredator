package org.avp.common.registry;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;
import org.avp.api.Tuple;
import org.avp.common.item.AVPItems;

public class AVPFuelRegistry {

    private static final List<Tuple<GameObject<Item>, Integer>> ENTRIES = new ArrayList<>();

    public static List<Tuple<GameObject<Item>, Integer>> getEntries() {
        return ENTRIES;
    }

    private static void register(GameObject<Item> itemGameObject, int burnTime) {
        ENTRIES.add(new Tuple<>(itemGameObject, burnTime));
    }

    public static void forceInitialization() {
        register(AVPItems.INSTANCE.CARBON, 800);
    }

    private AVPFuelRegistry() {
        throw new UnsupportedOperationException();
    }
}
