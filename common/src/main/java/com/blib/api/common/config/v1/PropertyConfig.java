package com.blib.api.common.config.v1;

import com.just.core.functional.option.Option;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A configuration system based on properties files. Supports hierarchical property access, type-safe parsing, and
 * preserves comments and order. Values are stored in a tree structure for O(1) subtree operations and deserialized
 * lazily on access with caching.
 */
public class PropertyConfig {

    private final Path filePath;

    private final List<PropertyLine> lines;

    private final PropertyNode root;

    private PropertyConfig(Path filePath, List<PropertyLine> lines, PropertyNode root) {
        this.filePath = filePath;
        this.lines = lines;
        this.root = root;
    }

    /**
     * Loads a PropertyConfig from the given file path. If the file does not exist, creates an empty config.
     */
    public static PropertyConfig load(Path path) throws IOException {
        var lines = new ArrayList<PropertyLine>();
        var root = new PropertyNode();

        if (Files.exists(path)) {
            var fileLines = Files.readAllLines(path);

            for (var line : fileLines) {
                var parsed = PropertyLine.parse(line);
                lines.add(parsed);

                if (parsed instanceof PropertyLine.Property(var key, var rawValue)) {
                    // Parse the dot-separated path and insert into tree
                    var segments = key.split("\\.");
                    var node = root;

                    for (var segment : segments) {
                        node = node.getOrCreateChild(segment);
                    }

                    node.setRawValue(rawValue);
                }
            }
        }

        return new PropertyConfig(path, lines, root);
    }

    /**
     * Creates an empty PropertyConfig for the given file path.
     */
    public static PropertyConfig create(Path path) {
        return new PropertyConfig(path, new ArrayList<>(), new PropertyNode());
    }

    /**
     * Saves the config back to the file, preserving comments and order. New properties are appended to the end.
     * Property lines are formatted with aligned equal signs based on the longest key.
     */
    public void save() throws IOException {
        // First, collect all keys that will be written to find max length
        var allKeys = new ArrayList<String>();

        for (var line : lines) {
            if (line instanceof PropertyLine.Property prop) {
                var node = getNodeByPath(prop.key());

                if (node != null && node.hasValue()) {
                    allKeys.add(prop.key());
                }
            }
        }

        // Add new keys not in original file
        var existingKeys = new LinkedHashSet<>(allKeys);

        collectAllPaths(root, "", (path, value) -> {
            if (!existingKeys.contains(path)) {
                allKeys.add(path);
            }
        });

        // Find the longest key for alignment
        var maxKeyLength = allKeys.stream()
            .mapToInt(String::length)
            .max()
            .orElse(0);

        // Build the formatted output lines
        var written = new LinkedHashSet<String>();
        var outputLines = new ArrayList<String>();

        for (var line : lines) {
            if (line instanceof PropertyLine.Property prop) {
                var node = getNodeByPath(prop.key());

                if (node != null && node.hasValue()) {
                    outputLines.add(formatProperty(prop.key(), node.getRawValue(), maxKeyLength));
                    written.add(prop.key());
                }
            } else {
                outputLines.add(line.toFileLine());
            }
        }

        // Append new properties that weren't in the original file
        collectAllPaths(root, "", (path, value) -> {
            if (!written.contains(path)) {
                outputLines.add(formatProperty(path, value, maxKeyLength));
            }
        });

        var parent = filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.write(filePath, outputLines);
    }

    /**
     * Formats a property line with padding for aligned equal signs.
     */
    private String formatProperty(String key, String value, int maxKeyLength) {
        var padding = " ".repeat(maxKeyLength - key.length());
        return key + padding + " = " + value;
    }

    /**
     * Collects all paths with values from the tree.
     */
    private void collectAllPaths(PropertyNode node, String prefix, PathValueConsumer consumer) {
        if (node.hasValue()) {
            consumer.accept(prefix, node.getRawValue());
        }

        for (var entry : node.getChildren().entrySet()) {
            var childPath = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            collectAllPaths(entry.getValue(), childPath, consumer);
        }
    }

    @FunctionalInterface
    private interface PathValueConsumer {

        void accept(String path, String value);
    }

    // ========== Typed Accessor Methods Using PropertyKey ==========

