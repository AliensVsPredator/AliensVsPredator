package com.avp.core.common.config.template;

import com.avp.core.common.config.Config;

public record ConfigTemplateContext(
    Config config,
    int maxPropertyIdLength
) {}
