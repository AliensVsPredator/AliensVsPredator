package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;
import org.avp.common.registry.AVPItemBindingRegistry;

public class AVPSpawnEggItems extends AVPItemBindingRegistry {

    public static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    private AVPSpawnEggItems() {}
}
