package com.avp.core.common.util.resin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ResinData implements ReadableResinData {

    public static final Codec<ResinData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("resin").forGetter(ResinData::resin),
            Codec.INT.fieldOf("resinMax").forGetter(ResinData::resinMax),
            Codec.INT.fieldOf("resinPerTick").forGetter(ResinData::resinPerTick),
            Codec.INT.fieldOf("tickRate").forGetter(ResinData::tickRate)
        )
            .apply(instance, ResinData::new)
    );

    private int resin;

    private int resinMax;

    private int resinPerTick;

    private int tickRate;

    public ResinData(int initialResin, int resinMax, int resinPerTick, int tickRate) {
        this.resin = initialResin;
        this.resinMax = resinMax;
        this.resinPerTick = resinPerTick;
        this.tickRate = tickRate;
    }

    public void addResin(int resin) {
        setResin(this.resin + resin);
    }

    public void setResin(int resin) {
        this.resin = Math.clamp(resin, 0, resinMax);
    }

    public void setResinMax(int resinMax) {
        this.resinMax = Math.max(0, resinMax);
    }

    @Override
    public int resin() {
        return resin;
    }

    @Override
    public int resinMax() {
        return resinMax;
    }

    @Override
    public int resinPerTick() {
        return resinPerTick;
    }

    @Override
    public int tickRate() {
        return tickRate;
    }
}
