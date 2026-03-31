package com.blib.internal.client.territory.compat;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.BLibAPI;

@ApiStatus.Internal
public class XaeroWorldMapCompat {

    private static boolean isLoaded = false;

    public static void init() {
        if (BLibAPI.isModLoaded("xaeroworldmap")) {
            isLoaded = true;
        }
    }

    public static boolean isLoaded() {
        return isLoaded;
    }

    private XaeroWorldMapCompat() {}
}
