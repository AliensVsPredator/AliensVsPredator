package org.avp.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TooltipUtils {

    public static void appendLabel(@NotNull List<Component> components, String label, MutableComponent component) {
        components.add(
            Component.translatable(label).withStyle(ChatFormatting.DARK_GREEN)
                .append(component.withStyle(ChatFormatting.GRAY))
        );
    }


    private TooltipUtils() {
        throw new UnsupportedOperationException();
    }
}
