package org.avp.common.registry;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

/**
 * @author Boston Vanseghi
 */
public class AVPItemBindingRegistry extends AVPBindingRegistry<Item> {

    @Override
    protected GameObject<Item> registerGameObject(String name, Supplier<Item> supplier) {
        return Services.ITEM_REGISTRY.register(name, supplier);
    }
}
