package com.blib.api.common.property.v1;

import java.nio.file.Path;

public record BLibPropertyContainerType(
    BLibPropertyContainer container,
    Path filePath
) {}
