package com.blib.internal.client.render.item.config;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import com.blib.api.client.render.v1.item.BLibItemTransforms;

/**
 * Post-merge view of a {@link RawItemRendererConfig} ready for the renderer to consume. {@code model}, {@code texture},
 * and {@code boneName} are guaranteed non-null — the loader skips any entry whose parent chain doesn't supply them and
 * logs a warning, so any entry present in {@link BLibItemRendererConfigs} is structurally complete.
 *
 * @param idleTransforms     Always non-null but possibly an empty (identity) set if no idle transforms were authored
 *                           anywhere in the chain.
 * @param blockingTransforms Non-null only when the chain explicitly authored a blocking set somewhere — null means "use
 *                           idle regardless of blocking state," same semantic as {@code BLibGeoBoneItemRendererConfig}.
 */
public record ResolvedItemRendererConfig(
    ResourceLocation model,
    ResourceLocation texture,
    String boneName,
    BLibItemTransforms idleTransforms,
    @Nullable BLibItemTransforms blockingTransforms
) {}
