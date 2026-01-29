package com.blib.azurelib.common.animation.controller.keyframe;

import org.jetbrains.annotations.Nullable;

import com.blib.azurelib.common.animation.controller.keyframe.handler.AzCustomKeyframeHandler;
import com.blib.azurelib.common.animation.controller.keyframe.handler.AzParticleKeyframeHandler;
import com.blib.azurelib.common.animation.controller.keyframe.handler.AzSoundKeyframeHandler;

public class AzKeyframeCallbacks<T> {

    private static final AzKeyframeCallbacks<?> NO_OP = new AzKeyframeCallbacks<>(null, null, null);

    @SuppressWarnings("unchecked")
    public static <T> AzKeyframeCallbacks<T> noop() {
        return (AzKeyframeCallbacks<T>) NO_OP;
    }

    private final @Nullable AzCustomKeyframeHandler<T> customKeyframeHandler;

    private final @Nullable AzParticleKeyframeHandler<T> particleKeyframeHandler;

    private final @Nullable AzSoundKeyframeHandler<T> soundKeyframeHandler;

    private AzKeyframeCallbacks(
        @Nullable AzCustomKeyframeHandler<T> customKeyframeHandler,
        @Nullable AzParticleKeyframeHandler<T> particleKeyframeHandler,
        @Nullable AzSoundKeyframeHandler<T> soundKeyframeHandler
    ) {
        this.customKeyframeHandler = customKeyframeHandler;
        this.particleKeyframeHandler = particleKeyframeHandler;
        this.soundKeyframeHandler = soundKeyframeHandler;
    }

    public @Nullable AzCustomKeyframeHandler<T> customKeyframeHandler() {
        return customKeyframeHandler;
    }

    public @Nullable AzParticleKeyframeHandler<T> particleKeyframeHandler() {
        return particleKeyframeHandler;
    }

    public @Nullable AzSoundKeyframeHandler<T> soundKeyframeHandler() {
        return soundKeyframeHandler;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private @Nullable AzCustomKeyframeHandler<T> customKeyframeHandler;

        private @Nullable AzParticleKeyframeHandler<T> particleKeyframeHandler;

        private @Nullable AzSoundKeyframeHandler<T> soundKeyframeHandler;

        private Builder() {}

        public Builder<T> setSoundKeyframeHandler(AzSoundKeyframeHandler<T> soundHandler) {
            this.soundKeyframeHandler = soundHandler;
            return this;
        }

        public Builder<T> setParticleKeyframeHandler(AzParticleKeyframeHandler<T> particleHandler) {
            this.particleKeyframeHandler = particleHandler;
            return this;
        }

        public Builder<T> setCustomInstructionKeyframeHandler(AzCustomKeyframeHandler<T> customInstructionHandler) {
            this.customKeyframeHandler = customInstructionHandler;
            return this;
        }

        public AzKeyframeCallbacks<T> build() {
            return new AzKeyframeCallbacks<>(customKeyframeHandler, particleKeyframeHandler, soundKeyframeHandler);
        }
    }
}
