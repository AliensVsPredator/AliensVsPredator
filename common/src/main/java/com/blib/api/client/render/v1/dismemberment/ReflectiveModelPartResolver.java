package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves {@link ModelPart}s on vanilla model classes that don't expose their root publicly. Most non-{@code
 * HierarchicalModel} models (wolf/horse/fox/chicken/rabbit and friends) take a {@code ModelPart root} in their
 * constructor and store individual children directly into private final fields — the constructor argument itself isn't
 * kept around, so there's nothing to walk. Vanilla's convention is that each named part in the {@code LayerDefinition}
 * (e.g. {@code right_hind_leg}) is stored in a camelCase field of the same meaning ({@code rightHindLeg}); this
 * resolver exploits that convention via reflection with per-class caching, so the hot path is a single map lookup once
 * warm.
 * <p>
 * Use {@link #resolverFor(Class)} to plug a {@code (model, partName) -> ModelPart} resolver into
 * {@link ModelPartResolverRegistry} for any model class whose private fields follow the convention.
 */
public final class ReflectiveModelPartResolver {

    private static final Map<Class<?>, Map<String, Field>> FIELDS_BY_CLASS = new ConcurrentHashMap<>();

    private ReflectiveModelPartResolver() {}

    /**
     * Returns a resolver that, given a model instance of {@code modelClass} and a snake_case part name, returns the
     * corresponding ModelPart from the matching private field — or {@code null} if no such field exists.
     */
    public static <M extends EntityModel<?>> ModelPartResolver<M> resolverFor(Class<M> modelClass) {
        return (model, partName) -> resolve(modelClass, model, partName);
    }

    private static @Nullable ModelPart resolve(Class<?> modelClass, Object model, String snakeCaseName) {
        var fields = FIELDS_BY_CLASS.computeIfAbsent(modelClass, ReflectiveModelPartResolver::buildFieldMap);
        var field = fields.get(snakeToCamel(snakeCaseName));

        if (field == null) {
            return null;
        }

        try {
            return (ModelPart) field.get(model);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static Map<String, Field> buildFieldMap(Class<?> clazz) {
        var result = new HashMap<String, Field>();

        // Walk the class hierarchy so subclasses (e.g. ChestedHorseModel) can resolve fields declared on superclasses
        // (HorseModel). Fields declared lower in the chain win on name conflicts.
        for (var c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (var field : c.getDeclaredFields()) {
                if (!ModelPart.class.isAssignableFrom(field.getType())) {
                    continue;
                }

                if (result.containsKey(field.getName())) {
                    continue;
                }

                field.setAccessible(true);
                result.put(field.getName(), field);
            }
        }

        return result;
    }

    private static String snakeToCamel(String snake) {
        var underscore = snake.indexOf('_');

        if (underscore < 0) {
            return snake;
        }

        var sb = new StringBuilder(snake.length());
        var capitalizeNext = false;

        for (var i = 0; i < snake.length(); i++) {
            var c = snake.charAt(i);

            if (c == '_') {
                capitalizeNext = true;
                continue;
            }

            sb.append(capitalizeNext ? Character.toUpperCase(c) : c);
            capitalizeNext = false;
        }

        return sb.toString();
    }
}
