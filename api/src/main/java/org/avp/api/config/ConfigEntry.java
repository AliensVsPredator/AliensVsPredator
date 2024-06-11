package org.avp.api.config;

public record ConfigEntry(
    String name,
    String value,
    String category,
    String comment,
    String internalComment
) {}
