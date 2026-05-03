package com.blib.api.client.animation.v1;

import java.util.List;

import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.AzCommandBuilder;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;
import com.blib.api.client.animation.v1.track.AzTrackHandle;

public class AzAnimationUtil {

    /**
     * Composes a single command that broadcasts an animation across multiple track handles, one
     * action per handle. The {@code <T>} parameter is the animatable type — all handles must agree
     * on it, enforced by the type system.
     */
    public static <T> AzCommand<T> compose(
        List<AzTrackHandle<T>> handles,
        String baseName,
        AzPlayBehavior playBehavior,
        AzDispatchMode dispatchMode
    ) {
        return AzCommand.compose(
            handles.stream()
                .map(handle -> AzAnimationUtil.<T>builderFor(dispatchMode)
                    .play(handle, baseName + "." + handle.name(), playBehavior)
                    .build())
                .toList()
        );
    }

    private static <T> AzCommandBuilder<T> builderFor(AzDispatchMode mode) {
        return switch (mode) {
            case REPLAY -> AzCommand.replay();
            case PLAY_IF_NOT_PLAYING -> AzCommand.idempotent();
            case ENQUEUE -> AzCommand.enqueueing();
        };
    }
}
