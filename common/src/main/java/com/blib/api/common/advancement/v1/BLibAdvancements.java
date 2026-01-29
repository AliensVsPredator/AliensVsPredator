package com.blib.api.common.advancement.v1;

import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;

public final class BLibAdvancements {

    public static BLibAdvancement create(String modId, String group, String path) {
        return new BLibAdvancement(modId, group, path);
    }

    public static void grantAll(ServerPlayer serverPlayer, BLibAdvancement... advancements) {
        grantAll(serverPlayer, Arrays.asList(advancements));
    }

    public static void grantAll(ServerPlayer serverPlayer, Collection<BLibAdvancement> advancements) {
        for (var advancement : advancements) {
            advancement.grant(serverPlayer);
        }
    }

    public static void revokeAll(ServerPlayer serverPlayer, BLibAdvancement... advancements) {
        revokeAll(serverPlayer, Arrays.asList(advancements));
    }

    public static void revokeAll(ServerPlayer serverPlayer, Collection<BLibAdvancement> advancements) {
        for (var advancement : advancements) {
            advancement.revoke(serverPlayer);
        }
    }

    public static boolean hasAll(ServerPlayer serverPlayer, BLibAdvancement... advancements) {
        return hasAll(serverPlayer, Arrays.asList(advancements));
    }

    public static boolean hasAll(ServerPlayer serverPlayer, Collection<BLibAdvancement> advancements) {
        for (var advancement : advancements) {
            if (!advancement.isGranted(serverPlayer)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasAny(ServerPlayer serverPlayer, BLibAdvancement... advancements) {
        return hasAny(serverPlayer, Arrays.asList(advancements));
    }

    public static boolean hasAny(ServerPlayer serverPlayer, Collection<BLibAdvancement> advancements) {
        for (var advancement : advancements) {
            if (advancement.isGranted(serverPlayer)) {
                return true;
            }
        }

        return false;
    }

    private BLibAdvancements() {}
}
