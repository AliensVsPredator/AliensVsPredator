package com.blib.api.common.property.v1;

import com.just.core.functional.result.Result;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BLibProperties {

    public static Result<BLibPropertyContainer, LoadError> load(Path path, BLibPropertySchema propertySchema) {
        var properties = new HashMap<String, BLibPropertyValue<?>>();

        if (!Files.exists(path)) {
            return Result.err(LoadError.DOES_NOT_EXIST);
        }

        List<@NotNull String> fileLines;

        try {
            fileLines = Files.readAllLines(path);
        } catch (IOException e) {
            return Result.err(new LoadError.ReadFailure(e));
        }

        for (var line : fileLines) {
            var parsed = BLibPropertyLine.parse(line);

            if (parsed instanceof BLibPropertyLine.Property(var key, var rawValue)) {
                properties.put(key, new BLibPropertyValue.Serialized<>(rawValue));
            }
        }

        return Result.ok(new BLibPropertyContainer(properties, propertySchema));
    }

    public static Result<Void, SaveError> save(Path path, BLibPropertyContainer propertyContainer, BLibPropertySchema propertySchema) {
        var writtenLines = new ArrayList<String>();

        var propertyPathToValueMap = propertyContainer.getPathToValueMap();
        var alignPropertyValues = propertySchema.alignPropertyValues();

        var maxPropertyPathLength = 0;

        if (alignPropertyValues) {
            for (var line : propertySchema.getLines()) {
                if (line instanceof BLibPropertySchema.Line.Property<?> property) {
                    maxPropertyPathLength = Math.max(maxPropertyPathLength, property.leaf().path().length());
                }
            }
        }

        for (var line : propertySchema.getLines()) {
            switch (line) {
                case BLibPropertySchema.Line.Blank blank -> writtenLines.add("");
                case BLibPropertySchema.Line.Comment comment -> writtenLines.add("# " + comment.text());
                case BLibPropertySchema.Line.Property<?> property -> {
                    var propertyPath = property.leaf().path();
                    var propertyValueOrNull = propertyPathToValueMap.get(propertyPath);
                    var padding = alignPropertyValues
                        ? " ".repeat(maxPropertyPathLength - propertyPath.length())
                        : "";

                    if (propertyValueOrNull == null) {
                        writtenLines.add(propertyPath + padding + "= " + property.defaultValue());
                        continue;
                    }

                    var rawPropertyValue = switch (propertyValueOrNull) {
                        case BLibPropertyValue.Deserialized<?> propertyValue -> {
                            @SuppressWarnings("unchecked")
                            var castedProperty = (BLibPropertySchema.Line.Property<Object>) property;
                            @SuppressWarnings("unchecked")
                            var castedPropertyValue = (BLibPropertyValue.Deserialized<Object>) propertyValue;

                            yield castedPropertyValue.serialized(castedProperty.leaf().serializer());
                        }
                        case BLibPropertyValue.Serialized<?> propertyValue -> propertyValue.value();
                    };

                    writtenLines.add(propertyPath + padding + "= " + rawPropertyValue);
                }
            }
        }

        var parent = path.getParent();

        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                return Result.err(new SaveError.CreateDirectoriesFailure(e, parent));
            }
        }

        try {
            Files.write(path, writtenLines);
        } catch (IOException e) {
            return Result.err(new SaveError.WriteFailure(e, writtenLines));
        }

        return Result.ok(null);
    }

    public sealed interface LoadError {

        DoesNotExist DOES_NOT_EXIST = DoesNotExist.INSTANCE;

        enum DoesNotExist implements LoadError {
            INSTANCE
        }

        record ReadFailure(IOException exception) implements LoadError {}
    }

    public sealed interface SaveError {

        record CreateDirectoriesFailure(
            IOException exception,
            Path parent
        ) implements SaveError {}

        record WriteFailure(
            IOException exception,
            List<String> writtenLines
        ) implements SaveError {}

    }
}
