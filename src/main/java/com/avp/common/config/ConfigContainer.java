package com.avp.common.config;

import com.avp.common.config.template.ConfigTemplate;

public record ConfigContainer(
    Config baseConfig,
    Config config,
    ConfigTemplate template
) {}
