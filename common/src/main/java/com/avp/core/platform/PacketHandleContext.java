package com.avp.core.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public sealed abstract class PacketHandleContext<T extends CustomPacketPayload> {

    private final T payload;

    private final Player player;

    protected PacketHandleContext(T payload, Player player) {
        this.payload = payload;
        this.player = player;
    }

    public T payload() {
        return payload;
    }

    public Player player() {
        return player;
    }

    public static final class Client<T extends CustomPacketPayload> extends PacketHandleContext<T> {

        private final Minecraft minecraft;

        public Client(T payload, Minecraft minecraft, LocalPlayer player) {
            super(payload, player);
            this.minecraft = minecraft;
        }

        public Minecraft getMinecraft() {
            return minecraft;
        }

        @Override
        public LocalPlayer player() {
            return (LocalPlayer) super.player();
        }
    }

    public static final class Server<T extends CustomPacketPayload> extends PacketHandleContext<T> {

        private final MinecraftServer server;

        public Server(T payload, ServerPlayer player, MinecraftServer server) {
            super(payload, player);
            this.server = server;
        }

        @Override
        public ServerPlayer player() {
            return (ServerPlayer) super.player();
        }

        public MinecraftServer server() {
            return server;
        }
    }
}
