package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.internal.common.storage.ProjectInfo;
import com.blib.mod.BLib;

/**
 * Server → client: list of BLib projects in the current world. Sent in response to {@link C2SListProjectsPayload} and
 * proactively after every successful create / delete so the picker UI refreshes without re-requesting. Sorted by
 * project name on the server side.
 */
public record S2CProjectListPayload(List<ProjectInfo> projects) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("project_list");

    public static final Type<S2CProjectListPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CProjectListPayload> CODEC = RecordStreamCodec.of(
        ProjectInfo.STREAM_CODEC.asList(),
        S2CProjectListPayload::projects,
        S2CProjectListPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
