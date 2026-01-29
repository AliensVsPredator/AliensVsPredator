package com.blib.azurelib.common.loading;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;

import com.blib.azurelib.AzureLib;
import com.blib.azurelib.common.animation.primitive.AzBakedAnimations;
import com.blib.azurelib.common.loading.json.raw.Model;
import com.blib.azurelib.common.util.AzureLibException;
import com.blib.azurelib.common.util.JsonUtil;

public final class FileLoader {

    private FileLoader() {
        throw new UnsupportedOperationException();
    }

    public static AzBakedAnimations loadAzAnimationsFile(ResourceLocation location, ResourceManager manager) {
        try {
            return JsonUtil.GEO_GSON.fromJson(loadFile(location, manager), AzBakedAnimations.class);
        } catch (Exception e) {
            logError(location);
            return null;
        }
    }

    public static Model loadModelFile(ResourceLocation location, ResourceManager manager) {
        try {
            return JsonUtil.GEO_GSON.fromJson(loadFile(location, manager), Model.class);
        } catch (Exception e) {
            logError(location);
            return null;
        }
    }

    public static JsonObject loadFile(ResourceLocation location, ResourceManager manager) {
        try {
            return GsonHelper.fromJson(JsonUtil.GEO_GSON, getFileContents(location, manager), JsonObject.class);
        } catch (Exception e) {
            logError(location);
            return null;
        }
    }

    public static String getFileContents(ResourceLocation location, ResourceManager manager) {
        try (InputStream inputStream = manager.getResourceOrThrow(location).open()) {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (Exception e) {
            AzureLib.LOGGER.error("Couldn't load {}", location, e);

            throw new AzureLibException(location.toString());
        }
    }

    private static void logError(Object args) {
        AzureLib.LOGGER.warn("Error parsing JSON from {}: skipping", args);
    }
}
