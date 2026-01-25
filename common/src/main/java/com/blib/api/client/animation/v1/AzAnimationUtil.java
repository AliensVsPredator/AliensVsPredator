package com.blib.api.client.animation.v1;

import java.util.List;

import com.blib.azurelib.common.animation.dispatch.command.AzCommand;
import com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior;
import com.blib.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class AzAnimationUtil {

    public static AzCommand compose(List<String> limbNames, String baseName) {
        return compose(limbNames, baseName, AzPlayBehaviors.LOOP);
    }

    public static AzCommand compose(List<String> limbNames, String baseName, AzPlayBehavior playBehavior) {
        return AzCommand.compose(
            limbNames.stream()
                .map(limbName -> AzCommand.create(limbName, baseName + "." + limbName, playBehavior))
                .toList()
        );
    }
}