    /**
     * Gets a typed property value as an Option. Values are deserialized lazily and cached.
     *
     * @param key The property key (must be a Leaf)
     * @param <T> The value type
     * @return An Option containing the value, or none if not found or wrong type
     */
    @SuppressWarnings("unchecked")
    public <T> Option<T> get(PropertyKey.Leaf<T> key) {
        var node = getNode(key);

        if (node == null || !node.hasValue()) {
            return Option.none();
        }

        // Check cache first
        var cached = node.getCachedValue();

        if (cached != null) {
            return Option.some((T) cached);
        }

        // Deserialize and cache
        var deserialized = key.serializer().deserialize(node.getRawValue());

        if (deserialized.isSome()) {
            node.setCachedValue(deserialized.unwrap());
        }

        return deserialized;
    }

    /**
     * Gets a typed property value or null if not found or wrong type.
     *
     * @param key The property key (must be a Leaf)
     * @param <T> The value type
     * @return The value or null
     */
    public <T> @Nullable T getOrNull(PropertyKey.Leaf<T> key) {
        var opt = get(key);
        return opt.isSome() ? opt.unwrap() : null;
    }

    /**
     * Gets a typed property value or the default if not found or wrong type.
     *
     * @param key          The property key (must be a Leaf)
     * @param defaultValue The default value to return if not found
     * @param <T>          The value type
     * @return The value or the default
     */
    public <T> T getOrDefault(PropertyKey.Leaf<T> key, T defaultValue) {
        return get(key).unwrapOr(defaultValue);
    }

    /**
     * Gets a typed property value or throws if not found or wrong type.
     *
     * @param key The property key (must be a Leaf)
     * @param <T> The value type
     * @return The value
     * @throws NoSuchElementException if the property is not found or has wrong type
     */
    public <T> T getOrThrow(PropertyKey.Leaf<T> key) {
        var opt = get(key);

        if (opt.isNone()) {
            throw new NoSuchElementException("Property not found or wrong type: " + key.path());
        }

        return opt.unwrap();
    }

    // ========== Setter Methods Using PropertyKey ==========

    /**
     * Sets a typed property value.
     *
     * @param key   The property key (must be a Leaf)
     * @param value The value to set
     * @param <T>   The value type
     */
    public <T> void set(PropertyKey.Leaf<T> key, T value) {
        var node = getOrCreateNode(key);
        var serialized = key.serializer().serialize(value);

        node.setRawValue(serialized);
        node.setCachedValue(value);
    }

    // ========== Raw Access Methods (for advanced use) ==========

    /**
     * Gets a raw property value as an Option by dot-separated path.
     */
    public Option<String> getRaw(String path) {
        var node = getNodeByPath(path);

        if (node == null || !node.hasValue()) {
            return Option.none();
        }

        return Option.ofNullable(node.getRawValue());
    }

    /**
     * Gets a raw property value or null by dot-separated path.
     */
    public @Nullable String getRawOrNull(String path) {
        var node = getNodeByPath(path);

        if (node == null || !node.hasValue()) {
            return null;
        }

        return node.getRawValue();
    }

    /**
     * Sets a raw property value by dot-separated path. Clears the cache for this path.
     */
    public void setRaw(String path, String value) {
        var node = getOrCreateNodeByPath(path);

        node.setRawValue(value);
        node.clearCache();
    }

    // ========== Hierarchical Access ==========

    /**
     * Gets all direct child keys under a parent key.
     *
     * @param parent The parent key
     * @return A set of child key names
     */
    public Set<String> getChildKeys(PropertyKey.Parent parent) {
        var node = getNode(parent);

        if (node == null) {
            return Set.of();
        }

        return new LinkedHashSet<>(node.getChildren().keySet());
    }

    /**
     * Gets all direct children with values under a parent key as raw strings.
     *
     * @param parent The parent key
     * @return A map of child names to raw values (only includes children with values)
     */
    public Map<String, String> getChildren(PropertyKey.Parent parent) {
        var node = getNode(parent);

        if (node == null) {
            return Map.of();
        }

        var result = new LinkedHashMap<String, String>();

        for (var entry : node.getChildren().entrySet()) {
            var child = entry.getValue();

            if (child.hasValue()) {
                result.put(entry.getKey(), child.getRawValue());
            }
        }

        return result;
    }

    // ========== Removal Methods ==========

    /**
     * Removes a leaf property. O(1) operation.
     *
     * @param key The leaf key to remove
     */
    public void remove(PropertyKey.Leaf<?> key) {
        var parentNode = key.parent() != null ? getNode(key.parent()) : root;

        if (parentNode != null) {
            parentNode.removeChild(key.name());
            removeLinesByPath(key.path());
        }
    }

    /**
     * Removes a parent key and all its descendants. O(1) for the tree operation.
     *
     * @param key The parent key to remove
     */
    public void remove(PropertyKey.Parent key) {
        var parentNode = key.parent() != null ? getNode(key.parent()) : root;

        if (parentNode != null) {
            parentNode.removeChild(key.name());
            removeLinesByPrefix(key.path());
        }
    }

