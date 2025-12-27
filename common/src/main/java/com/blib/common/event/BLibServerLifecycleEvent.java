package com.blib.common.event;

import net.minecraft.server.MinecraftServer;

public final class BLibServerLifecycleEvent {

    public interface Started {

        void invoke(MinecraftServer minecraftServer);
    }

    public interface Starting {

        void invoke(MinecraftServer minecraftServer);
    }

    public interface Stopped {

        void invoke(MinecraftServer minecraftServer);
    }

    public interface Stopping {

        void invoke(MinecraftServer minecraftServer);
    }
}
