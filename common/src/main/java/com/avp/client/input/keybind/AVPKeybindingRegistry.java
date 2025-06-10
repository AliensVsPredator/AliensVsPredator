package com.avp.client.input.keybind;

import com.bvanseg.just.functional.tuple.Tuple2;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.client.model.KeyInteractType;
import com.avp.common.model.Crawler;
import com.avp.common.network.packet.C2SGunReloadPayload;
import com.avp.common.network.packet.C2SPlayerToggleCrawlPayload;
import com.avp.service.Services;

public class AVPKeybindingRegistry {

    public static final Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> CRAWL = register(
        "crawl",
        "movement",
        GLFW.GLFW_KEY_LEFT_ALT,
        keyMapping -> {
            var player = Minecraft.getInstance().player;

            if (player != null) {
                var crawler = (Crawler) player;
                var shouldCrawl = keyMapping == KeyInteractType.PRESS;
                crawler.setCrawling(shouldCrawl);
                Services.CLIENT_NETWORKING.sendToServer(new C2SPlayerToggleCrawlPayload(shouldCrawl));
            }
        }
    );

    public static final Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> RELOAD = register(
        "reload",
        "weapons",
        GLFW.GLFW_KEY_R,
        keyMapping -> {
            var player = Minecraft.getInstance().player;

            if (player != null) {
                Services.CLIENT_NETWORKING.sendToServer(C2SGunReloadPayload.INSTANCE);
            }
        }
    );

    private static Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> register(
        String id,
        String category,
        int key,
        Consumer<KeyInteractType> onKeyMappingActivated
    ) {
        return Services.CLIENT_REGISTRY.registerKeyMapping(id, category, key, onKeyMappingActivated);
    }

    public static void initialize() {}
}
