package com.human.common.gameplay.power;

import com.human.common.gameplay.power.grid.PowerGridManager;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class PowerSystem {

    private static final Map<ServerLevel, PowerGridManager> MANAGERS = new HashMap<>();

    public static PowerGridManager get(ServerLevel level) {
        return MANAGERS.computeIfAbsent(level, l -> new PowerGridManager());
    }
}
