package com.avp.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public record C2SPlayerToggleCrawlPayload(
    boolean shouldCrawl
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("player_toggle_crawl");

    public static final Type<C2SPlayerToggleCrawlPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<FriendlyByteBuf, C2SPlayerToggleCrawlPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        C2SPlayerToggleCrawlPayload::shouldCrawl,
        C2SPlayerToggleCrawlPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
