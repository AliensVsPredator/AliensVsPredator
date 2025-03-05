package com.avp.common.config.template;

import com.avp.common.config.Config;

public record ConfigTemplateContext(
    Config config,
    int maxPropertyIdLength
) {}
