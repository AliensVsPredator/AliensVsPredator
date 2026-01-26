package com.blib.api.common.property.v1;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class BLibPropertySchema {

    public static Builder builder() {
        return new Builder();
    }

    private final boolean alignPropertyValues;

    private final List<Line> lines;

    private final Map<String, Line.Property<?>> pathToPropertyMap;

    private BLibPropertySchema(boolean alignPropertyValues, List<Line> lines) {
        this.alignPropertyValues = alignPropertyValues;
        this.lines = Collections.unmodifiableList(lines);
        this.pathToPropertyMap = lines.stream()
            .filter(line -> line instanceof Line.Property<?>)
            .map(line -> {
                var property = (Line.Property<?>) line;
                return Map.entry(property.leaf().path(), property);
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean alignPropertyValues() {
        return alignPropertyValues;
    }

    public List<Line> getLines() {
        return lines;
    }

    @SuppressWarnings("unchecked")
    public <T> Line.@Nullable Property<T> getPropertyByPathOrNull(String path) {
        return (Line.Property<T>) pathToPropertyMap.get(path);
    }

    public static class Builder {

        private final List<Line> lines;

        private boolean alignPropertyValues;

        private Builder() {
            this.lines = new ArrayList<>();
            this.alignPropertyValues = true;
        }

        public Builder addBlankLine() {
            lines.add(Line.BLANK);
            return this;
        }

        public Builder addComment(String comment) {
            lines.add(new Line.Comment(comment));
            return this;
        }

        public <T> Builder addProperty(BLibPropertyKey.Leaf<T> propertyKey, T defaultValue) {
            lines.add(new Line.Property<>(propertyKey, defaultValue));
            return this;
        }

        public Builder withPropertyValueAlignment(boolean alignPropertyValues) {
            this.alignPropertyValues = alignPropertyValues;
            return this;
        }

        public Builder apply(UnaryOperator<Builder> unaryOperator) {
            return unaryOperator.apply(this);
        }

        public BLibPropertySchema build() {
            return new BLibPropertySchema(alignPropertyValues, lines);
        }
    }

    public sealed interface Line {

        Blank BLANK = Blank.INSTANCE;

        enum Blank implements Line {
            INSTANCE;
        }

        record Comment(String text) implements Line {}

        record Property<T>(
            BLibPropertyKey.Leaf<T> leaf,
            T defaultValue
        ) implements Line {}
    }
}
