package com.avp.fabric.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Map;

public class AmmunitionIndicatorUtil {

    public enum DisplayState {
        LOW_AMMUNITION,
        NO_AMMUNITION,
        NOTHING
    }

    public static final Map<DisplayState, Component> DISPLAY_STATE_COMPONENT_MAP = net.minecraft.Util.make(
        new EnumMap<>(DisplayState.class),
        map -> {
            map.put(
                DisplayState.LOW_AMMUNITION,
                Component.translatable("display.avp.low_ammunition_warning").withStyle(ChatFormatting.YELLOW)
            );
            map.put(DisplayState.NO_AMMUNITION, Component.translatable("display.avp.no_ammunition_warning").withStyle(ChatFormatting.RED));
            map.put(DisplayState.NOTHING, Component.empty());
        }
    );
}
