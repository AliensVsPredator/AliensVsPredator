package org.avp.neoforge.service;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.AVPConstants;
import org.avp.api.service.ItemService;
import org.avp.neoforge.util.NeoForgeHolder;

public class NeoForgeItemService implements ItemService {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(AVPConstants.MOD_ID);

    @Override
    public BLHolder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return new NeoForgeHolder<>(ITEMS, registryName, supplier);
    }

    @Override
    public void register(BLHolder<Item> holder) { /* NO-OP FOR FORGE */ }
}
