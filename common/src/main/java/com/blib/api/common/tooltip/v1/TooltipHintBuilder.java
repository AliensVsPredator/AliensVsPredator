package com.blib.api.common.tooltip.v1;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TooltipHintBuilder {

    private final List<Component> components;

    public TooltipHintBuilder() {
        this.components = new ArrayList<>();
    }

    public TooltipHintBuilder addCategory(TooltipCategoryType tooltipCategoryType) {
        components.add(
            Component.translatable(tooltipCategoryType.getTranslationKey())
                .append(Component.literal(":"))
                .withStyle(ChatFormatting.YELLOW)
        );
        return this;
    }

    public TooltipHintBuilder addPositiveEffect(String translationKey) {
        components.add(
            Component.literal("+ ")
                .append(Component.translatable(translationKey))
                .withStyle(ChatFormatting.GREEN)
        );
        return this;
    }

    public TooltipHintBuilder addNegativeEffect(String translationKey) {
        components.add(
            Component.literal("- ")
                .append(Component.translatable(translationKey))
                .withStyle(ChatFormatting.RED)
        );
        return this;
    }

    public List<Component> build() {
        return components;
    }
}
