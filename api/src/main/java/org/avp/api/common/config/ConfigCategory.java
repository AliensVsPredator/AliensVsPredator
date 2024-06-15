package org.avp.api.common.config;

import org.jetbrains.annotations.NotNull;

import static org.avp.api.common.config.ConfigConstants.CATEGORY_END;
import static org.avp.api.common.config.ConfigConstants.CATEGORY_START;
import static org.avp.api.common.config.ConfigConstants.NEW_LINE;

public record ConfigCategory(
    @NotNull Class<?> clazz,
    @NotNull Category category
) {

    public String getName() {
        return !category.value().isBlank() ? category().value() : clazz.getSimpleName();
    }

    public StringBuilder getCategory() {
        var name = getName();
        var border = "#".repeat(name.length() + 6);
        var stringBuilder = new StringBuilder();

        stringBuilder
            .append(border)
            .append(NEW_LINE);

        stringBuilder
            .append(CATEGORY_START)
            .append(" ")
            .append(name)
            .append(" ")
            .append(CATEGORY_END)
            .append(NEW_LINE);

        stringBuilder
            .append(border)
            .append(NEW_LINE)
            .append(NEW_LINE);

        return stringBuilder;
    }
}
