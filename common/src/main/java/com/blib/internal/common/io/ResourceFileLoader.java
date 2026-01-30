package com.blib.internal.common.io;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.just.core.functional.option.Option;
import com.just.core.functional.result.Result;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ResourceFileLoader {

    public static <T> Result<T, ObjectLoadError> loadObjectFromFile(
        Gson gson,
        Class<T> clazz,
        ResourceLocation location,
        ResourceManager manager
    ) {
        var loadJsonObjectResult = loadJsonFromFile(gson, location, manager);

        try {
            return loadJsonObjectResult.map(jsonObject -> gson.fromJson(jsonObject, clazz))
                .mapErr(ObjectLoadError.JsonLoadFailure::new);
        } catch (JsonSyntaxException exception) {
            return Result.err(new ObjectLoadError.JsonSyntaxFailure(exception));
        }
    }

    public static Result<JsonObject, JsonLoadError> loadJsonFromFile(Gson gson, ResourceLocation location, ResourceManager manager) {
        var fileContentsResult = loadStringContents(location, manager);

        try {
            return fileContentsResult.map(fileContents -> gson.fromJson(fileContents, JsonObject.class))
                .mapErr(JsonLoadError.FileContentLoadFailure::new);
        } catch (JsonSyntaxException exception) {
            return Result.err(new JsonLoadError.SyntaxFailure(exception));
        }
    }

    public static Result<String, FileContentLoadError> loadStringContents(ResourceLocation location, ResourceManager manager) {
        try (var inputStream = manager.getResourceOrThrow(location).open()) {
            return Result.ok(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            return Result.err(new FileContentLoadError.FileNotFound(e));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ResourceFileLoader() {
        throw new UnsupportedOperationException();
    }

    public sealed interface ObjectLoadError extends LoadError {

        record JsonLoadFailure(JsonLoadError error) implements ObjectLoadError {

            @Override
            public String getMessage() {
                return error.getMessage();
            }

            @Override
            public Option<Throwable> getCause() {
                return error.getCause();
            }

            @Override
            public @NotNull String toString() {
                return getMessage();
            }
        }

        record JsonSyntaxFailure(JsonSyntaxException exception) implements ObjectLoadError {

            @Override
            public String getMessage() {
                return "The JSON structure does not match the expected type.";
            }

            @Override
            public Option<Throwable> getCause() {
                return Option.some(exception);
            }

            @Override
            public @NotNull String toString() {
                return getMessage();
            }
        }
    }

    public sealed interface JsonLoadError extends LoadError {

        record FileContentLoadFailure(FileContentLoadError error) implements JsonLoadError {

            @Override
            public String getMessage() {
                return error.getMessage();
            }

            @Override
            public Option<Throwable> getCause() {
                return error.getCause();
            }

            @Override
            public @NotNull String toString() {
                return getMessage();
            }
        }

        record SyntaxFailure(JsonSyntaxException exception) implements JsonLoadError {

            @Override
            public String getMessage() {
                return "The file content does not contain valid JSON.";
            }

            @Override
            public Option<Throwable> getCause() {
                return Option.some(exception);
            }

            @Override
            public @NotNull String toString() {
                return getMessage();
            }
        }
    }

    public sealed interface FileContentLoadError extends LoadError {

        record FileNotFound(FileNotFoundException exception) implements FileContentLoadError {

            @Override
            public String getMessage() {
                return "The file does not exist.";
            }

            @Override
            public Option<Throwable> getCause() {
                return Option.some(exception);
            }

            @Override
            public @NotNull String toString() {
                return getMessage();
            }
        }
    }

    public interface LoadError {

        String getMessage();

        Option<Throwable> getCause();

        default void log(Logger logger, Level level, String baseMessage, String postMessage, Object... arguments) {
            var builder = logger.atLevel(level);

            for (var argument : arguments) {
                builder = builder.addArgument(argument);
            }

            var causeOrNull = getCause().unwrapOr(null);

            if (causeOrNull != null) {
                builder = builder.setCause(causeOrNull);
            }

            builder.log(baseMessage + " " + getMessage() + " " + postMessage);
        }
    }
}
