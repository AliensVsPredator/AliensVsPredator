package com.blib.api.client.animation.v1;

import java.util.List;

import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.AzTrackCommandBuilder;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;

public class AzAnimationUtil {

    public static AzCommand compose(
        List<String> limbNames,
        String baseName,
        AzPlayBehavior playBehavior,
        AzDispatchMode dispatchMode
    ) {
        return AzCommand.compose(
            limbNames.stream()
                .map(limbName -> builderFor(dispatchMode)
                    .play(limbName, baseName + "." + limbName, playBehavior)
                    .build())
                .toList()
        );
    }

    private static AzTrackCommandBuilder builderFor(AzDispatchMode mode) {
        return switch (mode) {
            case REPLAY -> AzCommand.replay();
            case PLAY_IF_NOT_PLAYING -> AzCommand.idempotent();
            case ENQUEUE -> AzCommand.enqueueing();
        };
    }
}
