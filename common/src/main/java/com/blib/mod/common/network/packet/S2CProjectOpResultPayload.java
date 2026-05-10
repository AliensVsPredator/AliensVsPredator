package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Server → client: result of a project-management action (create / delete / open / reload). Carries the originating
 * {@link ProjectOp} as an int ordinal, a success flag, the affected project name, and a user-facing error message when
 * {@code !success}. The picker / FILE menu use this to display toasts and gate transitions (e.g. only switching to the
 * workspace screen on a successful {@code OPEN}).
 * <p>
 * {@code projectName} echoes the request's project so the client can correlate replies even if multiple are in-flight.
 * Empty {@code errorMessage} on success.
 */
public record S2CProjectOpResultPayload(
    int opOrdinal,
    boolean success,
    String projectName,
    String errorMessage
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("project_op_result");

    public static final Type<S2CProjectOpResultPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CProjectOpResultPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        S2CProjectOpResultPayload::opOrdinal,
        StreamCodecs.BOOLEAN,
        S2CProjectOpResultPayload::success,
        StreamCodecs.STRING_UTF8,
        S2CProjectOpResultPayload::projectName,
        StreamCodecs.STRING_UTF8,
        S2CProjectOpResultPayload::errorMessage,
        S2CProjectOpResultPayload::new
    );

    public ProjectOp op() {
        return ProjectOp.fromOrdinal(opOrdinal);
    }

    public static S2CProjectOpResultPayload success(ProjectOp op, String projectName) {
        return success(op, projectName, "");
    }

    /**
     * Success variant that carries a non-empty message — e.g. the capture engine's "Saved jigsaw capture (6×1×3 = 18
     * pieces)" summary. The {@code errorMessage} field is named historically; it carries any user-facing text on either
     * success or failure.
     */
    public static S2CProjectOpResultPayload success(ProjectOp op, String projectName, String message) {
        return new S2CProjectOpResultPayload(op.ordinal(), true, projectName, message == null ? "" : message);
    }

    public static S2CProjectOpResultPayload failure(ProjectOp op, String projectName, String errorMessage) {
        return new S2CProjectOpResultPayload(op.ordinal(), false, projectName, errorMessage == null ? "" : errorMessage);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
