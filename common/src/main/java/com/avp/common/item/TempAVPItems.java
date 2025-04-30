package com.avp.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import com.avp.service.Services;

// TODO: Rename this once multi-loader migration is finished.
public class TempAVPItems {

    public static final Supplier<Item> RAW_SILICA = register("raw_silica");

    private static Supplier<Item> register(String name) {
        return Services.REGISTRY.register(BuiltInRegistries.ITEM, name, () -> new Item(new Item.Properties()));
    }

    public static void initialize() {}
}
