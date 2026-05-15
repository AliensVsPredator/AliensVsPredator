package com.blib.engine.texture;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public enum TextureTool {

    SELECT("Select"),
    PENCIL("Pencil"),
    BUCKET("Bucket");

    private final String displayName;

    TextureTool(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
