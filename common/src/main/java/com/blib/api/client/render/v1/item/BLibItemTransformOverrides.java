package com.blib.api.client.render.v1.item;

import com.blib.api.client.render.v1.BLibTransform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Process-wide registry of in-memory transform overrides keyed by item id, mode (idle/blocking), and
 * {@link ItemDisplayContext}. Reads are taken every render via {@link BLibTunableItemTransforms#get}, so any
 * override placed here takes effect on the next frame — that's the hook the {@code /blib transform-tune}
 * command uses to live-tweak transforms without restarting the game.
 * <p>
 * Overrides are not persisted across game launches by design — they're a tuning aid, not configuration. The
 * intended workflow is: nudge values until the pose looks right in-game, then run {@code dump} to print the
 * final values formatted as Java code, paste them back into your renderer's {@link BLibItemTransforms}
 * constants, and reset the overrides.
 */
public final class BLibItemTransformOverrides {

    private static final Map<ResourceLocation, EnumMap<BLibItemTransformMode, EnumMap<ItemDisplayContext, BLibTransform>>> OVERRIDES = new HashMap<>();

    private static final Map<ResourceLocation, EnumMap<BLibItemTransformMode, BLibItemTransforms>> BASES = new HashMap<>();

    /**
     * When true, the geo-bone item renderer draws a small wireframe AABB and a colored axis tripod at the
     * pivot location each frame. Used by the tuner's {@code debug pivot} subcommand so the user can see
     * where rotation/scale is anchored and which direction each rotation axis currently points. Volatile
     * because the toggle is flipped from the server thread (command source) and read from the client render
     * thread; both run in the same JVM in single-player.
     */
    private static volatile boolean PIVOT_VISUALIZATION_ENABLED = false;

    /**
     * When true, the geo-bone item renderer treats every render as if the holder were actively using the
     * item (e.g., raising the shield), so the {@link BLibItemTransformMode#BLOCKING} transforms are picked
     * regardless of the holder's actual use state. Lets users tune the blocking pose with gizmos without
     * needing to physically hold RMB the whole time.
     */
    private static volatile boolean FORCE_BLOCKING_ENABLED = false;

    private BLibItemTransformOverrides() {
        throw new UnsupportedOperationException();
    }

    public static boolean isPivotVisualizationEnabled() {
        return PIVOT_VISUALIZATION_ENABLED;
    }

    public static void setPivotVisualizationEnabled(boolean enabled) {
        PIVOT_VISUALIZATION_ENABLED = enabled;
    }

    public static boolean isForceBlockingEnabled() {
        return FORCE_BLOCKING_ENABLED;
    }

    public static void setForceBlockingEnabled(boolean enabled) {
        FORCE_BLOCKING_ENABLED = enabled;
    }

    /**
     * Records the base transforms a {@link BLibTunableItemTransforms} wraps so the tuner command can read
     * the current effective value (override or base) for {@code nudge} operations. Called automatically by
     * {@link BLibTunableItemTransforms#wrap}.
     */
    public static void registerBase(ResourceLocation itemId, BLibItemTransformMode mode, BLibItemTransforms base) {
        BASES.computeIfAbsent(itemId, $ -> new EnumMap<>(BLibItemTransformMode.class)).put(mode, base);
    }

    /**
     * Returns the current effective transform — override if set, else base if registered. Returns null when
     * neither is set for this exact mode/context (without cascading through other modes). Used by
     * {@link #getEffective} and by the tuner command's {@code dump} so it can emit only what was explicitly
     * set per mode.
     */
    public static @Nullable BLibTransform getModeValueOrNull(ResourceLocation itemId, BLibItemTransformMode mode, ItemDisplayContext context) {
        var override = get(itemId, mode, context);

        if (override != null) {
            return override;
        }

        var modeBases = BASES.get(itemId);

        if (modeBases == null) {
            return null;
        }

        var base = modeBases.get(mode);

        if (base == null) {
            return null;
        }

        return base.getOrNull(context);
    }

    /**
     * Returns the current effective transform — what the renderer would apply right now. Cascades through
     * idle for {@link BLibItemTransformMode#BLOCKING} when blocking has nothing for the requested context,
     * matching the renderer's per-context override semantics. Falls through to {@link BLibTransform#IDENTITY}
     * when neither mode has anything to say.
     * <p>
     * Used by the tuner's {@code nudge} subcommand so deltas apply on top of what the player sees.
     */
    public static BLibTransform getEffective(ResourceLocation itemId, BLibItemTransformMode mode, ItemDisplayContext context) {
        var primary = getModeValueOrNull(itemId, mode, context);

        if (primary != null) {
            return primary;
        }


        if (mode == BLibItemTransformMode.BLOCKING) {
            var idleValue = getModeValueOrNull(itemId, BLibItemTransformMode.IDLE, context);

            if (idleValue != null) {
                return idleValue;
            }
        }

        return BLibTransform.IDENTITY;
    }

    /** Items registered as tunable via {@link BLibTunableItemTransforms#wrap}. Used by command autocomplete. */
    public static Set<ResourceLocation> tunableItemIds() {
        return Set.copyOf(BASES.keySet());
    }

    /**
     * Returns the overridden transform for this (item, mode, context) tuple, or {@code null} if no override
     * has been set — caller falls back to the base transforms.
     */
    public static @Nullable BLibTransform get(ResourceLocation itemId, BLibItemTransformMode mode, ItemDisplayContext context) {
        var modeMap = OVERRIDES.get(itemId);

        if (modeMap == null) {
            return null;
        }

        var contextMap = modeMap.get(mode);

        if (contextMap == null) {
            return null;
        }

        return contextMap.get(context);
    }

    public static void set(ResourceLocation itemId, BLibItemTransformMode mode, ItemDisplayContext context, BLibTransform transform) {
        OVERRIDES.computeIfAbsent(itemId, $ -> new EnumMap<>(BLibItemTransformMode.class))
            .computeIfAbsent(mode, $ -> new EnumMap<>(ItemDisplayContext.class))
            .put(context, transform);
    }

    /** Returns the live override map for the given (item, mode), or an empty map if none is set. Read-only view. */
    public static Map<ItemDisplayContext, BLibTransform> snapshot(ResourceLocation itemId, BLibItemTransformMode mode) {
        var modeMap = OVERRIDES.get(itemId);

        if (modeMap == null) {
            return Map.of();
        }

        var contextMap = modeMap.get(mode);

        if (contextMap == null) {
            return Map.of();
        }

        return Map.copyOf(contextMap);
    }

    public static void clear(ResourceLocation itemId, BLibItemTransformMode mode) {
        var modeMap = OVERRIDES.get(itemId);

        if (modeMap != null) {
            modeMap.remove(mode);
        }

    }

    public static void clearAll(ResourceLocation itemId) {
        OVERRIDES.remove(itemId);
    }

    /** Items with at least one override in any mode. Used by command autocomplete. */
    public static Set<ResourceLocation> registeredItemIds() {
        return Set.copyOf(OVERRIDES.keySet());
    }
}
