package com.blib.engine.modeler.animation;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.ModelerBone;

@ApiStatus.Internal
public final class AnimationFrameUpdate {

    private static long lastFrameToken = Long.MIN_VALUE;

    private AnimationFrameUpdate() {}

    public static void update(@Nullable ModelerBone root, AnimationEditorState state) {
        var frameToken = Minecraft.getInstance().getFrameTimeNs();
        if (frameToken == lastFrameToken) {
            return;
        }
        lastFrameToken = frameToken;
        state.updatePlaybackClock();
        if (root != null) {
            AnimationCollisionState.get().refresh(root, state);
        }
    }
}
