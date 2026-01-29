package com.blib.azurelib.common.animation.dispatch;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum AzDispatchSide implements StringRepresentable {

    CLIENT(0),
    SERVER(1);

    private static final Map<Integer, AzDispatchSide> ID_TO_ENUM_MAP = new Int2ObjectArrayMap<>();

    static {
        // Populate the map for a quick lookup
        for (AzDispatchSide side : values()) {
            ID_TO_ENUM_MAP.put(side.id, side);
        }
    }

    private final int id;

    AzDispatchSide(int id) {
        this.id = id;
    }

    public static final StreamCodec<FriendlyByteBuf, AzDispatchSide> CODEC = StreamCodec.of(
        (buf, val) -> buf.writeByte(val.id),
        buf -> ID_TO_ENUM_MAP.get((int) buf.readByte())
    );

    @Override
    public @NotNull String getSerializedName() {
        return name();
    }
}
