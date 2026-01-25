/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package com.blib.azurelib.common.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Container class for UV face information, only used in deserialization at startup
 */
public record UVFaces(
    @Nullable com.blib.azurelib.common.loading.json.raw.FaceUV north,
    @Nullable com.blib.azurelib.common.loading.json.raw.FaceUV south,
    @Nullable com.blib.azurelib.common.loading.json.raw.FaceUV east,
    @Nullable com.blib.azurelib.common.loading.json.raw.FaceUV west,
    @Nullable com.blib.azurelib.common.loading.json.raw.FaceUV up,
    @Nullable com.blib.azurelib.common.loading.json.raw.FaceUV down
) {

    public static JsonDeserializer<UVFaces> deserializer() {
        return (json, type, context) -> {
            JsonObject obj = json.getAsJsonObject();
            com.blib.azurelib.common.loading.json.raw.FaceUV north = GsonHelper.getAsObject(
                obj,
                "north",
                null,
                context,
                com.blib.azurelib.common.loading.json.raw.FaceUV.class
            );
            com.blib.azurelib.common.loading.json.raw.FaceUV south = GsonHelper.getAsObject(
                obj,
                "south",
                null,
                context,
                com.blib.azurelib.common.loading.json.raw.FaceUV.class
            );
            com.blib.azurelib.common.loading.json.raw.FaceUV east = GsonHelper.getAsObject(
                obj,
                "east",
                null,
                context,
                com.blib.azurelib.common.loading.json.raw.FaceUV.class
            );
            com.blib.azurelib.common.loading.json.raw.FaceUV west = GsonHelper.getAsObject(
                obj,
                "west",
                null,
                context,
                com.blib.azurelib.common.loading.json.raw.FaceUV.class
            );
            com.blib.azurelib.common.loading.json.raw.FaceUV up = GsonHelper.getAsObject(
                obj,
                "up",
                null,
                context,
                com.blib.azurelib.common.loading.json.raw.FaceUV.class
            );
            com.blib.azurelib.common.loading.json.raw.FaceUV down = GsonHelper.getAsObject(
                obj,
                "down",
                null,
                context,
                com.blib.azurelib.common.loading.json.raw.FaceUV.class
            );

            return new UVFaces(north, south, east, west, up, down);
        };
    }

    public FaceUV fromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> north;
            case SOUTH -> south;
            case EAST -> east;
            case WEST -> west;
            case UP -> up;
            case DOWN -> down;
        };
    }
}
