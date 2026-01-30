package com.blib.internal.client.animation.easing;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

public class AzEasingTypeLoader {

    public static AzEasingType fromJson(JsonElement json) {
        if (!(json instanceof JsonPrimitive primitive) || !primitive.isString())
            return AzEasingTypes.LINEAR;

        return fromString(primitive.getAsString().toLowerCase(Locale.ROOT));
    }

    public static AzEasingType fromString(String name) {
        return AzEasingTypeRegistry.getOrDefault(name, AzEasingTypes.LINEAR);
    }
}
