package com.avp.fabric.client;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.client.AVPClient;
import com.avp.fabric.client.input.keybind.AVPKeybindingRegistry;
import com.avp.fabric.client.network.AVPClientPacketHandlerRegistry;

public class AVPFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AVPClient.initialize();

        // Items

        // Keybindings
        AVPKeybindingRegistry.initialize();

        // Networking
        AVPClientPacketHandlerRegistry.initialize();
    }

    private void registerItemRenderer(Item item, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        var path = BuiltInRegistries.ITEM.getKey(item).getPath();
        var itemRendererSupplier = rendererFactory.apply(path);
        AzItemRendererRegistry.register(itemRendererSupplier, item);
    }
}
