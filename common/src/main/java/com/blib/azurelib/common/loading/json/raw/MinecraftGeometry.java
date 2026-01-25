/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package com.blib.azurelib.common.loading.json.raw;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import com.blib.azurelib.common.util.JsonUtil;

/**
 * Container class for generic geometry information, only used in deserialization at startup
 */
public record MinecraftGeometry(
    com.blib.azurelib.common.loading.json.raw.Bone[] bones,
    @Nullable String cape,
    @Nullable com.blib.azurelib.common.loading.json.raw.ModelProperties modelProperties
) {

    public static JsonDeserializer<MinecraftGeometry> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject obj = json.getAsJsonObject();
            com.blib.azurelib.common.loading.json.raw.Bone[] bones = JsonUtil.jsonArrayToObjectArray(
                GsonHelper.getAsJsonArray(obj, "bones", new JsonArray(0)),
                context,
                Bone.class
            );
            String cape = GsonHelper.getAsString(obj, "cape", null);
            com.blib.azurelib.common.loading.json.raw.ModelProperties modelProperties = GsonHelper.getAsObject(
                obj,
                "description",
                null,
                context,
                ModelProperties.class
            );

            return new MinecraftGeometry(bones, cape, modelProperties);
        };
    }
}
