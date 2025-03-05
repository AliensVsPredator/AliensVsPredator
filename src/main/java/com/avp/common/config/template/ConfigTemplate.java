package com.avp.common.config.template;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.avp.common.config.Config;
import com.avp.common.config.property.ConfigPropertyKey;

public record ConfigTemplate(
    List<Function<ConfigTemplateContext, Optional<String>>> lineGenerators
) {

    public List<String> generateLinesForConfig(Config config) {
        var maxLength = config.properties()
            .keys()
            .stream()
            .map(ConfigPropertyKey::identifier)
            .max(Comparator.comparingInt(String::length))
            .orElse("")
            .length();

        var context = new ConfigTemplateContext(config, maxLength);

        return lineGenerators.stream()
            .map(lineGenerator -> lineGenerator.apply(context))
            .flatMap(Optional::stream)
            .toList();
    }
}
