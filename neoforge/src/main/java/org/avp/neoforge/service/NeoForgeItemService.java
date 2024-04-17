package org.avp.neoforge.service;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.AVPConstants;
import org.avp.common.service.ItemService;
import org.avp.neoforge.util.NeoForgeHolder;

public class NeoForgeItemService implements ItemService {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(AVPConstants.MOD_ID);

    @Override
    public Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return new NeoForgeHolder<>(ITEMS, registryName, supplier);
    }

    @Override
    public void register(Holder<Item> holder) { /* NO-OP FOR FORGE */ }
}
