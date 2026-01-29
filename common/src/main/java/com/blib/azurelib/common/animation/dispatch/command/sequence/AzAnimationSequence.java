package com.blib.azurelib.common.animation.dispatch.command.sequence;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

import com.blib.azurelib.common.animation.dispatch.command.stage.AzAnimationStage;
import com.blib.azurelib.common.util.codec.AzListStreamCodec;

public record AzAnimationSequence(
    List<AzAnimationStage> stages
) {

    public static final StreamCodec<FriendlyByteBuf, AzAnimationSequence> CODEC = StreamCodec.composite(
        new AzListStreamCodec<>(AzAnimationStage.CODEC),
        AzAnimationSequence::stages,
        AzAnimationSequence::new
    );
}