    /**
     * Removes a property by raw dot-separated path.
     */
    public void removeRaw(String path) {
        var segments = path.split("\\.");

        if (segments.length == 0) {
            return;
        }

        if (segments.length == 1) {
            root.removeChild(segments[0]);
        } else {
            // Find parent node
            var node = root;

            for (int i = 0; i < segments.length - 1; i++) {
                node = node.getChild(segments[i]);

                if (node == null) {
                    return;
                }
            }

            node.removeChild(segments[segments.length - 1]);
        }

        removeLinesByPath(path);
    }

    /**
     * Removes lines from the line list by exact path match.
     */
    private void removeLinesByPath(String path) {
        lines.removeIf(line -> line instanceof PropertyLine.Property prop && prop.key().equals(path));
    }

    /**
     * Removes lines from the line list by path prefix (for subtree removal).
     */
    private void removeLinesByPrefix(String prefix) {
        var searchPrefix = prefix + ".";

        lines.removeIf(
            line -> line instanceof PropertyLine.Property prop &&
                (prop.key().equals(prefix) || prop.key().startsWith(searchPrefix))
        );
    }

    // ========== Utility Methods ==========

    /**
     * Checks if a leaf property exists and has a valid value.
     */
    public boolean contains(PropertyKey.Leaf<?> key) {
        var node = getNode(key);
        return node != null && node.hasValue();
    }

    /**
     * Checks if a parent key exists (has children or a value).
     */
    public boolean contains(PropertyKey.Parent key) {
        var node = getNode(key);
        return node != null && !node.isEmpty();
    }

    /**
     * Checks if a raw path exists and has a valid value.
     */
    public boolean containsRaw(String path) {
        var node = getNodeByPath(path);
        return node != null && node.hasValue();
    }

    /**
     * Returns all leaf property paths that have values.
     */
    public Set<String> keys() {
        var result = new LinkedHashSet<String>();

        collectAllPaths(root, "", (path, value) -> result.add(path));

        return result;
    }

    /**
     * Returns the file path this config is associated with.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Adds a comment line to the config. The comment will be appended after all existing lines.
     */
    public void addComment(String comment) {
        var line = comment.startsWith("#") ? comment : "# " + comment;
        lines.add(new PropertyLine.Comment(line));
    }

    /**
     * Adds a blank line to the config.
     */
    public void addBlankLine() {
        lines.add(PropertyLine.Blank.INSTANCE);
    }

    /**
     * Clears all cached deserialized values in the tree.
     */
    public void clearCache() {
        clearCacheRecursive(root);
    }

    private void clearCacheRecursive(PropertyNode node) {
        node.clearCache();

        for (var child : node.getChildren().values()) {
            clearCacheRecursive(child);
        }
    }

    // ========== Internal Tree Navigation ==========

    /**
     * Gets the path segments for a property key by walking up the parent chain.
     */
    private String[] getPathSegments(PropertyKey key) {
        var segments = new ArrayList<String>();
        PropertyKey current = key;

        while (current != null) {
            segments.addFirst(current.name());
            current = current.parent();
        }

        return segments.toArray(String[]::new);
    }

    /**
     * Gets the node for a property key, or null if it doesn't exist.
     */
    private @Nullable PropertyNode getNode(PropertyKey key) {
        var segments = getPathSegments(key);
        var node = root;

        for (var segment : segments) {
            node = node.getChild(segment);

            if (node == null) {
                return null;
            }
        }

        return node;
    }

    /**
     * Gets or creates the node for a property key.
     */
    private PropertyNode getOrCreateNode(PropertyKey key) {
        var segments = getPathSegments(key);
        var node = root;

        for (var segment : segments) {
            node = node.getOrCreateChild(segment);
        }

        return node;
    }

    /**
     * Gets a node by dot-separated path string, or null if it doesn't exist.
     */
    private @Nullable PropertyNode getNodeByPath(String path) {
        var segments = path.split("\\.");
        var node = root;

        for (var segment : segments) {
            node = node.getChild(segment);

            if (node == null) {
                return null;
            }
        }

        return node;
    }

    /**
     * Gets or creates a node by dot-separated path string.
     */
    private PropertyNode getOrCreateNodeByPath(String path) {
        var segments = path.split("\\.");
        var node = root;

        for (var segment : segments) {
            node = node.getOrCreateChild(segment);
        }

        return node;
    }
}
