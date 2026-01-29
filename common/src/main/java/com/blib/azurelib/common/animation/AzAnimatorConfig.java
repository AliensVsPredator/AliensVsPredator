package com.blib.azurelib.common.animation;

public record AzAnimatorConfig(
    double boneResetTime,
    boolean crashIfBoneMissing,
    boolean shouldPlayAnimationsWhileGamePaused
) {

    public static Builder builder() {
        return new Builder();
    }

    public static AzAnimatorConfig defaultConfig() {
        return builder().build();
    }

    public static class Builder {

        private double boneResetTime;

        private boolean crashIfBoneMissing;

        private boolean shouldPlayAnimationsWhileGamePaused;

        private Builder() {
            this.boneResetTime = 1;
            this.crashIfBoneMissing = false;
            this.shouldPlayAnimationsWhileGamePaused = false;
        }

        public Builder crashIfBoneMissing() {
            this.crashIfBoneMissing = true;
            return this;
        }

        public Builder shouldPlayAnimationsWhileGamePaused() {
            this.shouldPlayAnimationsWhileGamePaused = true;
            return this;
        }

        public Builder withBoneResetTime(double boneResetTime) {
            this.boneResetTime = boneResetTime;
            return this;
        }

        public AzAnimatorConfig build() {
            return new AzAnimatorConfig(
                boneResetTime,
                crashIfBoneMissing,
                shouldPlayAnimationsWhileGamePaused
            );
        }
    }
}
