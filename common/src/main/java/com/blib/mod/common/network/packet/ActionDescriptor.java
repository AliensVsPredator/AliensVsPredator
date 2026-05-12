package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.codec.v1.BLibCodecs;

/**
 * Client-facing summary of one {@code EditorAction}. The server flattens its undo+redo stacks to a list of these and
 * ships them in {@link S2CActionHistorySyncPayload} so the Action Stack panel can render rows without needing the full
 * server-side state. Carries only display data; reverting/redoing always goes through the server.
 *
 * @param typeId      Stable kind id (e.g. {@code "block_region"}). Drives panel icon/grouping.
 * @param description Human-readable label. e.g. {@code "Place jigsaw piece blib:foo"}.
 * @param timestamp   Wall-clock epoch millis when the action was pushed.
 * @param dim         Dimension id for world actions; {@code null} for dimension-agnostic project actions.
 * @param projectName Project name for project actions; {@code null} for world actions.
 */
public record ActionDescriptor(
    String typeId,
    String description,
    long timestamp,
    @Nullable ResourceLocation dim,
    @Nullable String projectName
) {

    public static final StreamCodec<ActionDescriptor> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        ActionDescriptor::typeId,
        StreamCodecs.STRING_UTF8,
        ActionDescriptor::description,
        StreamCodecs.LONG,
        ActionDescriptor::timestamp,
        BLibCodecs.Stream.RESOURCE_LOCATION.asOptional(),
        ActionDescriptor::dimOptional,
        StreamCodecs.STRING_UTF8.asOptional(),
        ActionDescriptor::projectNameOptional,
        ActionDescriptor::fromOptionals
    );

    public java.util.Optional<ResourceLocation> dimOptional() {
        return java.util.Optional.ofNullable(dim);
    }

    public java.util.Optional<String> projectNameOptional() {
        return java.util.Optional.ofNullable(projectName);
    }

    public static ActionDescriptor fromOptionals(
        String typeId,
        String description,
        long timestamp,
        java.util.Optional<ResourceLocation> dim,
        java.util.Optional<String> projectName
    ) {
        return new ActionDescriptor(typeId, description, timestamp, dim.orElse(null), projectName.orElse(null));
    }
}
