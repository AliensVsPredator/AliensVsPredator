package com.avp.common.network.packet;

import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.avp.AVPResources;

public record S2CSyncGenesPayload(
    int entityId,
    List<GeneBonusDataEntry> geneBonusDataEntries
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("sync_genes");

    public static final Type<S2CSyncGenesPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<FriendlyByteBuf, S2CSyncGenesPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        S2CSyncGenesPayload::entityId,
        ByteBufCodecs.collection(ArrayList::new, GeneBonusDataEntry.STREAM_CODEC),
        S2CSyncGenesPayload::geneBonusDataEntries,
        S2CSyncGenesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
