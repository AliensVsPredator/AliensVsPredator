package com.avp.core.common.config;

import com.avp.core.common.config.template.ConfigTemplate;

public record ConfigContainer(
    Config baseConfig,
    Config config,
    ConfigTemplate template
) {}
